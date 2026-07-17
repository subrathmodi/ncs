package com.nalandaconvent.ncs_core.repository;

import com.nalandaconvent.ncs_core.entity.CurriculumMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CurriculumMappingRepository extends JpaRepository<CurriculumMapping, Long> {
    // Queries full target curriculum structure rules for a specific class row segment
    List<CurriculumMapping> findByAcademicSessionSessionIdAndClassName(Long sessionId, String className);
}
