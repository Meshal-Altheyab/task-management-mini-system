import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
})
export class RegisterComponent {
  errorMsg = '';
  successMsg = '';
  isSubmitting = false;

  form = this.fb.group({
    username: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]],
    role: ['USER', [Validators.required]],
  });

  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router) {}

  submit() {
    this.errorMsg = '';
    this.successMsg = '';

    if (this.form.invalid) {
      this.errorMsg = 'تأكد من اليوزرنيم + الإيميل + الباسورد';
      return;
    }

    this.isSubmitting = true;

    this.auth.register({
      username: this.form.value.username!,
      email: this.form.value.email!,
      password: this.form.value.password!,
      role: this.form.value.role!,
    }).subscribe({
      next: () => {
        this.successMsg = 'تم التسجيل بنجاح ✅ الآن سجّل دخول';
        setTimeout(() => this.router.navigateByUrl('/login'), 700);
      },
      error: (err) => {
        console.error(err);
        this.errorMsg = 'فشل التسجيل (ممكن اليوزر موجود)';
        this.isSubmitting = false;
      },
      complete: () => {
        this.isSubmitting = false;
      }
    });
  }
}
