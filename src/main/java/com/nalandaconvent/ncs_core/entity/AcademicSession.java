package com.nalandaconvent.ncs_core.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "academic_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcademicSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sessionId;

    @Column(unique = true, nullable = false, length = 20)
    private String sessionName; // e.g., "2025-2026", "2026-2027"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SessionStatus status = SessionStatus.UPCOMING;
}