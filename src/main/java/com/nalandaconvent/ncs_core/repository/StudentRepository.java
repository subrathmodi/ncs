package com.nalandaconvent.ncs_core.repository;

import com.nalandaconvent.ncs_core.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface StudentRepository extends JpaRepository<Student, Long> {
    // Standard CRUD operations for the core profile
    @Query("SELECT COUNT(s) FROM Student s WHERE s.currentStatus != 'TC_ISSUED'")
    long countActiveEnrolledStudents();

    // 2. Count students admitted within a given date window (Current Month calculation)
    @Query("SELECT COUNT(s) FROM Student s WHERE s.dateOfAdmission BETWEEN :startDate AND :endDate")
    long countRecentAdmissions(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
