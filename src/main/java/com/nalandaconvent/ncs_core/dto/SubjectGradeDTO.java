package com.nalandaconvent.ncs_core.dto;

import lombok.Data;

@Data
public class SubjectGradeDTO {
    private String subjectName;
    private boolean isCoScholastic;

    // Scholastic Fields (Can contain numeric values or nulls mapping to "N/A")
    private Integer quarterlyMarks;
    private Integer halfYearlyMarks;
    private Integer annualMarks;
    private Integer subjectTotal; // Aggregate sum components

    // Co-Scholastic Grade Placeholder
    private String coScholasticGrade;
}
