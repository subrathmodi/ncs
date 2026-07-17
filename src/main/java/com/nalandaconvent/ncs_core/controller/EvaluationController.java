package com.nalandaconvent.ncs_core.controller;

import com.nalandaconvent.ncs_core.dto.BulkMarkSubmitRequest;
import com.nalandaconvent.ncs_core.entity.StudentMark;
import com.nalandaconvent.ncs_core.repository.StudentMarkRepository;
import com.nalandaconvent.ncs_core.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/academics/evaluation")
public class EvaluationController {

    @Autowired
    private EvaluationService evaluationService;

    @Autowired
    private StudentMarkRepository markRepository;

    // POST: Save or update the entire class marks sheet simultaneously
    @PostMapping("/save-bulk")
    public ResponseEntity<?> submitBulkMarks(@RequestBody BulkMarkSubmitRequest request) {
        try {
            evaluationService.saveBulkMarks(request);
            return ResponseEntity.ok(Map.of("message", "Class evaluation records saved successfully!"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "An error occurred while compiling processing loops."));
        }
    }

    // GET: Load previously recorded marks to pre-populate the spreadsheet grid rows
    @GetMapping("/load-grid")
    public ResponseEntity<List<StudentMark>> fetchEvaluationGrid(
            @RequestParam Long sessionId,
            @RequestParam String className,
            @RequestParam Long subjectId) {

        List<StudentMark> recordedCluster = markRepository.findRecordedMarksCluster(sessionId, className, subjectId);
        return ResponseEntity.ok(recordedCluster);
    }
}
