package com.example.demo.report.service.impl;

import com.example.demo.model.TaskStatus;
import com.example.demo.model.Tasks;
import com.example.demo.model.User;
import com.example.demo.repo.TaskRepo;
import com.example.demo.repo.UserRepo;
import com.example.demo.report.dto.ReportRequestDTO;
import com.example.demo.report.dto.ReportResponseDTO;
import com.example.demo.report.dto.TaskReportRowDTO;
import com.example.demo.report.enums.ReportOutputFormat;
import com.example.demo.report.enums.ReportType;
import com.example.demo.report.exception.ReportGenerationException;
import com.example.demo.report.service.ReportService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
public class JasperReportServiceImpl implements ReportService {

    private final TaskRepo taskRepo;
    private final UserRepo userRepo;

    @Value("${app.report.path:Reports/}")
    private String reportsRoot;

    // عشان ما نكمبايل الملفات كل مرة
    private final AtomicBoolean templatesCompiled = new AtomicBoolean(false);

    public JasperReportServiceImpl(TaskRepo taskRepo, UserRepo userRepo) {
        this.taskRepo = taskRepo;
        this.userRepo = userRepo;
    }

    @Override
    public ReportResponseDTO generate(ReportRequestDTO request) {

        validateRequest(request);

        ReportType type = request.getType();
        ReportOutputFormat format = request.getFormat();

        Path root = Paths.get(reportsRoot).toAbsolutePath().normalize();
        ensureReportFolderExists(root);

        // (1) كمبايل كل jrxml -> jasper (نفس فكرة SFES لأن subreports تتوقع .jasper)
        ensureTemplatesCompiled(root);

        // (2) حمل الـ .jasper النهائي
        Path jasperFile = root.resolve(type.getTemplate().replace(".jrxml", ".jasper"));
        if (!Files.exists(jasperFile)) {
            throw new ReportGenerationException("Compiled report not found: " + jasperFile);
        }

        JasperReport jasperReport;
        try {
            jasperReport = (JasperReport) JRLoader.loadObject(jasperFile.toFile());
        } catch (Exception e) {
            throw new ReportGenerationException("Failed to load compiled report: " + jasperFile, e);
        }

        // (3) جهز الداتا + الباراميترز
        Map<String, Object> params = new HashMap<>();
        params.put("P_LOCATION", root.toString() + File.separator);

        String now = java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        params.put("P_GENERATED_AT", now);

        JasperPrint print;
        try {
            if (isListReport(type)) {
                List<Tasks> tasks = loadTasks(type);
                List<TaskReportRowDTO> rows = mapToRows(tasks);

                params.put("P_TITLE", buildTitle(type));
                params.put("P_TOTAL", rows.size());

                JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(rows);
                print = JasperFillManager.fillReport(jasperReport, params, dataSource);

            } else {
                List<Tasks> tasks = loadTasks(type);
                Map<TaskStatus, Long> counts = countByStatus(tasks);

                params.put("P_TITLE", buildTitle(type));
                params.put("P_TOTAL", tasks.size());
                params.put("P_NEW", counts.getOrDefault(TaskStatus.NEW, 0L));
                params.put("P_IN_PROGRESS", counts.getOrDefault(TaskStatus.IN_PROGRESS, 0L));
                params.put("P_DONE", counts.getOrDefault(TaskStatus.DONE, 0L));

                print = JasperFillManager.fillReport(jasperReport, params, new JREmptyDataSource());
            }
        } catch (JRException e) {
            throw new ReportGenerationException("Failed to fill report", e);
        }

        // (4) Export bytes
        byte[] bytes = export(print, format);

        String ext = format.name().toLowerCase();
        String fileName = type.getOutputBaseName() + "-" + java.time.LocalDate.now() + "." + ext;

        return new ReportResponseDTO(fileName, bytes, contentType(format));
    }

    private void validateRequest(ReportRequestDTO request) {
        if (request == null) {
            throw new ReportGenerationException("Request body is required");
        }
        if (request.getType() == null) {
            throw new ReportGenerationException("type is required");
        }
        if (request.getFormat() == null) {
            throw new ReportGenerationException("format is required");
        }
    }

    private void ensureReportFolderExists(Path root) {
        if (!Files.exists(root)) {
            throw new ReportGenerationException(
                    "Reports folder not found: " + root + "\n" +
                    "Fix: create folder and put jrxml/templates inside it, or update app.report.path"
            );
        }
        if (!Files.isDirectory(root)) {
            throw new ReportGenerationException("app.report.path must point to a folder. Current: " + root);
        }
    }

