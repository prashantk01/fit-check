package com.fitcheck.fit_check.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.fitcheck.fit_check.dto.user.UserResponse;

@Component
public class SecurityUtil {
    public Authentication getAuth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public String getCurrentUsername() {
        Authentication auth = getAuth();
        if (!isAuthenticated()) {
            return null;
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof UserResponse user) {
            return user.username();

        }
        return auth.getName();
    }

    public boolean isAuthenticated() {
        Authentication auth = getAuth();
        return auth != null && auth.isAuthenticated();
    }

    public boolean hasRole(String role) {
        Authentication auth = getAuth();
        if (auth == null)
            return false;
        return auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_" + role));
    }

    public String getCurrentUserId() {
        Authentication auth = getAuth();
        if (!isAuthenticated()) {
            return null;
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof UserResponse user) {
            return user.id();
        }
        return auth.getPrincipal().toString();
    }

}
