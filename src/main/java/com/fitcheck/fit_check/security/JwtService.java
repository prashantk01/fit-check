package com.fithcheck.fit_check.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secretKey}") // Inject the secret key from application properties .env
    private String secretKey;
    @Value("${jwt.expirationMs}") // Inject the expiration time from application properties .env
    private long expirationTimeMs;

    // Method to generate JWT token with roles included in the claims
    public String generateToken(String username, List<String> roles) {
        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("roles", roles); // Add roles to the token claims
        return Jwts.builder()
                .setSubject(username)
                .addClaims(roleMap) // Add roles to the token claims
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTimeMs))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // validate and extract claims from the token
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }

    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("roles", List.class);
    }

    public boolean isTokenValid(String token, String username) {
        final String tokenUsername = extractUsername(token);
        return (tokenUsername.equals(username) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
}