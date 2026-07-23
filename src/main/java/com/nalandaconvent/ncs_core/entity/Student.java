package com.nalandaconvent.ncs_core.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentId;

    @Column(name = "date_of_admission", nullable = false)
    private LocalDate dateOfAdmission;

    @Column(name = "admission_type", nullable = false, length = 20)
    private String admissionType; // "NEW" or "RENEWAL"

    @Column(name = "admission_scheme", length = 100)
    private String admissionScheme;

    // --- Personal Information ---
    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "father_name", nullable = false, length = 100)
    private String fatherName;

    @Column(name = "mother_name", nullable = false, length = 100)
    private String motherName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false, length = 10)
    private String gender;

    @Column(length = 50)
    private String religion;

    @Column(length = 50)
    private String category; // General, OBC, SC, ST

    @Column(name = "father_contact_no", nullable = false, length = 15)
    private String fatherContactNo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String fullAddress;

    // --- Initial Academic Settings ---
    @Column(name = "class_of_admission", nullable = false, length = 20)
    private String classOfAdmission;

    @Column(length = 20)
    private String medium; // "English" or "Hindi"

    @Column(length = 50)
    private String stream; // Science, Commerce, Arts (for higher classes)

    // --- Government Registry Identifiers (Compulsory Fields) ---
    @Column(name = "student_aadhar_no", unique = true, nullable = false, length = 12)
    private String studentAadharNo;

    @Column(name = "student_sssmid_no", nullable = false, length = 9)
    private String studentSssmidNo;

    @Column(name = "child_id_no", nullable = false, length = 9)
    private String childIdNo;

    @Column(name = "family_id_no", nullable = false, length = 8)
    private String familyIdNo;

    @Column(name = "pen_no", nullable = false, length = 11)
    private String penNo; // Permanent Education Number

    @Column(name = "apaar_id_no", nullable = false, length = 12)
    private String apaarIdNo;

    // --- Bank Framework Details (Compulsory Fields) ---
    @Column(name = "name_of_bank", nullable = false, length = 100)
    private String nameOfBank;

    @Column(name = "bank_account_no", nullable = false, length = 20)
    private String bankAccountNo;

    @Column(name = "ifsc_code", nullable = false, length = 11)
    private String ifscCode;

    // --- Photograph File Location Pointer ---
    @Column(name = "photo_file_path", length = 512)
    private String photoFilePath;

    // --- Lifecycle State Machine Control ---
    @Column(name = "current_status", nullable = false, length = 20)
    private String currentStatus = "ACTIVE"; // ACTIVE, PROMOTED, TC_ISSUED

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}