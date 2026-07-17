package com.nalandaconvent.ncs_core.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = null;

        // 1. Try to read from standard Authorization Header first
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            token = headerAuth.substring(7);
        }

        // 2. FALLBACK: Read from HTTP Cookies if header is missing (e.g., standard browser link navigation tabs)
        if (token == null && request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                // Made case-insensitive check to prevent spelling mismatches
                if ("JWT_TOKEN".equalsIgnoreCase(cookie.getName()) || "jwtToken".equalsIgnoreCase(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // If valid, apply authentication details to Spring context
        if (token != null && jwtUtils.validateToken(token)) {
            String username = jwtUtils.getUsernameFromToken(token);
            String role = jwtUtils.getRoleFromToken(token); // e.g., "ADMIN"

            // Prefix role with ROLE_ for Spring Security specifications
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, Collections.singletonList(authority));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}