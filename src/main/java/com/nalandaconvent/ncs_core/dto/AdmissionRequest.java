package com.nalandaconvent.ncs_core.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AdmissionRequest {
    private String schoolRegNo;
    private LocalDate dateOfAdmission;
    private String admissionType;
    private String admissionScheme;

    private String name;
    private String fatherName;
    private String motherName;
    private LocalDate dateOfBirth;
    private String gender;
    private String religion;
    private String category;
    private String fatherContactNo;
    private String fullAddress;

    // Academic positioning data mappings
    private Long sessionId; // Links to active AcademicSession object
    private String classOfAdmission;
    private String medium;
    private String stream;
    private Integer rollNumber;

    // Registry IDs
    private String childIdNo;
    private String familyIdNo;
    private String studentAadharNo;
    private String studentSssmidNo;
    private String penNo;
    private String apaarIdNo;

    // Bank data mappings
    private String bankAccountNo;
    private String ifscCode;
    private String nameOfBank;
}