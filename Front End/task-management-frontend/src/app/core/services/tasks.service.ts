import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type TaskStatus = 'NEW' | 'IN_PROGRESS' | 'DONE';

export interface TaskDto {
  title: string;
  description: string;
  status: TaskStatus;
}

export interface User {
  id?: number;
  username?: string;
  email?: string;
  role?: string;
}

export interface Task {
  id: number;
  title: string;
  description: string;
  status: TaskStatus;
  created_at?: string;
  user?: User;
}

@Injectable({
  providedIn: 'root',
})
export class TasksService {
  constructor(private http: HttpClient) {}

  // GET /api/tasks/my
  myTasks(): Observable<Task[]> {
    return this.http.get<Task[]>('/api/tasks/my');
  }

  // GET /api/tasks/all (ADMIN)
  allTasks(): Observable<Task[]> {
    return this.http.get<Task[]>('/api/tasks/all');
  }

  // POST /api/tasks
  create(dto: TaskDto): Observable<Task> {
    return this.http.post<Task>('/api/tasks', dto);
  }

  // PUT /api/tasks/{id}
  update(id: number, dto: TaskDto): Observable<Task> {
    return this.http.put<Task>(`/api/tasks/${id}`, dto);
  }

  // DELETE /api/tasks/{id}
  delete(id: number): Observable<string> {
    return this.http.delete(`/api/tasks/${id}`, { responseType: 'text' });
  }
}
