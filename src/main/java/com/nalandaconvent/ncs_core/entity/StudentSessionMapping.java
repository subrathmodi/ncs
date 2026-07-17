package com.nalandaconvent.ncs_core.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "student_session_mappings", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_id", "session_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentSessionMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mappingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private AcademicSession academicSession;

    @Column(name = "current_class", nullable = false, length = 50)
    private String currentClass; // Current year's class placement (e.g., "Class VII")

    private Integer rollNumber;
}