import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

// لازم تكون نفس أسماء الـ enums اللي في الباك (ReportType / ReportOutputFormat)
export type ReportType =
  | 'MY_TASKS_LIST'
  | 'ALL_TASKS_LIST'
  | 'MY_TASKS_DASHBOARD'
  | 'ALL_TASKS_DASHBOARD';

export type ReportOutputFormat = 'PDF' | 'XLSX' | 'RTF';

export interface ReportRequest {
  type: ReportType;
  format: ReportOutputFormat;
}

@Injectable({
  providedIn: 'root',
})
export class ReportsService {
  constructor(private http: HttpClient) {}

  /**
   * POST /api/reports
   * الباك يرجع bytes + headers (content-disposition) عشان اسم الملف
   */
  generate(request: ReportRequest): Observable<HttpResponse<Blob>> {
    return this.http.post('/api/reports', request, {
      observe: 'response',
      responseType: 'blob',
    });
  }
}
