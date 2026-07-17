package com.nalandaconvent.ncs_core.service;

import com.nalandaconvent.ncs_core.dto.TCIssuanceRequest;
import com.nalandaconvent.ncs_core.entity.Student;
import com.nalandaconvent.ncs_core.entity.TransferCertificate;
import com.nalandaconvent.ncs_core.repository.StudentRepository;
import com.nalandaconvent.ncs_core.repository.TransferCertificateRepository;
import com.nalandaconvent.ncs_core.util.DateToWordConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class TransferCertificateService {

    @Autowired
    private TransferCertificateRepository tcRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Transactional
    public TransferCertificate issueTransferCertificate(TCIssuanceRequest request) {
        // 1. Fetch Student master entry profile validation
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("Target Student profile reference record not found."));

        // Safeguard: Check if this profile has already been processed for exit
        Optional<TransferCertificate> existingTC = tcRepository.findByStudentStudentId(request.getStudentId());
        if (existingTC.isPresent()) {
            throw new IllegalStateException("A Transfer Certificate has already been issued for this student record.");
        }

        // 2. Generate dynamic sequential document tracking number (e.g., "2026-008")
        int currentYear = LocalDate.now().getYear();
        long trackingCount = tcRepository.count() + 1;
        String calculatedSerialNo = String.format("%d-%03d", currentYear, trackingCount);

        // 3. Assemble and populate the structural database record mapping blocks
        TransferCertificate tc = new TransferCertificate();
        tc.setCertificateNo(calculatedSerialNo);
        tc.setStudent(student);
        tc.setDateOfApplication(LocalDate.now());
        tc.setDateOfIssue(request.getDateOfIssue());
        tc.setReasonForLeaving(request.getReasonForLeaving());
        tc.setAcademicConduct(request.getStudentBehavior());
        tc.setPromotedToClass(request.getPromotedToClassFig());

        // Save the dynamic textual breakdown conversion into general remarks columns
        String dobInWords = DateToWordConverter.convert(student.getDateOfBirth());
        tc.setGeneralRemarks(
                "Category: " + request.getStudentCategory() + " | " +
                        "Religion: " + request.getStudentReligion() + " | " +
                        "DOB in Words: " + dobInWords + " | " +
                        "Last Exam Taken: " + request.getLastExaminationResult() + " | " +
                        "Subjects: " + request.getSubjectsStudied() + " | " +
                        "Promotion Target String: " + request.getPromotedToClassWords()
        );

        // 4. Update the student's lifecycle status to permanently stop further promotions
        student.setCurrentStatus("TC_ISSUED");
        studentRepository.save(student);

        return tcRepository.save(tc);
    }
}
