package com.nalandaconvent.ncs_core.dto;

import lombok.Data;
import java.util.List;

@Data
public class BulkMarkSubmitRequest {
    private Long sessionId;
    private String className;
    private Long subjectId;
    private List<StudentMarkEntryDTO> marksList;
}
