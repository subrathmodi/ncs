package com.nalandaconvent.ncs_core.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class TCIssuanceRequest {
    private Long studentId;
    private String studentReligion;
    private String studentCategory; // General, OBC, SC, ST
    private String lastExaminationResult; // e.g., "Passed Class VI Annual Exam"
    private String subjectsStudied; // Composed list string matching checklist
    private String promotedToClassFig; // e.g., "Class 7"
    private String promotedToClassWords; // e.g., "Class Seventh"
    private String studentBehavior; // e.g., "GOOD"
    private String reasonForLeaving;
    private LocalDate dateOfIssue;


}
