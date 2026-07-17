package com.nalandaconvent.ncs_core.controller;

import com.nalandaconvent.ncs_core.dto.TCIssuanceRequest;
import com.nalandaconvent.ncs_core.entity.TransferCertificate;
import com.nalandaconvent.ncs_core.service.TransferCertificateService;
import com.nalandaconvent.ncs_core.repository.TransferCertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/academics/tc")
public class TransferCertificateController {

    @Autowired
    private TransferCertificateService tcService;

    @Autowired
    private TransferCertificateRepository tcRepository;

    @PostMapping("/issue")
    public ResponseEntity<?> processTCIssuance(@RequestBody TCIssuanceRequest request) {
        try {
            TransferCertificate record = tcService.issueTransferCertificate(request);
            return ResponseEntity.ok(Map.of(
                    "message", "Transfer Certificate issued and archived successfully!",
                    "tcId", record.getTcId()
            ));
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to compile the exit pipeline sequence."));
        }
    }
}
