package com.nalandaconvent.ncs_core.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "curriculum_mappings", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"session_id", "class_name", "subject_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurriculumMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mappingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", referencedColumnName = "sessionId", nullable = false)
    private AcademicSession academicSession;

    @Column(name = "class_name", nullable = false, length = 50)
    private String className; // e.g., "Class VI", "Class VII"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    // Maximum structural limit parameters (Returns Null / NA if row mapping entry doesn't exist)
    private Integer maxQuarterly;
    private Integer maxHalfYearly;
    private Integer maxAnnual;
}
