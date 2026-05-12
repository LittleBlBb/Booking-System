import { Component, OnInit, OnDestroy, ChangeDetectorRef, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';

type State = 'loading' | 'success' | 'error';

@Component({
  selector: 'app-activate',
  standalone: true,
  imports: [CommonModule],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  templateUrl: './activate.html',
})
export class ActivateComponent implements OnInit, OnDestroy {
  state: State = 'loading';
  countdown = 5;
  errorMessage = 'Ссылка недействительна или уже была использована.';

  private countdownInterval: any = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    const token = this.route.snapshot.paramMap.get('token');
    if (!token) {
      this.state = 'error';
      this.cdr.detectChanges();
      return;
    }

    this.http.get<{ message: string }>(`http://localhost:8080/api/users/activate/${token}`)
      .subscribe({
        next: () => {
          this.state = 'success';
          this.cdr.detectChanges();
          this.startCountdown();
        },
        error: (err) => {
          this.state = 'error';
          this.errorMessage = err?.error?.message || 'Ссылка недействительна или уже была использована.';
          this.cdr.detectChanges();
        }
      });
  }

  private startCountdown() {
    this.countdownInterval = setInterval(() => {
      this.countdown--;
      this.cdr.detectChanges();
      if (this.countdown <= 0) {
        clearInterval(this.countdownInterval);
        this.router.navigate(['/auth']);
      }
    }, 1000);
  }

  goToAuth() {
    if (this.countdownInterval) clearInterval(this.countdownInterval);
    this.router.navigate(['/auth']);
  }

  ngOnDestroy() {
    if (this.countdownInterval) clearInterval(this.countdownInterval);
  }
}
