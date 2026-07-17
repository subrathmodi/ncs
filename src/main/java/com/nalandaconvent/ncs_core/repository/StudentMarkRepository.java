package com.nalandaconvent.ncs_core.repository;

import com.nalandaconvent.ncs_core.entity.StudentMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface StudentMarkRepository extends JpaRepository<StudentMark, Long> {

    // Pulls all recorded marks for a given class/subject grouping loop context
    @Query("SELECT m FROM StudentMark m WHERE m.studentSessionMapping.academicSession.sessionId = :sessionId " +
            "AND m.studentSessionMapping.currentClass = :className AND m.subject.subjectId = :subjectId")
    List<StudentMark> findRecordedMarksCluster(
            @Param("sessionId") Long sessionId,
            @Param("className") String className,
            @Param("subjectId") Long subjectId);

    // Fetch single performance profile records row for isolated checks
    Optional<StudentMark> findByStudentSessionMappingMappingIdAndSubjectSubjectId(Long mappingId, Long subjectId);
}