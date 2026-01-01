import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';

import {
  ReportsService,
  ReportRequest,
  ReportType,
  ReportOutputFormat,
} from '../../core/services/reports.service';

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './reports.component.html',
})
export class ReportsComponent {
  errorMsg = '';
  infoMsg = '';
  isSubmitting = false;

  // خيارات النوع (لازم نفس أسماء الباك)
  readonly reportTypes: { value: ReportType; label: string; adminOnly?: boolean }[] = [
    { value: 'MY_TASKS_LIST', label: 'قائمة مهامي (List)' },
    { value: 'MY_TASKS_DASHBOARD', label: 'لوحة مهامي (Dashboard)' },
    { value: 'ALL_TASKS_LIST', label: 'قائمة كل التاسكات (Admin)', adminOnly: true },
    { value: 'ALL_TASKS_DASHBOARD', label: 'لوحة كل التاسكات (Admin)', adminOnly: true },
  ];

  readonly formats: { value: ReportOutputFormat; label: string }[] = [
    { value: 'PDF', label: 'PDF' },
    { value: 'XLSX', label: 'Excel (XLSX)' },
    { value: 'RTF', label: 'Word (RTF)' },
  ];

  form = this.fb.group({
    type: ['MY_TASKS_LIST' as ReportType, [Validators.required]],
    format: ['PDF' as ReportOutputFormat, [Validators.required]],
  });

  constructor(private fb: FormBuilder, private reports: ReportsService) {}

  download() {
    this.errorMsg = '';
    this.infoMsg = '';

    if (this.form.invalid) {
      this.errorMsg = 'اختر نوع التقرير + الصيغة';
      return;
    }

    const req: ReportRequest = {
      type: this.form.value.type!,
      format: this.form.value.format!,
    };

    this.isSubmitting = true;
    this.reports.generate(req).subscribe({
      next: (res) => {
        const blob = res.body;
        if (!blob) {
          this.errorMsg = 'الرد من السيرفر فاضي';
          this.isSubmitting = false;
          return;
        }

        const filename = this.extractFileName(res.headers.get('content-disposition'));
        this.saveBlob(blob, filename);
        this.infoMsg = `تم تجهيز التقرير: ${filename}`;
      },
      error: (err) => {
        // 403 غالبًا إذا اختار تقرير ALL وهو مو Admin
        console.error(err);
        if (err?.status === 403) {
          this.errorMsg = 'ما عندك صلاحية (Admin) لهذا التقرير';
        } else {
          this.errorMsg = 'فشل توليد التقرير (تأكد أن فولدر Reports موجود في الباك)';
        }
        this.isSubmitting = false;
      },
      complete: () => {
        this.isSubmitting = false;
      },
    });
  }

  private extractFileName(contentDisposition: string | null): string {
    // مثال: attachment; filename="my-tasks-2025-12-31.pdf"
    if (!contentDisposition) return 'report';
    const match = /filename\s*=\s*"?([^";]+)"?/i.exec(contentDisposition);
    return match?.[1] || 'report';
  }

  private saveBlob(blob: Blob, filename: string) {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    a.remove();
    window.URL.revokeObjectURL(url);
  }
}
