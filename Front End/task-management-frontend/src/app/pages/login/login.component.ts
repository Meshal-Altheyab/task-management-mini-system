import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
})
export class LoginComponent {
  errorMsg = '';
  isSubmitting = false;

  form = this.fb.group({
    username: ['', [Validators.required]],
    password: ['', [Validators.required]],
  });

  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router) {}

  submit() {
    this.errorMsg = '';

    if (this.form.invalid) {
      this.errorMsg = 'اكتب اليوزرنيم والباسورد';
      return;
    }

    this.isSubmitting = true;

    this.auth.login({
      username: this.form.value.username!,
      password: this.form.value.password!,
    }).subscribe({
      next: () => {
        this.router.navigateByUrl('/my-tasks');
      },
      error: (err) => {
        console.error(err);
        this.errorMsg = 'فشل تسجيل الدخول (تأكد من البيانات)';
        this.isSubmitting = false;
      },
      complete: () => {
        this.isSubmitting = false;
      }
    });
  }
}
