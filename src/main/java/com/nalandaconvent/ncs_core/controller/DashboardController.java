package com.nalandaconvent.ncs_core.controller;

import com.nalandaconvent.ncs_core.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;

@Controller
public class DashboardController {

    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Resolves to templates/login.html
    }

    @GetMapping("/dashboard")
    public String showDashboard(Authentication authentication) {
        if (authentication != null) {
            // Loop through roles assigned by JwtAuthenticationFilter
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                String role = authority.getAuthority();

                if ("ROLE_ADMIN".equals(role)) {
                    return "dashboard/admin"; // Resolves to templates/dashboard/admin.html
                } else if ("ROLE_OPERATOR".equals(role)) {
                    return "dashboard/operator"; // Resolves to templates/dashboard/operator.html
                }
            }
        }
        // Fallback safety measure if no explicit role is parsed
        return "redirect:/login";
    }

//    @GetMapping("/dashboard/student/admission")
//    public String showAdmissionForm() {
//        return "student/admission-form"; // Resolves template page location
//    }
//    @GetMapping("/dashboard/student/directory")
//    public String showStudentDirectory() {
//        return "student/directory"; // Resolves template view path layout
//    }

    @GetMapping("/dashboard/students")
    public String manageStudents() {
        return "student/manage-students"; // Resolves to the new combined template
    }


    @GetMapping("/dashboard/academics")
    public String showAcademicsWorkspace() {
        return "academics/academics"; // Maps to the combined workspace template layout
    }

    @GetMapping("/dashboard/evaluation")
    public String showEvaluationGrid() {
        return "academics/evaluation-grid"; // Resolves template view file path
    }

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/dashboard/metrics")
    public ResponseEntity<?> getSummaryMetrics() {
        try {
            // 1. Calculate active totals
            long totalEnrolled = studentRepository.countActiveEnrolledStudents();

            // 2. Compute date windows for the current month
            LocalDate today = LocalDate.now();
            LocalDate startOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
            LocalDate endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
            long recentAdmissions = studentRepository.countRecentAdmissions(startOfMonth, endOfMonth);

            // 3. Return clean JSON data payload
            return ResponseEntity.ok(

                    Map.of(
                    "totalEnrolled", totalEnrolled,
                    "recentAdmissions", recentAdmissions
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to compile summary metrics."));
        }
    }
}