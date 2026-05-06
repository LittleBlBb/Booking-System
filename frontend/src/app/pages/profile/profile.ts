import { Component, CUSTOM_ELEMENTS_SCHEMA, ChangeDetectorRef } from '@angular/core';
import { CommonModule, AsyncPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { AuthService, Me } from '../../core/services/auth.service';
import { Observable, Subject, switchMap, tap, startWith } from 'rxjs';

type BannerType = 'success' | 'error' | null;

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, AsyncPipe],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  templateUrl: './profile.html',
})
export class ProfileComponent {

  private refresh$ = new Subject<void>();

  me$: Observable<Me> = this.refresh$.pipe(
    startWith(null),
    switchMap(() => this.authService.getMe()),
    tap(me => {
      this.form.username = me.username;
      this.form.email = me.email;
    })
  );

  form = {
    username: '',
    email: '',
    currentPassword: '',
    password: '',
    confirmPassword: ''
  };

  formErrors: any = {};

  banner: string | null = null;
  bannerKind: BannerType = null;
  private bannerTimer: any = null;

  saving = false;
  showLeaveModal = false;
  leaving = false;

  private apiUrl = 'http://localhost:8080/api';

  constructor(
    private authService: AuthService,
    private http: HttpClient,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  // ---------------- LEAVE COMPANY ----------------

  openLeaveModal() {
    this.showLeaveModal = true;
  }

  closeLeaveModal() {
    this.showLeaveModal = false;
  }

  leaveCompany() {
    this.leaving = true;

    this.http.delete<Me>(`${this.apiUrl}/users/leave`).subscribe({
      next: () => {
        this.leaving = false;
        this.showLeaveModal = false;
        this.cdr.detectChanges();
        this.router.navigate(['/company-select']);
      },
      error: (err: HttpErrorResponse) => {
        this.leaving = false;
        this.showLeaveModal = false;
        const msg = err.error?.message || 'Ошибка при выходе из организации';
        this.setBanner(msg, 'error');
      }
    });
  }

  // ---------------- SAVE ----------------

  save() {
    this.formErrors = {};

    if (!this.form.username.trim()) {
      this.formErrors.username = 'Введите имя пользователя';
    }
    if (!this.form.email.trim()) {
      this.formErrors.email = 'Введите email';
    }
    if (!this.form.currentPassword) {
      this.formErrors.currentPassword = 'Введите текущий пароль';
    }
    if (this.form.password && this.form.password.length < 6) {
      this.formErrors.password = 'Минимум 6 символов';
    }
    if (this.form.password && this.form.password !== this.form.confirmPassword) {
      this.formErrors.confirmPassword = 'Пароли не совпадают';
    }

    if (Object.keys(this.formErrors).length > 0) return;

    this.saving = true;

    const body = {
      username: this.form.username,
      email: this.form.email,
      currentPassword: this.form.currentPassword,
      password: this.form.password || this.form.currentPassword,
      confirmPassword: this.form.password || this.form.currentPassword,
    };

    this.http.put<Me>(`${this.apiUrl}/users/updateUser`, body).subscribe({
      next: () => {
        this.authService.login({
          login: this.form.username,
          password: this.form.password || this.form.currentPassword
        }).subscribe({
          next: () => {
            this.form.currentPassword = '';
            this.form.password = '';
            this.form.confirmPassword = '';
            this.saving = false;
            this.setBanner('Профиль обновлён', 'success');
            this.refresh$.next();
          },
          error: () => {
            this.form.currentPassword = '';
            this.form.password = '';
            this.form.confirmPassword = '';
            this.saving = false;
            this.setBanner('Данные сохранены, но потребуется повторный вход', 'success');
            this.refresh$.next();
          }
        });
      },
      error: (err: HttpErrorResponse) => {
        this.saving = false;
        const msg = err.error?.message || 'Ошибка при сохранении';
        this.setBanner(msg, 'error');
      }
    });
  }

  // ---------------- UTILS ----------------

  logout() {
    this.authService.logout();
    this.router.navigate(['/auth']);
  }

  goDashboard() {
    this.router.navigate(['/dashboard']);
  }

  setBanner(message: string, type: BannerType) {
    if (this.bannerTimer) clearTimeout(this.bannerTimer);
    this.banner = message;
    this.bannerKind = type;
    this.cdr.detectChanges();
    this.bannerTimer = setTimeout(() => {
      this.banner = null;
      this.bannerKind = null;
      this.cdr.detectChanges();
      this.bannerTimer = null;
    }, 5000);
  }

  closeBanner() {
    if (this.bannerTimer) clearTimeout(this.bannerTimer);
    this.banner = null;
    this.bannerKind = null;
    this.cdr.detectChanges();
  }

  goMyBookings() { this.router.navigate(['/my-bookings']); }
}
