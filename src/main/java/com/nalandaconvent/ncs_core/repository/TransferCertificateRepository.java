package com.nalandaconvent.ncs_core.repository;

import com.nalandaconvent.ncs_core.entity.TransferCertificate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TransferCertificateRepository extends JpaRepository<TransferCertificate, Long> {

    // Check if a certificate has already been generated using its document number
    Optional<TransferCertificate> findByCertificateNo(String certificateNo);

    // Check if a student already has a pending or issued TC file record
    Optional<TransferCertificate> findByStudentStudentId(Long studentId);
}