package com.nalandaconvent.ncs_core.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_marks", uniqueConstraints = {
        // A student can only have one marks record row entry for a specific subject per academic year
        @UniqueConstraint(columnNames = {"student_session_mapping_id", "subject_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentMark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long markId;

    // Links directly to our localized class/session tracking junction row
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_session_mapping_id", nullable = false)
    private StudentSessionMapping studentSessionMapping;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    // --- Scholastic Mark Fields ---
    private Integer quarterlyMarks;
    private Integer halfYearlyMarks;
    private Integer annualMarks;

    // --- Co-Scholastic Grade Field ---
    @Column(length = 5)
    private String coScholasticGrade; // e.g., "A+", "B", "C" (populated if subject.isCoScholastic is true)

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
