package com.nalandaconvent.ncs_core.controller;

import com.nalandaconvent.ncs_core.dto.ReportCardDTO;
import com.nalandaconvent.ncs_core.service.MarksheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/dashboard/academics/marksheet")
public class MarksheetController {

    @Autowired
    private MarksheetService marksheetService;

    // Renders the print-optimized A4 Report Card View
    @GetMapping("/print/{mappingId}")
    public String generateStudentMarksheet(@PathVariable Long mappingId, Model model) {
        ReportCardDTO reportData = marksheetService.compileReportCard(mappingId);
        model.addAttribute("rc", reportData);
        return "academics/report-card-print"; // Resolves template location path
    }
}
