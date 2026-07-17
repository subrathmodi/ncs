package com.nalandaconvent.ncs_core.repository;

import com.nalandaconvent.ncs_core.entity.StudentSessionMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface StudentSessionMappingRepository extends JpaRepository<StudentSessionMapping, Long> {

    @Query("SELECT m FROM StudentSessionMapping m WHERE m.academicSession.sessionId = :sessionId AND m.currentClass = :currentClass")
    List<StudentSessionMapping> findByAcademicSessionIdAndCurrentClass(
            @Param("sessionId") Long sessionId,
            @Param("currentClass") String currentClass);

    List<StudentSessionMapping> findByAcademicSessionSessionId(Long sessionId);
}