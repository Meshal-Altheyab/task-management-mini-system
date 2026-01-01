import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { TasksService, Task } from '../../core/services/tasks.service';

@Component({
  selector: 'app-task-details',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './task-details.component.html',
})
export class TaskDetailsComponent implements OnInit {
  id = 0;
  task: Task | null = null;
  backUrl = '/my-tasks';

  loading = false;
  errorMsg = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private tasksService: TasksService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    this.id = Number(idParam);

    if (!this.id) {
      this.errorMsg = 'الرابط غير صحيح (ما فيه ID)';
      return;
    }

    const stateTask = history.state?.task as Task | undefined;
    const from = history.state?.from as string | undefined;
    if (from) {
      this.backUrl = from;
    }
    if (stateTask && stateTask.id === this.id) {
      this.task = stateTask;
      return;
    }

    this.loadTaskFromApi();
  }

  private loadTaskFromApi() {
    this.loading = true;
    this.errorMsg = '';

    this.tasksService.myTasks().subscribe({
      next: (tasks) => {
        const found = tasks.find((t) => t.id === this.id);
        if (found) {
          this.task = found;
          this.loading = false;
          return;
        }

        this.tasksService.allTasks().subscribe({
          next: (all) => {
            const foundAll = all.find((t) => t.id === this.id);
            if (foundAll) {
              this.task = foundAll;
            } else {
              this.errorMsg = 'ما لقينا التاسك المطلوب';
            }
            this.loading = false;
          },
          error: (err) => {
            console.error(err);
            this.errorMsg = 'ما لقينا التاسك أو ما عندك صلاحية';
            this.loading = false;
          },
        });
      },
      error: (err) => {
        console.error(err);
        this.errorMsg = 'ما قدرنا نجيب تفاصيل التاسك (تأكد أنك مسجل دخول)';
        this.loading = false;
      },
    });
  }

  goEdit() {
    if (!this.task) return;
    this.router.navigate(['/edit-task', this.task.id], { state: { task: this.task, from: this.backUrl } });
  }

  back() {
    this.router.navigateByUrl(this.backUrl || '/my-tasks');
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
