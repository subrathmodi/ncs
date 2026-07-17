package com.nalandaconvent.ncs_core.repository;

import com.nalandaconvent.ncs_core.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    // Find a subject by its exact string name (useful for tracking seed checks)
    Optional<Subject> findBySubjectName(String subjectName);

    // Fetch subjects filtered by whether they are scholastic or co-scholastic[cite: 2]
    List<Subject> findByIsCoScholastic(boolean isCoScholastic);
}