import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { TasksService, Task, TaskDto, TaskStatus } from '../../core/services/tasks.service';

@Component({
  selector: 'app-edit-task',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './edit-task.component.html',
})
export class EditTaskComponent implements OnInit {
  id = 0;
  backUrl = '/my-tasks';
  loading = false;
  errorMsg = '';
  successMsg = '';
  isSubmitting = false;

  form = this.fb.group({
    title: ['', [Validators.required]],
    description: ['', [Validators.required]],
    status: ['NEW' as TaskStatus, [Validators.required]],
  });

  constructor(
    private fb: FormBuilder,
    private tasksService: TasksService,
    private route: ActivatedRoute,
    private router: Router
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
      this.fillForm(stateTask);
      return;
    }

    this.loadTaskFromApi();
  }

  private fillForm(task: Task) {
    this.form.patchValue({
      title: task.title,
      description: task.description,
      status: task.status,
    });
  }

  private loadTaskFromApi() {
    this.loading = true;
    this.errorMsg = '';

    this.tasksService.myTasks().subscribe({
      next: (tasks) => {
        const found = tasks.find((t) => t.id === this.id);
        if (found) {
          this.fillForm(found);
          this.loading = false;
          return;
        }

        this.tasksService.allTasks().subscribe({
          next: (all) => {
            const foundAll = all.find((t) => t.id === this.id);
            if (foundAll) {
              this.fillForm(foundAll);
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
        this.errorMsg = 'ما قدرنا نجيب بيانات التاسك (تأكد أنك مسجل دخول)';
        this.loading = false;
      },
    });
  }

  submit() {
    this.errorMsg = '';
    this.successMsg = '';

    if (this.form.invalid) {
      this.errorMsg = 'تأكد عبيت العنوان والوصف والحالة';
      return;
    }

    const dto: TaskDto = {
      title: this.form.value.title!,
      description: this.form.value.description!,
      status: this.form.value.status!,
    };

    this.isSubmitting = true;

    this.tasksService.update(this.id, dto).subscribe({
      next: () => {
        this.successMsg = 'تم تعديل التاسك ✅';
        setTimeout(() => this.router.navigateByUrl(this.backUrl || '/my-tasks'), 600);
      },
      error: (err) => {
        console.error(err);
        this.errorMsg = 'فشل تعديل التاسك';
        this.isSubmitting = false;
      },
      complete: () => (this.isSubmitting = false)
    });
  }

  cancel() {
    this.router.navigateByUrl(this.backUrl || '/my-tasks');
  }
}
