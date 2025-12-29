package com.example.demo.report.dto;

import com.example.demo.report.enums.ReportOutputFormat;
import com.example.demo.report.enums.ReportType;

public class ReportRequestDTO {

    private ReportType type;
    private ReportOutputFormat format;

    public ReportType getType() {
        return type;
    }

    public void setType(ReportType type) {
        this.type = type;
    }

    public ReportOutputFormat getFormat() {
        return format;
    }

    public void setFormat(ReportOutputFormat format) {
        this.format = format;
    }
}
