package com.example.demo.report.service;

import com.example.demo.report.dto.ReportRequestDTO;
import com.example.demo.report.dto.ReportResponseDTO;

public interface ReportService {
    ReportResponseDTO generate(ReportRequestDTO request);
}
