package com.nalandaconvent.ncs_core.service;

import com.nalandaconvent.ncs_core.dto.BulkMarkSubmitRequest;
import com.nalandaconvent.ncs_core.dto.StudentMarkEntryDTO;
import com.nalandaconvent.ncs_core.entity.*;
import com.nalandaconvent.ncs_core.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EvaluationService {

    @Autowired
    private StudentMarkRepository markRepository;

    @Autowired
    private CurriculumMappingRepository curriculumRepository;

    @Autowired
    private StudentSessionMappingRepository sessionMappingRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    /**
     * Processes bulk spreadsheet submissions while enforcing max limits defined in the curriculum.
     */
    @Transactional
    public void saveBulkMarks(BulkMarkSubmitRequest request) {
        // 1. Fetch Curriculum Rules to read maximum scores
        List<CurriculumMapping> curriculumList = curriculumRepository
                .findByAcademicSessionSessionIdAndClassName(request.getSessionId(), request.getClassName());

        CurriculumMapping rule = curriculumList.stream()
                .filter(c -> c.getSubject().getSubjectId().equals(request.getSubjectId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("This subject is not part of the curriculum for the selected class and session!"));

        Subject subject = rule.getSubject();

        // 2. Iterate through student rows from the spreadsheet grid
        for (StudentMarkEntryDTO entry : request.getMarksList()) {

            // Validate limits for Scholastic subjects
            if (!subject.isCoScholastic()) {
                validateMarkLimit(entry.getQuarterlyMarks(), rule.getMaxQuarterly(), "Quarterly", entry.getStudentSessionMappingId());
                validateMarkLimit(entry.getHalfYearlyMarks(), rule.getMaxHalfYearly(), "Half Yearly", entry.getStudentSessionMappingId());
                validateMarkLimit(entry.getAnnualMarks(), rule.getMaxAnnual(), "Annual", entry.getStudentSessionMappingId());
            }

            // 3. Upsert: Update existing row or create a new record if it doesn't exist
            StudentMark markRecord = markRepository
                    .findByStudentSessionMappingMappingIdAndSubjectSubjectId(entry.getStudentSessionMappingId(), request.getSubjectId())
                    .orElse(new StudentMark());

            if (markRecord.getMarkId() == null) {
                StudentSessionMapping studentMapping = sessionMappingRepository.findById(entry.getStudentSessionMappingId())
                        .orElseThrow(() -> new IllegalArgumentException("Student mapping mappingId not found."));
                markRecord.setStudentSessionMapping(studentMapping);
                markRecord.setSubject(subject);
            }

            // Populate the fields based on subject classification
            if (subject.isCoScholastic()) {
                markRecord.setCoScholasticGrade(entry.getCoScholasticGrade());
            } else {
                markRecord.setQuarterlyMarks(entry.getQuarterlyMarks());
                markRecord.setHalfYearlyMarks(entry.getHalfYearlyMarks());
                markRecord.setAnnualMarks(entry.getAnnualMarks());
            }

            markRepository.save(markRecord);
        }
    }

    private void validateMarkLimit(Integer inputScore, Integer maxAllowed, String examType, Long mappingId) {
        if (inputScore != null && maxAllowed != null && inputScore > maxAllowed) {
            throw new IllegalArgumentException("Error: Entered " + examType + " marks (" + inputScore +
                    ") exceeds the maximum limit of " + maxAllowed + " for student mapping ref: " + mappingId);
        }
    }
}