    private void ensureTemplatesCompiled(Path root) {
        if (templatesCompiled.get()) {
            return;
        }
        synchronized (this) {
            if (templatesCompiled.get()) {
                return;
            }

            try {
                Files.list(root)
                        .filter(p -> p.getFileName().toString().endsWith(".jrxml"))
                        .forEach(jrxml -> {
                            Path out = root.resolve(jrxml.getFileName().toString().replace(".jrxml", ".jasper"));
                            try {
                                JasperCompileManager.compileReportToFile(jrxml.toString(), out.toString());
                            } catch (JRException e) {
                                throw new ReportGenerationException("Failed to compile template: " + jrxml, e);
                            }
                        });

                templatesCompiled.set(true);

            } catch (ReportGenerationException e) {
                throw e;
            } catch (Exception e) {
                throw new ReportGenerationException("Failed to scan/compile templates in: " + root, e);
            }
        }
    }

    private boolean isListReport(ReportType type) {
        return type == ReportType.MY_TASKS_LIST || type == ReportType.ALL_TASKS_LIST;
    }

    private String buildTitle(ReportType type) {
        return switch (type) {
            case MY_TASKS_LIST -> "My Tasks (List)";
            case ALL_TASKS_LIST -> "All Tasks (List)";
            case MY_TASKS_DASHBOARD -> "My Tasks Dashboard";
            case ALL_TASKS_DASHBOARD -> "All Tasks Dashboard";
        };
    }

    private List<Tasks> loadTasks(ReportType type) {

        User currentUser = getCurrentUser();

        boolean wantAll = (type == ReportType.ALL_TASKS_LIST || type == ReportType.ALL_TASKS_DASHBOARD);
        if (wantAll) {
            if (!isAdmin(currentUser)) {
                throw new AccessDeniedException("Admin only: this report requires ADMIN role");
            }
            return taskRepo.findAll();
        }

        return taskRepo.findByUser(currentUser);
    }

    private boolean isAdmin(User user) {
        if (user == null || user.getRole() == null) return false;
        return user.getRole().trim().equalsIgnoreCase("ADMIN") || user.getRole().trim().equalsIgnoreCase("ROLE_ADMIN");
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new ReportGenerationException("No authentication found");
        }
        String username = auth.getName();
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new ReportGenerationException("User not found: " + username);
        }
        return user;
    }

    private List<TaskReportRowDTO> mapToRows(List<Tasks> tasks) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return tasks.stream()
                .map(t -> new TaskReportRowDTO(
                        t.getId(),
                        t.getTitle(),
                        t.getDescription(),
                        t.getStatus() == null ? null : t.getStatus().name(),
                        t.getCreated_at() == null ? null : t.getCreated_at().format(fmt),
                        (t.getUser() == null ? null : t.getUser().getUsername())
                ))
                .collect(Collectors.toList());
    }

    private Map<TaskStatus, Long> countByStatus(List<Tasks> tasks) {
        Map<TaskStatus, Long> result = new EnumMap<>(TaskStatus.class);
        for (Tasks t : tasks) {
            TaskStatus s = t.getStatus();
            if (s == null) continue;
            result.put(s, result.getOrDefault(s, 0L) + 1);
        }
        return result;
    }

    private byte[] export(JasperPrint print, ReportOutputFormat format) {
        try {
            return switch (format) {
                case PDF -> JasperExportManager.exportReportToPdf(print);
                case RTF -> exportRtf(print);
                case XLSX -> exportXlsx(print);
            };
        } catch (Exception e) {
            throw new ReportGenerationException("Failed to export report as " + format, e);
        }
    }

    private byte[] exportRtf(JasperPrint print) throws JRException {
        JRRtfExporter exporter = new JRRtfExporter();
        exporter.setExporterInput(new SimpleExporterInput(print));

        try (java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream()) {
            exporter.setExporterOutput(new SimpleWriterExporterOutput(out));
            exporter.exportReport();
            return out.toByteArray();
        } catch (java.io.IOException e) {
            throw new JRException(e);
        }
    }

    private byte[] exportXlsx(JasperPrint print) throws JRException {
        JRXlsxExporter exporter = new JRXlsxExporter();
        exporter.setExporterInput(new SimpleExporterInput(print));

        SimpleXlsxReportConfiguration config = new SimpleXlsxReportConfiguration();
        config.setOnePagePerSheet(false);
        config.setDetectCellType(true);
        config.setCollapseRowSpan(false);
        config.setRemoveEmptySpaceBetweenRows(true);
        config.setRemoveEmptySpaceBetweenColumns(true);
        exporter.setConfiguration(config);

        try (java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream()) {
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
            exporter.exportReport();
            return out.toByteArray();
        } catch (java.io.IOException e) {
            throw new JRException(e);
        }
    }

    private String contentType(ReportOutputFormat format) {
        return switch (format) {
            case PDF -> "application/pdf";
            case RTF -> "application/rtf";
            case XLSX -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        };
    }
}
