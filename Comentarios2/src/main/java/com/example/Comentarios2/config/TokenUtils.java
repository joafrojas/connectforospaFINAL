package com.example.Comentarios2.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class TokenUtils {

    public static String extraerTokenDelContexto() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getCredentials() != null) {
            return auth.getCredentials().toString();
        }
        return null;
    }
}