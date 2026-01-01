import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';

import { TasksService, Task } from '../../core/services/tasks.service';

@Component({
  selector: 'app-tasks-my',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './tasks-my.component.html',
})
export class TasksMyComponent implements OnInit {
  tasks: Task[] = [];
  loading = false;
  errorMsg = '';

  constructor(private tasksService: TasksService, private router: Router) {}

  ngOnInit(): void {
    this.loadMyTasks();
  }

  loadMyTasks() {
    this.loading = true;
    this.errorMsg = '';

    this.tasksService.myTasks().subscribe({
      next: (data) => (this.tasks = data),
      error: (err) => {
        console.error(err);
        this.errorMsg = 'ما قدرنا نجيب المهام (تأكد أنك مسجل دخول)';
        this.loading = false;
      },
      complete: () => (this.loading = false),
    });
  }

  details(task: Task) {
    this.router.navigate(['/task', task.id], { state: { task, from: '/my-tasks' } });
  }

  edit(task: Task) {
    this.router.navigate(['/edit-task', task.id], { state: { task, from: '/my-tasks' } });
  }

  remove(id: number) {
    if (!confirm('متأكد تبي تحذف التاسك؟')) return;

    this.tasksService.delete(id).subscribe({
      next: () => this.loadMyTasks(),
      error: (err) => {
        console.error(err);
        this.errorMsg = 'فشل حذف التاسك';
      },
    });
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
