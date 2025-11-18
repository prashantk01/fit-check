package com.fitcheck.fit_check.security;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fitcheck.fit_check.dto.user.UserResponse;
import com.fitcheck.fit_check.service.UserService;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtService jwtService;
    private final UserService userService;

    public JwtAuthFilter(JwtService jwtService, @Lazy UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
        System.out.println("=".repeat(80));
        System.out.println(">>> JwtAuthFilter CONSTRUCTOR CALLED");
        System.out.println("=".repeat(80));
    }

    // Define which endpoints are public
    private boolean isPublicEndpoint(String uri) {
        return uri.matches("^/api/auth/.*")
                || uri.equals("/api/auth/login")
                || uri.equals("/api/auth/register")
                || uri.startsWith("/api/v1/health")
                || uri.startsWith("/swagger-ui")
                || uri.startsWith("/v3/api-docs");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        System.out.println(">>> FILTER URI = " + uri);

        String header = request.getHeader("Authorization");

        // Case 1: Public endpoint
        if (isPublicEndpoint(uri)) {
            System.out.println(">>> PUBLIC ENDPOINT - CONTINUING WITHOUT AUTH");
            filterChain.doFilter(request, response);
            return;
        }

        // Case 2: No token present
        if (header == null || !header.startsWith("Bearer ")) {
            logger.warn("Missing or invalid Authorization header for protected endpoint: {}", uri);
            // throw new JwtException("Missing or invalid Authorization header");
            unauthorizedAccess(response, " Missing or invalid Authorization header");
            return;
        }

        // Case 3: Token present — validate it
        String token = header.substring(7);
        String username;

        try {
            username = jwtService.extractUsername(token);
        } catch (JwtException e) {
            logger.warn("Invalid JWT: {}", e.getMessage());
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Invalid JWT\"}");
            return;
        }

        if (jwtService.isTokenExpired(token)) {
            logger.warn("Expired or invalid token for user: {}", username);
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Expired or invalid JWT token\"}");
            return;
        }

        if (username == null || username.isBlank()) {
            logger.warn("Token does not contain a valid username");
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Token does not contain a valid username\"}");
            return;
        }

        try {
            // Lookup user details from database
            // TODO: Cache user details to avoid DB hit on every request
            // User model instance not DTO
            UserResponse user = userService.getUserByUsername(username);
            if (user == null || user.username() == null) {
                logger.warn("User not found for token username: {}", username);
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"User not found\"}");
                return;
            }

            List<SimpleGrantedAuthority> authorities = user.role() != null
                    ? user.role().stream()
                            .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                            .toList()
                    : List.of();

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null,
                    authorities);
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);

            logger.info("Authenticated user: {} with roles: {}", username, user.role());
        } catch (Exception e) {
            logger.error("UserService lookup failed: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // Case 4: Valid token — continue normally
        filterChain.doFilter(request, response);
    }

    private void unauthorizedAccess(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        String jsonResponse = String.format("{\"error\": \"%s\"}", message);
        response.getWriter().write(jsonResponse);
    }
}