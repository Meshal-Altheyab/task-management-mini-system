import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

import { TasksService, Task } from '../../core/services/tasks.service';

@Component({
  selector: 'app-tasks-all',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './tasks-all.component.html',
})
export class TasksAllComponent implements OnInit {
  tasks: Task[] = [];
  loading = false;
  errorMsg = '';

  constructor(private tasksService: TasksService, private router: Router) {}

  ngOnInit(): void {
    this.loadAllTasks();
  }

  loadAllTasks() {
    this.loading = true;
    this.errorMsg = '';

    this.tasksService.allTasks().subscribe({
      next: (data) => (this.tasks = data),
      error: (err) => {
        console.error(err);
        this.errorMsg = 'ما عندك صلاحية (لازم ADMIN) أو فيه مشكلة اتصال';
        this.loading = false;
      },
      complete: () => (this.loading = false),
    });
  }

  details(task: Task) {
    this.router.navigate(['/task', task.id], { state: { task, from: '/all-tasks' } });
  }

  badgeClass(status: string) {
    switch (status) {
      case 'NEW':
        return 'bg-secondary';
      case 'IN_PROGRESS':
        return 'bg-warning text-dark';
      case 'DONE':
        return 'bg-success';
      default:
        return 'bg-dark';
    }
  }
}
