import { Component, OnInit, ChangeDetectorRef, CUSTOM_ELEMENTS_SCHEMA, ElementRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService, Me } from '../../core/services/auth.service';
import { BookingService, Booking } from '../../core/services/booking.service';
import { ResourceService, Resource } from '../../core/services/resource.service';
import { HttpErrorResponse } from '@angular/common/http';

type BannerType = 'success' | 'error' | null;

@Component({
  selector: 'app-my-bookings',
  standalone: true,
  imports: [CommonModule],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  templateUrl: './my-bookings.html',
})
export class MyBookingsComponent implements OnInit {

  @ViewChild('mainEl') mainEl!: ElementRef<HTMLElement>;

  me: Me | null = null;
  resources: Resource[] = [];

  bookings: Booking[] = [];
  currentPage = 0;
  isLastPage = false;
  loadingMore = false;
  loading = true;

  showDetailModal = false;
  detailBooking: Booking | null = null;
  detailLoading = false;

  showCancelModal = false;
  cancelTarget: Booking | null = null;
  cancellingId: number | null = null;

  banner: string | null = null;
  bannerKind: BannerType = null;
  private bannerTimer: any = null;

  constructor(
    private authService: AuthService,
    private bookingService: BookingService,
    private resourceService: ResourceService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    setTimeout(() => {
      this.authService.getMe().subscribe({
        next: (me) => {
          this.me = me;
          this.loadResources(me);
          this.loadBookings();
        },
        error: () => {
          this.authService.logout();
          this.router.navigate(['/auth']);
        }
      });
    });
  }

  loadResources(me: Me) {
    if (!me.companyId) return;
    this.resourceService.getResources(me.companyId).subscribe({
      next: (r) => { this.resources = r; this.cdr.detectChanges(); }
    });
  }

  loadBookings() {
    if (!this.me) return;
    this.loadingMore = true;

    this.bookingService.getUserBookings(this.me.id, this.currentPage).subscribe({
      next: (page) => {
        this.bookings = [...this.bookings, ...page.content];
        this.isLastPage = page.last;
        this.loading = false;
        this.loadingMore = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
        this.loadingMore = false;
        this.cdr.detectChanges();
      }
    });
  }

  onMainScroll(event: Event) {
    if (this.isLastPage || this.loadingMore) return;
    const el = event.target as HTMLElement;
    if (el.scrollTop + el.clientHeight >= el.scrollHeight - 200) {
      this.currentPage++;
      this.loadBookings();
    }
  }

  // ---------------- HELPERS ----------------

  getResourceName(resourceId: number): string {
    return this.resources.find(r => r.id === resourceId)?.name || `Ресурс #${resourceId}`;
  }

  get activeBookings(): Booking[] {
    return this.bookings.filter(b => b.status === 'ACTIVE');
  }

  get historyBookings(): Booking[] {
    return this.bookings.filter(b => b.status === 'EXPIRED' || b.status === 'CANCELED');
  }

  get activeHoursThisMonth(): number {
    const now = new Date();
    const monthStart = new Date(now.getFullYear(), now.getMonth(), 1);
    const monthEnd = new Date(now.getFullYear(), now.getMonth() + 1, 1);
    return this.bookings
      .filter(b => b.status !== 'CANCELED')
      .filter(b => { const s = new Date(b.startTime); return s >= monthStart && s < monthEnd; })
      .reduce((sum, b) => sum + (new Date(b.endTime).getTime() - new Date(b.startTime).getTime()) / 3600000, 0);
  }

  formatDate(dateStr: string): string {
    return new Date(dateStr).toLocaleDateString('ru-RU', { day: 'numeric', month: 'short', year: 'numeric' });
  }

  formatTime(startStr: string, endStr: string): string {
    const start = new Date(startStr);
    const end = new Date(endStr);
    const fmt = (d: Date) => `${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`;
    const diffMin = Math.round((end.getTime() - start.getTime()) / 60000);
    const h = Math.floor(diffMin / 60);
    const m = diffMin % 60;
    const dur = h > 0 && m > 0 ? `${h}ч ${m}м` : h > 0 ? `${h}ч` : `${m}м`;
    return `${fmt(start)} - ${fmt(end)} (${dur})`;
  }

  statusLabel(status: string): string {
    return ({ ACTIVE: 'Активно', EXPIRED: 'Завершено', CANCELED: 'Отменено' } as any)[status] || status;
  }

  statusClass(status: string): string {
    return ({ ACTIVE: 'bg-emerald-50 text-emerald-700', EXPIRED: 'bg-gray-100 text-gray-500', CANCELED: 'bg-red-50 text-red-500' } as any)[status] || 'bg-gray-100 text-gray-500';
  }

  isCancellable(booking: Booking): boolean {
    return booking.status === 'ACTIVE' && new Date(booking.endTime) > new Date();
  }

  // ---------------- DETAIL ----------------

  openDetail(booking: Booking) {
    this.detailBooking = booking;
    this.showDetailModal = true;
    this.detailLoading = true;
    this.bookingService.getBookingById(booking.id).subscribe({
      next: (b) => { this.detailBooking = b; this.detailLoading = false; this.cdr.detectChanges(); },
      error: () => { this.detailLoading = false; this.cdr.detectChanges(); }
    });
  }

  closeDetailModal() { this.showDetailModal = false; this.detailBooking = null; }

  // ---------------- CANCEL ----------------

  openCancelModal(event: Event, booking: Booking) {
    event.stopPropagation();
    this.cancelTarget = booking;
    this.showCancelModal = true;
  }

  closeCancelModal() { this.showCancelModal = false; this.cancelTarget = null; }

  confirmCancel() {
    if (!this.cancelTarget) return;
    const id = this.cancelTarget.id;
    this.cancellingId = id;
    this.closeCancelModal();

    this.bookingService.cancelBooking(id).subscribe({
      next: (updated) => {
        const idx = this.bookings.findIndex(b => b.id === id);
        if (idx !== -1) this.bookings[idx] = updated;
        if (this.detailBooking?.id === id) this.detailBooking = updated;
        this.cancellingId = null;
        this.setBanner('Бронирование отменено', 'success');
      },
      error: (err: HttpErrorResponse) => {
        this.cancellingId = null;
        this.setBanner(err.error?.message || 'Ошибка при отмене', 'error');
      }
    });
  }

  // ---------------- NAV ----------------

  goDashboard() { this.router.navigate(['/dashboard']); }
  goProfile() { this.router.navigate(['/profile']); }
  logout() { this.authService.logout(); this.router.navigate(['/auth']); }

  // ---------------- BANNER ----------------

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
  }
}
