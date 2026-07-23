package com.nalandaconvent.ncs_core.service;

import com.nalandaconvent.ncs_core.dto.*;
import com.nalandaconvent.ncs_core.entity.*;
import com.nalandaconvent.ncs_core.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class MarksheetService {

    @Autowired
    private StudentSessionMappingRepository mappingRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private StudentMarkRepository markRepository;

    @Autowired
    private CurriculumMappingRepository curriculumRepository;

    public ReportCardDTO compileReportCard(Long mappingId) {
        StudentSessionMapping map = mappingRepository.findById(mappingId)
                .orElseThrow(() -> new IllegalArgumentException("Target student mapping segment not found."));

        ReportCardDTO rc = new ReportCardDTO();
        rc.setSessionName(map.getAcademicSession().getSessionName());
        rc.setStudentName(map.getStudent().getName());
        rc.setFatherName(map.getStudent().getFatherName());
        rc.setMotherName(map.getStudent().getMotherName());
        rc.setClassName(map.getCurrentClass());
        rc.setRollNo(map.getRollNumber() != null ? map.getRollNumber().toString() : "N/A");
        rc.setDateOfBirth(map.getStudent().getDateOfBirth().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        rc.setPhotoPath(map.getStudent().getPhotoFilePath());
        rc.setGender(map.getStudent().getGender());

        // Pull active curriculum mapping rules to check for "N/A" layout constraints
        List<CurriculumMapping> activeCurriculum = curriculumRepository
                .findByAcademicSessionSessionIdAndClassName(map.getAcademicSession().getSessionId(), map.getCurrentClass());

        // Fetch all student marks records cached inside PostgreSQL
        List<StudentMark> savedMarks = markRepository.findAll(); // Optimization: use custom query filters in production

        List<SubjectGradeDTO> scholastics = new ArrayList<>();
        List<SubjectGradeDTO> coscholastics = new ArrayList<>();

        int totalEarned = 0;
        int maxPossibleScholasticScore = 0;

        // Dynamic Compilation Loop matching all possible active master subjects records register
        List<Subject> allGlobalSubjects = subjectRepository.findAll();
        for (Subject sub : allGlobalSubjects) {

            // Check if this subject is structurally mapped into the active classroom curriculum
            Optional<CurriculumMapping> curriculumRule = activeCurriculum.stream()
                    .filter(c -> c.getSubject().getSubjectId().equals(sub.getSubjectId()))
                    .findFirst();

            SubjectGradeDTO row = new SubjectGradeDTO();
            row.setSubjectName(sub.getSubjectName());
            row.setCoScholastic(sub.isCoScholastic());

            // Isolate matching raw marks entry point records values from database vault
            Optional<StudentMark> markOpt = savedMarks.stream()
                    .filter(m -> m.getStudentSessionMapping().getMappingId().equals(mappingId)
                            && m.getSubject().getSubjectId().equals(sub.getSubjectId()))
                    .findFirst();

            if (sub.isCoScholastic()) {
                // If it is inside curriculum mapping framework, read grade, otherwise mark "N/A"
                if (curriculumRule.isPresent()) {
                    row.setCoScholasticGrade(markOpt.isPresent() ? markOpt.get().getCoScholasticGrade() : "A");
                    coscholastics.add(row);
                }
            } else {
                if (curriculumRule.isPresent()) {
                    CurriculumMapping rule = curriculumRule.get();
                    int q = (markOpt.isPresent() && markOpt.get().getQuarterlyMarks() != null) ? markOpt.get().getQuarterlyMarks() : 0;
                    int h = (markOpt.isPresent() && markOpt.get().getHalfYearlyMarks() != null) ? markOpt.get().getHalfYearlyMarks() : 0;
                    int a = (markOpt.isPresent() && markOpt.get().getAnnualMarks() != null) ? markOpt.get().getAnnualMarks() : 0;

                    row.setQuarterlyMarks(q);
                    row.setHalfYearlyMarks(h);
                    row.setAnnualMarks(a);
                    row.setSubjectTotal(q + h + a);

                    totalEarned += row.getSubjectTotal();
                    maxPossibleScholasticScore += (rule.getMaxQuarterly() + rule.getMaxHalfYearly() + rule.getMaxAnnual());

                    scholastics.add(row);
                }
            }
        }

        rc.setScholasticRecords(scholastics);
        rc.setCoScholasticRecords(coscholastics);
        rc.setGrandTotal(totalEarned);

        // Derive percentage parameters dynamically
        if (maxPossibleScholasticScore > 0) {
            double pct = ((double) totalEarned / maxPossibleScholasticScore) * 100;
            rc.setFinalPercentage(Math.round(pct * 100.0) / 100.0);
        }

        // Apply specified corporate marking matrix ranges boundaries matching visual keys checklist rules
        double pct = rc.getFinalPercentage();
        if (pct >= 91) { rc.setFinalGrade("A+"); rc.setCalculatedRemark("Outstanding Performance"); }
        else if (pct >= 81) { rc.setFinalGrade("A"); rc.setCalculatedRemark("Excellent Performance"); }
        else if (pct >= 71) { rc.setFinalGrade("B+"); rc.setCalculatedRemark("Very Good Performance"); }
        else if (pct >= 61) { rc.setFinalGrade("B"); rc.setCalculatedRemark("Good Performance"); }
        else if (pct >= 51) { rc.setFinalGrade("C+"); rc.setCalculatedRemark("Fair Performance"); }
        else if (pct >= 41) { rc.setFinalGrade("C"); rc.setCalculatedRemark("Satisfactory Performance"); }
        else if (pct >= 33) { rc.setFinalGrade("D"); rc.setCalculatedRemark("Average Performance"); }
        else { rc.setFinalGrade("E"); rc.setCalculatedRemark("Very Weak Performance"); }

        rc.setPromotionStatusText(pct >= 33.0 ? "Promoted to Next Class Tier" : "Retained in Current Class Stage");

        return rc;
    }
}
