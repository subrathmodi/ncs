package com.nalandaconvent.ncs_core.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subjects")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subjectId;

    @Column(unique = true, nullable = false, length = 100)
    private String subjectName; // e.g., "English [W]", "Science", "Reading Skill"

    @Column(nullable = false)
    private boolean isCoScholastic = false; // true = purple purple-box, false = red scholastic
}
