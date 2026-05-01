import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../core/services/auth.service';

type AuthMode = 'login' | 'register';
type BannerType = 'success' | 'error' | 'info' | null;

@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './auth.html',
})
export class AuthComponent {

  mode: AuthMode = 'login';

  banner: string | null = null;
  bannerKind: BannerType = null;

  loginForm = { identifier: '', password: '' };

  registerForm = {
    username: '',
    email: '',
    password: '',
    confirmPassword: ''
  };

  loginErrors: any = {};
  registerErrors: any = {};

  private bannerTimer: any = null;

  constructor(
    private router: Router,
    private authService: AuthService,
    private cdr: ChangeDetectorRef  // ← добавь
  ) {}

  // ---------------- UTILS ----------------

  private extractErrorMessage(err: HttpErrorResponse, fallback: string): string {
    console.log('=== HTTP ERROR ===');
    console.log('status:', err.status);
    console.log('err.error type:', typeof err.error);
    console.log('err.error value:', err.error);

    let body = err.error;

    if (typeof body === 'string') {
      console.log('body is string, trying to parse...');
      try {
        body = JSON.parse(body);
        console.log('parsed body:', body);
      } catch {
        console.log('parse failed, returning raw string');
        return body || fallback;
      }
    }

    console.log('body.message:', body?.message);
    return body?.message || fallback;
  }

  setBanner(message: string, type: BannerType) {
    if (this.bannerTimer) {
      clearTimeout(this.bannerTimer);
    }

    this.banner = message;
    this.bannerKind = type;
    this.cdr.detectChanges();

    this.bannerTimer = setTimeout(() => {
      this.banner = null;
      this.bannerKind = null;
      this.cdr.detectChanges(); // ← и тут
      this.bannerTimer = null;
    }, 5000);
  }

  closeBanner() {
    if (this.bannerTimer) clearTimeout(this.bannerTimer);
    this.banner = null;
    this.bannerKind = null;
  }

  setMode(mode: AuthMode) {
    this.mode = mode;
    this.loginErrors = {};
    this.registerErrors = {};
    this.closeBanner();
  }

  // ---------------- LOGIN ----------------

  login() {
    this.loginErrors = {};

    if (!this.loginForm.identifier) {
      this.loginErrors.identifier = 'Введите email или username';
    }
    if (!this.loginForm.password) {
      this.loginErrors.password = 'Введите пароль';
    }

    if (Object.keys(this.loginErrors).length > 0) return;

    this.authService.login({
      login: this.loginForm.identifier,
      password: this.loginForm.password
    }).subscribe({
      next: () => {
        this.setBanner('Успешный вход!', 'success');
        this.router.navigate(['/company-select']);
      },
      error: (err: HttpErrorResponse) => {
        const msg = this.extractErrorMessage(err, 'Ошибка входа');
        this.setBanner(msg, 'error');
      }
    });
  }

  // ---------------- REGISTER ----------------

  register() {
    this.registerErrors = {};

    if (!this.registerForm.username) {
      this.registerErrors.username = 'Введите username';
    }
    if (!this.registerForm.email) {
      this.registerErrors.email = 'Введите email';
    }
    if (this.registerForm.password.length < 6) {
      this.registerErrors.password = 'Минимум 6 символов';
    }
    if (this.registerForm.password !== this.registerForm.confirmPassword) {
      this.registerErrors.confirmPassword = 'Пароли не совпадают';
    }

    if (Object.keys(this.registerErrors).length > 0) return;

    this.authService.register(this.registerForm).subscribe({
      next: () => {
        this.setBanner(
          'Аккаунт создан. Проверьте почту для подтверждения.',
          'success'
        );
        this.mode = 'login';
        this.loginForm.identifier = this.registerForm.email;
        this.registerForm = {
          username: '',
          email: '',
          password: '',
          confirmPassword: ''
        };
      },
      error: (err: HttpErrorResponse) => {
        const msg = this.extractErrorMessage(err, 'Ошибка регистрации');
        this.setBanner(msg, 'error');
      }
    });
  }
}
