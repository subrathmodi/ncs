package com.nalandaconvent.ncs_core.dto;

import lombok.Data;

@Data
public class StudentMarkEntryDTO {
    private Long studentSessionMappingId;
    private Integer quarterlyMarks;
    private Integer halfYearlyMarks;
    private Integer annualMarks;
    private String coScholasticGrade;
}
