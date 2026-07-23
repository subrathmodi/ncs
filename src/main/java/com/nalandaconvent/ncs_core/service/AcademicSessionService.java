package com.nalandaconvent.ncs_core.service;

import com.nalandaconvent.ncs_core.entity.*;
import com.nalandaconvent.ncs_core.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AcademicSessionService {

    @Autowired
    private AcademicSessionRepository sessionRepository;

    @Autowired
    private StudentSessionMappingRepository sessionMappingRepository;

    @Autowired
    private StudentRepository studentRepository;

    /**
     * Rule: Provision an UPCOMING session. Allowed only if no other UPCOMING session exists.
     */
    @Transactional
    public AcademicSession createUpcomingSession(String sessionName) {
        if (sessionRepository.existsByStatus(SessionStatus.UPCOMING)) {
            throw new IllegalStateException("An upcoming session is already in planning. Complete or delete it before establishing a new one.");
        }

        AcademicSession newSession = new AcademicSession();
        newSession.setSessionName(sessionName.trim());
        newSession.setStatus(SessionStatus.UPCOMING);
        return sessionRepository.save(newSession);
    }

    /**
     * Rule: Transition active session to ENDED and transition UPCOMING to ACTIVE.
     * Enforces bulk roll-over promotion routines.
     */
    @Autowired
    private StudentMarkRepository markRepository;

    @Autowired
    private CurriculumMappingRepository curriculumRepository;

    @Transactional
    public void executeSessionTransition() {
        AcademicSession upcomingSession = sessionRepository.findByStatus(SessionStatus.UPCOMING)
                .orElseThrow(() -> new IllegalStateException("No UPCOMING session found to start. Create a curriculum blueprint first."));

        Optional<AcademicSession> currentActiveOpt = sessionRepository.findByStatus(SessionStatus.ACTIVE);
        if (currentActiveOpt.isPresent()) {
            AcademicSession activeSession = currentActiveOpt.get();
            List<StudentSessionMapping> currentStudents = sessionMappingRepository.findByAcademicSessionSessionId(activeSession.getSessionId());

            // Fetch the curriculum configuration for the ending session to calculate total possible scores
            List<CurriculumMapping> activeCurriculum = curriculumRepository.findAll();

            for (StudentSessionMapping oldMap : currentStudents) {
                Student student = oldMap.getStudent();

                if ("ACTIVE".equals(student.getCurrentStatus()) || "PROMOTED".equals(student.getCurrentStatus())) {

                    // --- PASS / FAIL VERIFICATION ENGINE ---
                    List<StudentMark> studentMarks = markRepository.findAll(); // In production, filter by mappingId
                    int totalEarned = 0;
                    int maxPossible = 0;

                    for (StudentMark mark : studentMarks) {
                        if (mark.getStudentSessionMapping().getMappingId().equals(oldMap.getMappingId())
                                && mark.getSubject() != null && !mark.getSubject().isCoScholastic()) {

                            int q = mark.getQuarterlyMarks() != null ? mark.getQuarterlyMarks() : 0;
                            int h = mark.getHalfYearlyMarks() != null ? mark.getHalfYearlyMarks() : 0;
                            int a = mark.getAnnualMarks() != null ? mark.getAnnualMarks() : 0;
                            totalEarned += (q + h + a);

                            // Lookup the max marks from curriculum to calculate baseline
                            Optional<CurriculumMapping> rule = activeCurriculum.stream()
                                    .filter(c -> c.getAcademicSession().getSessionId().equals(activeSession.getSessionId())
                                            && c.getClassName().equals(oldMap.getCurrentClass())
                                            && c.getSubject().getSubjectId().equals(mark.getSubject().getSubjectId()))
                                    .findFirst();

                            if (rule.isPresent()) {
                                maxPossible += (rule.get().getMaxQuarterly() + rule.get().getMaxHalfYearly() + rule.get().getMaxAnnual());
                            }
                        }
                    }

                    double finalPercentage = 0.0;
                    if (maxPossible > 0) {
                        finalPercentage = ((double) totalEarned / maxPossible) * 100;
                    }

                    String targetedClassTier;
                    if (finalPercentage >= 33.0) {
                        // PASS: Advance to the next grade
                        targetedClassTier = getNextClassTier(oldMap.getCurrentClass());
                        student.setCurrentStatus("ACTIVE");
                    } else {
                        // FAIL: Keep them back in the same class (Retained)
                        targetedClassTier = oldMap.getCurrentClass();
                        student.setCurrentStatus("RETAINED");
                    }
                    // ---------------------------------------

                    // Inject the session tracking record row entry
                    StudentSessionMapping renewalMap = new StudentSessionMapping();
                    renewalMap.setStudent(student);
                    renewalMap.setAcademicSession(upcomingSession);
                    renewalMap.setCurrentClass(targetedClassTier);
                    sessionMappingRepository.save(renewalMap);

                    // Sync the master student record
                    student.setAdmissionType("RENEWAL");
                    student.setClassOfAdmission(targetedClassTier);
                    studentRepository.save(student);
                }
            }

            activeSession.setStatus(SessionStatus.ENDED);
            sessionRepository.save(activeSession);
        }

        upcomingSession.setStatus(SessionStatus.ACTIVE);
        sessionRepository.save(upcomingSession);
    }

    /**
     * Simple organizational helper to bump standard school grades automatically
     */
//    private String getNextClassTier(String currentClass) {
//        switch (currentClass) {
//            case "Class 1": return "Class 2";
//            case "Class 2": return "Class 3";
//            case "Class 3": return "Class 4";
//            case "Class 4": return "Class 5";
//            case "Class 5": return "Class 6";
//            case "Class 6": return "Class 7";
//            case "Class 7": return "Class 8";
//            case "Class 8": return "Class 9";
//            case "Class 9": return "Class 10";
//            case "Class 10": return "Class 11";
//            case "Class 11": return "Class 12";
//            default: return currentClass; // High school complete or custom category mapping safety escape
//        }
//    }
    /**
     * Computes the next class tier based on the standardized curriculum ladder.
     */
    private String getNextClassTier(String currentClass) {
        if (currentClass == null) return "Nursery";

        // Standardized academic curriculum ladder sequence structure
        List<String> classLadder = List.of(
                "Nursery", "LKG", "UKG",
                "Class 1", "Class 2", "Class 3", "Class 4", "Class 5",
                "Class 6", "Class 7", "Class 8", "Class 9" ,"Class 10",
                "Class 11" ,"Class 12"
        );

        int currentIndex = classLadder.indexOf(currentClass.trim());

        // If the class name isn't recognized, or they graduated from the highest tier (Class 9), halt progression
        if (currentIndex == -1 || currentIndex == classLadder.size() - 1) {
            return currentClass;
        }

        // Advance to the next grade sequence entry point
        return classLadder.get(currentIndex + 1);
    }
    // Add this method inside AcademicSessionService.java
    public List<AcademicSession> getAllSessionsList() {
        return sessionRepository.findAll();
    }
}
