package com.nalandaconvent.ncs_core.controller;

import com.nalandaconvent.ncs_core.entity.AcademicSession;
import com.nalandaconvent.ncs_core.service.AcademicSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/academics/sessions")
public class AcademicSessionController {

    @Autowired
    private AcademicSessionService sessionService;

    @PostMapping("/create-upcoming")
    public ResponseEntity<?> createUpcoming(@RequestBody Map<String, String> request) {
        try {
            String name = request.get("sessionName");
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Session name string token cannot be empty."));
            }
            AcademicSession session = sessionService.createUpcomingSession(name);
            return ResponseEntity.ok(Map.of("message", "Upcoming session established successfully!", "id", session.getSessionId()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getLocalizedMessage()));
        }
    }

    @PostMapping("/transition-live")
    public ResponseEntity<?> startNewSession() {
        try {
            sessionService.executeSessionTransition();
            return ResponseEntity.ok(Map.of("message", "Session transition executed! Current curriculum configurations locked live."));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getLocalizedMessage()));
        }
    }

    // Append this method to your AcademicSessionController.java
    @GetMapping("/all")
    public ResponseEntity<?> getAllSessions() {
        return ResponseEntity.ok(sessionService.getAllSessionsList());
    }
}
