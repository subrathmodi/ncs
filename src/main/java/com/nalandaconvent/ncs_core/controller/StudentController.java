package com.nalandaconvent.ncs_core.controller;

import com.nalandaconvent.ncs_core.dto.AdmissionRequest;
import com.nalandaconvent.ncs_core.entity.Student;
import com.nalandaconvent.ncs_core.entity.StudentSessionMapping;
import com.nalandaconvent.ncs_core.repository.StudentRepository;
import com.nalandaconvent.ncs_core.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentService studentService;

    // POST Endpoint: Handle New Admission Form Submissions
    @PostMapping("/admission")
    public ResponseEntity<?> registerNewStudent(@RequestBody AdmissionRequest request) {
        try {
            Student registeredStudent = studentService.processAdmission(request);
            return ResponseEntity.ok(Map.of(
                    "message", "Admission successful!",
                    "admissionNumber", registeredStudent.getStudentId() // Fixed to match your entity property
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "An error occurred while processing admission."));
        }
    }

    // GET Endpoint: Retrieve a filtered cluster matching specific parameters
    @GetMapping("/directory")
    public ResponseEntity<List<StudentSessionMapping>> fetchDirectoryCluster(
            @RequestParam Long sessionId,
            @RequestParam String currentClass) {

        List<StudentSessionMapping> directoryCluster = studentService.getClusterDirectory(sessionId, currentClass);
        return ResponseEntity.ok(directoryCluster);
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateStudentProfile(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            // 1. Fetch the existing student master record from the database vault
            Student student = studentRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Student profile record not found."));

            // 2. Extract and bind the personal profile updates from the frontend form payload securely
            student.setName((String) payload.get("name"));
            student.setFatherName((String) payload.get("fatherName"));
            student.setMotherName((String) payload.get("motherName"));
            student.setFatherContactNo((String) payload.get("fatherContactNo"));

            // Parse the HTML date string safely into a Java LocalDate type
            if (payload.get("dateOfBirth") != null && !((String) payload.get("dateOfBirth")).isEmpty()) {
                student.setDateOfBirth(java.time.LocalDate.parse((String) payload.get("dateOfBirth")));
            }

            student.setGender((String) payload.get("gender"));
            student.setReligion((String) payload.get("religion"));
            student.setCategory((String) payload.get("category"));
            student.setStudentAadharNo((String) payload.get("studentAadharNo"));

            // 3. Extract and bind the government registry tracking indicators
            student.setStudentSssmidNo((String) payload.get("studentSssmidNo"));
            student.setChildIdNo((String) payload.get("childIdNo"));
            student.setFamilyIdNo((String) payload.get("familyIdNo"));
            student.setPenNo((String) payload.get("penNo"));
            student.setApaarIdNo((String) payload.get("apaarIdNo"));

            // 4. Extract and bind the residential tracking parameters
            student.setFullAddress((String) payload.get("fullAddress"));
            student.setBankAccountNo((String) payload.get("bankAccountNo"));
            student.setIfscCode((String) payload.get("ifscCode"));
            student.setNameOfBank((String) payload.get("nameOfBank"));

            // 5. Commit changes back to PostgreSQL
            studentRepository.save(student);

            return ResponseEntity.ok(Map.of("message", "Student profile synchronized and updated successfully."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "An internal error occurred while updating the profile ledger."));
        }
    }
}