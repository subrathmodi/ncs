package com.nalandaconvent.ncs_core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean(name = "smsPasswordEncoder")
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                // Ensure stateless management so Spring relies entirely on your HTTP-Only JWT Cookie
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. Root landing, login endpoints, and all static asset structures are fully open
                        .requestMatchers("/", "/login", "/api/auth/login", "/api/auth/logout").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()

                        // 2. Strict Admin-only paths
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/dashboard/admin.html", "/dashboard/admin/**").hasRole("ADMIN")

                        // 3. Shared Operations (Admins and Operators)
                        .requestMatchers("/dashboard/operator.html", "/dashboard/operator/**").hasAnyRole("ADMIN", "OPERATOR")

                        // 4. Academic & Administrative UI Page Modules
                        .requestMatchers("/dashboard", "/dashboard/").hasAnyRole("ADMIN", "OPERATOR")
                        .requestMatchers("/dashboard/academics/**").hasAnyRole("ADMIN", "OPERATOR")

                        // 5. Secure REST API Data Delivery Channels
                        .requestMatchers("/api/academics/**").hasAnyRole("ADMIN", "OPERATOR")
                        .requestMatchers("/api/students/**").hasAnyRole("ADMIN", "OPERATOR")
                        .requestMatchers("/api/dashboard/**").hasAnyRole("ADMIN", "OPERATOR")

                        // 6. Global fallback access rules for authenticated users
                        .requestMatchers("/logout").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}