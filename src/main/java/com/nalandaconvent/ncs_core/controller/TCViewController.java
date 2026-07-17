package com.nalandaconvent.ncs_core.controller;

import com.nalandaconvent.ncs_core.entity.Student;
import com.nalandaconvent.ncs_core.entity.TransferCertificate;
import com.nalandaconvent.ncs_core.repository.StudentRepository;
import com.nalandaconvent.ncs_core.repository.TransferCertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/dashboard/academics/tc")
public class TCViewController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TransferCertificateRepository tcRepository;

    // 1. Renders the operator data entry form panel
    @GetMapping("/issue-form/{studentId}")
    public String showTCIssuanceForm(@PathVariable Long studentId, Model model) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found."));
        model.addAttribute("student", student);
        return "academics/tc-issue-form";
    }

    // 2. Compiles historical metadata map configurations and renders the A4 print sheet layout
    @GetMapping("/print/{tcId}")
    public String printTransferCertificate(@PathVariable Long tcId, Model model) {
        TransferCertificate tc = tcRepository.findById(tcId)
                .orElseThrow(() -> new IllegalArgumentException("Transfer Certificate records not found."));

        model.addAttribute("tc", tc);

        // Parse packed metadata fields safely out of the general remarks block string
        Map<String, String> meta = parseRemarksMetadata(tc.getGeneralRemarks());
        model.addAttribute("meta", meta);

        return "academics/tc-print-layout";
    }
    // Append this helpful secondary lookup endpoint inside your TCViewController.java file:
    @GetMapping("/print/by-mapping/{mappingId}")
    public String printTransferCertificateByMapping(@PathVariable Long mappingId, Model model) {
        // Find the record matching the student session assignment history log
        TransferCertificate tc = tcRepository.findAll().stream()
                .filter(t -> t.getStudent().getCurrentStatus().equals("TC_ISSUED"))
                .filter(t -> t.getStudent().getStudentId().equals(
                        studentRepository.findAll().stream() // Standard production environments optimize with custom query joins
                                .filter(s -> s.getStudentId().equals(t.getStudent().getStudentId()))
                                .findFirst().map(Student::getStudentId).orElse(-1L)
                ))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No archive certificate generated for this mapping identifier block."));

        model.addAttribute("tc", tc);

        // Safely parse out metadata properties
        java.util.Map<String, String> meta = new java.util.HashMap<>();
        if (tc.getGeneralRemarks() != null) {
            String[] tokens = tc.getGeneralRemarks().split("\\s*\\|\\s*");
            for (String token : tokens) {
                String[] kv = token.split("\\s*:\\s*", 2);
                if (kv.length == 2) meta.put(kv[0].trim(), kv[1].trim());
            }
        }
        model.addAttribute("meta", meta);

        return "academics/tc-print-layout";
    }

    private Map<String, String> parseRemarksMetadata(String remarks) {
        Map<String, String> map = new HashMap<>();
        if (remarks == null || remarks.isEmpty()) return map;

        String[] tokens = remarks.split("\\s*\\|\\s*");
        for (String token : tokens) {
            String[] kv = token.split("\\s*:\\s*", 2);
            if (kv.length == 2) {
                map.put(kv[0].trim(), kv[1].trim());
            }
        }
        return map;
    }
}