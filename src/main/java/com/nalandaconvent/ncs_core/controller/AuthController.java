package com.nalandaconvent.ncs_core.controller;

import com.nalandaconvent.ncs_core.entity.User;
import com.nalandaconvent.ncs_core.repository.UserRepository;
import com.nalandaconvent.ncs_core.config.JwtUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @org.springframework.beans.factory.annotation.Qualifier("smsPasswordEncoder")
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;



    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody Map<String, String> loginRequest, HttpServletResponse response) {
        String username = loginRequest.get("username").trim();
        String password = loginRequest.get("password").trim();

        System.out.println("=================== BCrypt Debugging ===================");
        System.out.println(">>> Form Password Raw: [" + password + "] (Length: " + password.length() + ")");

        // Dynamically encrypt the incoming password right now to see what hash it generates
        String dynamicHash = passwordEncoder.encode(password);
        System.out.println(">>> Dynamic Hash Generated Now: [" + dynamicHash + "]");

        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println(">>> DB Password Hash Found:     [" + user.getPassword() + "]");

            // Manual verification attempt using the same encoder instance
            boolean matches = passwordEncoder.matches(password, user.getPassword());
            System.out.println(">>> Does encoder.matches() think they match? " + matches);
            System.out.println("========================================================");

            if (matches) {
                String token = jwtUtils.generateToken(user.getUsername(), user.getRole());
                Cookie jwtCookie = new Cookie("JWT_TOKEN", token);
                jwtCookie.setHttpOnly(true);
                jwtCookie.setPath("/");
                jwtCookie.setMaxAge(86400);
                response.addCookie(jwtCookie);
                return ResponseEntity.ok(Map.of("message", "Authorized successfully", "role", user.getRole()));
            }
        } else {
            System.out.println(">>> User not found in DB.");
            System.out.println("========================================================");
        }

        return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        // Create an identical cookie but set its lifespan to 0 seconds
        Cookie jwtCookie = new Cookie("JWT_TOKEN", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0); // This immediately deletes the cookie from the browser

        response.addCookie(jwtCookie);

        // Redirect back to login screen with a clean state
        return "redirect:/login";
    }

}
