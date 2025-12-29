package com.example.demo.report.controller;

import com.example.demo.report.dto.ReportRequestDTO;
import com.example.demo.report.dto.ReportResponseDTO;
import com.example.demo.report.service.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public ResponseEntity<byte[]> generate(@RequestBody ReportRequestDTO request) {

        ReportResponseDTO response = reportService.generate(request);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + response.getFileName() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, response.getContentType())
                .body(response.getBytes());
    }
}
