package com.nalandaconvent.ncs_core.repository;

import com.nalandaconvent.ncs_core.entity.AcademicSession;
import com.nalandaconvent.ncs_core.entity.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AcademicSessionRepository extends JpaRepository<AcademicSession, Long> {
    // Resolves sessions by their structural state machine flags (ACTIVE, UPCOMING, ENDED)
    Optional<AcademicSession> findByStatus(SessionStatus status);

    // Checks presence constraints before changing states
    boolean existsByStatus(SessionStatus status);
}
