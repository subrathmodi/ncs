package com.nalandaconvent.ncs_core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
                .authorizeHttpRequests(auth -> auth
                        // 1. Static resources and public endpoints accessible to everyone
                        .requestMatchers("/login", "/api/auth/login", "/css/**", "/js/**", "/images/**").permitAll()

                        // 2. Strict Admin-only paths
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/dashboard/admin.html", "/dashboard/admin/**").hasRole("ADMIN")

                        // 3. Shared Operations (Admins and Operators)
                        .requestMatchers("/dashboard/operator.html", "/dashboard/operator/**").hasAnyRole("ADMIN", "OPERATOR")

                        // BROAD MATCH ROOT: Captures both form, standard print, and by-mapping route links
                        .requestMatchers("/dashboard/academics/tc/**").hasAnyRole("ADMIN", "OPERATOR")
                        .requestMatchers("/dashboard/academics/**").hasAnyRole("ADMIN", "OPERATOR")
                        .requestMatchers("/api/academics/**").hasAnyRole("ADMIN", "OPERATOR")

                        .requestMatchers("/api/students/update/**").hasAnyRole("ADMIN", "OPERATOR")
                        // 4. Global fallback access rules for authenticated users
                        .requestMatchers("/dashboard", "/logout").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}