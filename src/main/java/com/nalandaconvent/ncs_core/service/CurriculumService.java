package com.nalandaconvent.ncs_core.service;

import com.nalandaconvent.ncs_core.entity.*;
import com.nalandaconvent.ncs_core.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class CurriculumService {

    @Autowired
    private CurriculumMappingRepository curriculumMappingRepository;

    @Autowired
    private AcademicSessionRepository sessionRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    /**
     * Maps a subject to a class curriculum with component max mark limits.
     * Rule: Can only modify curriculum mapping records for UPCOMING planning sessions.
     */
    @Transactional
    public CurriculumMapping mapSubjectToClass(Long sessionId, String className, Long subjectId,
                                               Integer maxQuat, Integer maxHalf, Integer maxAnnual) {

        // 1. Fetch using the explicit sessionId primary key field
        AcademicSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Academic Session ID " + sessionId + " not found inside the database database registries."));

        if (session.getStatus() != SessionStatus.UPCOMING) {
            throw new IllegalStateException("Curriculum configuration is locked. Modifications are restricted on ACTIVE or ENDED sessions.");
        }

        // 2. Resolve Subject entity
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Subject master ID not found."));

        // 3. Build or update curriculum map entry
        List<CurriculumMapping> existingList = curriculumMappingRepository.findByAcademicSessionSessionIdAndClassName(sessionId, className);
        CurriculumMapping mapping = existingList.stream()
                .filter(m -> m.getSubject().getSubjectId().equals(subjectId))
                .findFirst()
                .orElse(new CurriculumMapping());

        mapping.setAcademicSession(session);
        mapping.setClassName(className);
        mapping.setSubject(subject);

        // Handle scholastic max score configurations versus co-scholastic placeholders
        if (subject.isCoScholastic()) {
            mapping.setMaxQuarterly(null);
            mapping.setMaxHalfYearly(null);
            mapping.setMaxAnnual(null);
        } else {
            mapping.setMaxQuarterly(maxQuat != null ? maxQuat : 0);
            mapping.setMaxHalfYearly(maxHalf != null ? maxHalf : 0);
            mapping.setMaxAnnual(maxAnnual != null ? maxAnnual : 0);
        }

        return curriculumMappingRepository.save(mapping);
    }

    /**
     * Fetches the mapped curriculum schema for a class to determine active subjects and "N/A" positions.
     */
    public List<CurriculumMapping> getClassCurriculum(Long sessionId, String className) {
        return curriculumMappingRepository.findByAcademicSessionSessionIdAndClassName(sessionId, className);
    }

    /**
     * Removes a subject mapping from a class curriculum layout.
     * Rule: Restricted strictly to UPCOMING sessions to safeguard active historical records.
     */
    @Transactional
    public void removeSubjectFromCurriculum(Long mappingId) {
        CurriculumMapping mapping = curriculumMappingRepository.findById(mappingId)
                .orElseThrow(() -> new IllegalArgumentException("Curriculum mapping rule not found."));

        // Enforce our primary timeline safeguard rule
        if (mapping.getAcademicSession().getStatus() != SessionStatus.UPCOMING) {
            throw new IllegalStateException("Curriculum configuration is locked. Cannot remove subjects from ACTIVE or ENDED sessions.");
        }

        curriculumMappingRepository.delete(mapping);
    }
}
