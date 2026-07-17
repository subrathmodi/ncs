package com.nalandaconvent.ncs_core.controller;

import com.nalandaconvent.ncs_core.entity.User;
import com.nalandaconvent.ncs_core.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/operators")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<List<User>> getAllOperators() {
        return ResponseEntity.ok(userRepository.findByRole("OPERATOR"));
    }

    @PostMapping
    public ResponseEntity<?> createOperator(@RequestBody Map<String, String> request) {
        String username = request.get("username").trim();
        String password = request.get("password").trim();

        if (userRepository.existsByUsername(username)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username is already taken!"));
        }

        User newOperator = new User();
        newOperator.setUsername(username);
        newOperator.setPlainTextPassword(password); // Save plain text for admin visibility
        newOperator.setPassword(passwordEncoder.encode(password));
        newOperator.setRole("OPERATOR");

        userRepository.save(newOperator);
        return ResponseEntity.ok(Map.of("message", "Operator account created successfully!"));
    }

    @PutMapping("/{id}/change-password")
    public ResponseEntity<?> changePassword(@PathVariable Long id, @RequestBody Map<String, String> request) {
        if (!request.containsKey("password") || request.get("password").trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Password content cannot be empty."));
        }

        String newPassword = request.get("password").trim();
        Optional<User> userOpt = userRepository.findById(id);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Operator profile missing."));
        }

        User operator = userOpt.get();
        operator.setPlainTextPassword(newPassword); // Update visible plain-text
        operator.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(operator);

        return ResponseEntity.ok(Map.of("message", "Password modified successfully!"));
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOperator(@PathVariable Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Operator profiles do not exist."));
        }

        // Force atomic state updates to prevent database locks
        userRepository.delete(userOpt.get());
        userRepository.flush();

        return ResponseEntity.ok(Map.of("message", "Operator account wiped successfully."));
    }
}