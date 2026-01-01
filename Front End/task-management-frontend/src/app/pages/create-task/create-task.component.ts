import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { TasksService, TaskDto, TaskStatus } from '../../core/services/tasks.service';

@Component({
  selector: 'app-create-task',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './create-task.component.html',
})
export class CreateTaskComponent {
  errorMsg = '';
  successMsg = '';
  isSubmitting = false;

  form = this.fb.group({
    title: ['', [Validators.required]],
    description: ['', [Validators.required]],
    status: ['NEW' as TaskStatus, [Validators.required]],
  });

  constructor(private fb: FormBuilder, private tasksService: TasksService, private router: Router) {}

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

    this.tasksService.create(dto).subscribe({
      next: () => {
        this.successMsg = 'تم إنشاء التاسك ✅';
        setTimeout(() => this.router.navigateByUrl('/my-tasks'), 600);
      },
      error: (err) => {
        console.error(err);
        this.errorMsg = 'فشل إنشاء التاسك';
        this.isSubmitting = false;
      },
      complete: () => (this.isSubmitting = false)
    });
  }
}
