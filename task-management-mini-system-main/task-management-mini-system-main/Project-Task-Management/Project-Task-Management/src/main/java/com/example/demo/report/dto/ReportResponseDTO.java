package com.example.demo.report.dto;

public class ReportResponseDTO {

    private final String fileName;
    private final byte[] bytes;
    private final String contentType;

    public ReportResponseDTO(String fileName, byte[] bytes, String contentType) {
        this.fileName = fileName;
        this.bytes = bytes;
        this.contentType = contentType;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public String getContentType() {
        return contentType;
    }
}
