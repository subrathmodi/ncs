package com.nalandaconvent.ncs_core.dto;

import lombok.Data;
import java.util.List;

@Data
public class ReportCardDTO {
    // School Details
    private String schoolName = "NALANDA CONVENT SR.SEC. SCHOOL";
    private String sessionName;

    // Student Information Profile Core
    private String studentName;
    private String fatherName;
    private String motherName;
    private String className;
    private String rollNo;
    private String dateOfBirth;
    private String photoPath;

    // Performance Registers
    private List<SubjectGradeDTO> scholasticRecords;
    private List<SubjectGradeDTO> coScholasticRecords;

    // Aggregates & Derived Summary Parameters
    private Integer grandTotal = 0;
    private Double finalPercentage = 0.0;
    private String finalGrade;
    private String calculatedRemark;
    private String promotionStatusText;
}
