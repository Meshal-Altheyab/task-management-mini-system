package com.example.demo.report.enums;

public enum ReportType {
    MY_TASKS_LIST("TasksListReport.jrxml", "my-tasks"),
    ALL_TASKS_LIST("TasksListReport.jrxml", "all-tasks"),
    MY_TASKS_DASHBOARD("TasksDashboardReport.jrxml", "my-tasks-dashboard"),
    ALL_TASKS_DASHBOARD("TasksDashboardReport.jrxml", "all-tasks-dashboard");

    private final String template;
    private final String outputBaseName;

    ReportType(String template, String outputBaseName) {
        this.template = template;
        this.outputBaseName = outputBaseName;
    }

    public String getTemplate() {
        return template;
    }

    public String getOutputBaseName() {
        return outputBaseName;
    }
}
