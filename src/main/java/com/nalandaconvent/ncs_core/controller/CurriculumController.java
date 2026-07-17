package com.nalandaconvent.ncs_core.controller;

import com.nalandaconvent.ncs_core.entity.CurriculumMapping;
import com.nalandaconvent.ncs_core.service.CurriculumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/academics/curriculum")
public class CurriculumController {

    @Autowired
    private CurriculumService curriculumService;

    // POST: Link a subject to a class with its max marking scheme metrics
    @PostMapping("/map-subject")
    public ResponseEntity<?> mapSubject(@RequestBody Map<String, Object> request) {
        try {
            Long sessionId = Long.valueOf(request.get("sessionId").toString());
            String className = request.get("className").toString();
            Long subjectId = Long.valueOf(request.get("subjectId").toString());

            Integer maxQuat = request.get("maxQuarterly") != null ? Integer.valueOf(request.get("maxQuarterly").toString()) : null;
            Integer maxHalf = request.get("maxHalfYearly") != null ? Integer.valueOf(request.get("maxHalfYearly").toString()) : null;
            Integer maxAnnual = request.get("maxAnnual") != null ? Integer.valueOf(request.get("maxAnnual").toString()) : null;

            CurriculumMapping result = curriculumService.mapSubjectToClass(sessionId, className, subjectId, maxQuat, maxHalf, maxAnnual);
            return ResponseEntity.ok(Map.of("message", "Subject added to curriculum successfully!", "mappingId", result.getMappingId()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getLocalizedMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to update classroom mapping rule profiles."));
        }
    }

    // GET: Retrieve a clean list of subjects mapped to a specific class tier
    @GetMapping("/view")
    public ResponseEntity<List<CurriculumMapping>> getCurriculum(@RequestParam Long sessionId, @RequestParam String className) {
        return ResponseEntity.ok(curriculumService.getClassCurriculum(sessionId, className));
    }
    @DeleteMapping("/remove/{mappingId}")
    public ResponseEntity<?> removeSubject(@PathVariable Long mappingId) {
        try {
            curriculumService.removeSubjectFromCurriculum(mappingId);
            return ResponseEntity.ok(Map.of("message", "Subject removed from the curriculum successfully."));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getLocalizedMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to remove the selected subject configuration rule."));
        }
    }
}
