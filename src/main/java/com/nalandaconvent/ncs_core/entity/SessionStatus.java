package com.nalandaconvent.ncs_core.entity;

public enum SessionStatus {
    UPCOMING, // Planning Phase: Admissions accepted, curriculum editable
    ACTIVE,   // Live Phase: Curriculum locked, marks entry enabled
    ENDED     // Archived Phase: Historical read-only records ledger
}
