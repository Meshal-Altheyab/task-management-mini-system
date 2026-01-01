import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  // الباك عندك يدعم role (ADMIN / USER)
  // لو ما انرسل، الباك ممكن يحطه افتراضي حسب الـ RegistrationService
  role?: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly TOKEN_KEY = 'tm_token';

  constructor(private http: HttpClient) {}

  /**
   * يسوي Login ويرجع توكن (String) من الباك: POST /api/auth/login
   */
  login(req: LoginRequest): Observable<string> {
    return this.http
      .post('/api/auth/login', req, { responseType: 'text' })
      .pipe(tap((token) => this.saveToken(token)));
  }

  /**
   * يسوي Register: POST /api/auth/register
   */
  register(req: RegisterRequest): Observable<string> {
    return this.http.post('/api/auth/register', req, { responseType: 'text' });
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
  }

  loggedIn(): boolean {
    return !!this.getToken();
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  private saveToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
  }
}
