package com.nalandaconvent.ncs_core.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "transfer_certificates")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferCertificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tcId;

    // Strict serial document tracking number (e.g., "NCS/TC/2026/001")
    @Column(name = "certificate_no", unique = true, nullable = false, length = 50)
    private String certificateNo;

    // Bidirectional link to the primary student master table
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false, unique = true)
    private Student student;

    @Column(name = "date_of_application", nullable = false)
    private LocalDate dateOfApplication;

    @Column(name = "date_of_issue", nullable = false)
    private LocalDate dateOfIssue;

    @Column(name = "reason_for_leaving", nullable = false, length = 255)
    private String reasonForLeaving; // e.g., "Parent Relocation", "Higher Studies"

    @Column(length = 50)
    private String academicConduct = "GOOD"; // Good, Satisfactory, Exemplary

    @Column(name = "promoted_to_class", length = 50)
    private String promotedToClass; // Class tier reached upon exit milestone

    @Column(columnDefinition = "TEXT")
    private String generalRemarks;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
