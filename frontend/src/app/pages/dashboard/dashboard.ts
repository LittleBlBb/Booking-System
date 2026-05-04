import { Component, OnInit, ChangeDetectorRef, CUSTOM_ELEMENTS_SCHEMA, NgZone, ElementRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService, Me } from '../../core/services/auth.service';
import { ResourceService, Resource } from '../../core/services/resource.service';
import { BookingService, CompanySettings, Booking } from '../../core/services/booking.service';

type BannerType = 'success' | 'error' | null;

export interface ResourceStatus {
  label: 'Свободно' | 'Занято' | 'Скоро';
  nextLabel: string;
  colorClass: string;
  dotClass: string;
  btnClass: string;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  templateUrl: './dashboard.html',
})
export class DashboardComponent implements OnInit {

  @ViewChild('timelineEl') timelineEl!: ElementRef<HTMLDivElement>;

  me: Me | null = null;
  resources: Resource[] = [];
  filteredResources: Resource[] = [];
  settings: CompanySettings | null = null;
  allBookings: Booking[] = [];

  searchQuery = '';
  selectedType = '';
  allTypes: string[] = [];
  loading = true;

  // --- Booking modal ---
  showBookingModal = false;
  selectedResource: Resource | null = null;
  existingBookings: Booking[] = [];
  bookingLoading = false;
  bookingSubmitting = false;

  selectedDate = this.todayStr();

  sliderStart = 0;
  sliderEnd = 60;
  isDragging: 'start' | 'end' | 'move' | null = null;
  dragStartX = 0;
  dragSliderStart = 0;
  dragSliderEnd = 0;

  banner: string | null = null;
  bannerKind: BannerType = null;
  private bannerTimer: any = null;

  constructor(
    private router: Router,
    private authService: AuthService,
    private resourceService: ResourceService,
    private bookingService: BookingService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {}

  ngOnInit() {
    setTimeout(() => {
      this.ngZone.run(() => {
        this.authService.getMe().subscribe({
          next: (me) => {
            this.me = me;
            if (me.companyId) {
              this.loadAll(me.companyId);
            } else {
              this.loading = false;
            }
          },
          error: () => {
            this.authService.logout();
            this.router.navigate(['/auth']);
          }
        });
      });
    });
  }

  // ---------------- LOAD ----------------

  loadAll(companyId: number) {
    let resourcesDone = false;
    let bookingsDone = false;
    let settingsDone = false;

    const checkDone = () => {
      if (resourcesDone && bookingsDone && settingsDone) {
        this.loading = false;
        this.cdr.detectChanges();
      }
    };

    this.resourceService.getResources(companyId).subscribe({
      next: (list) => {
        this.resources = list;
        this.filteredResources = list;
        this.allTypes = [...new Set(list.map(r => String(r.name)))];
        resourcesDone = true;
        checkDone();
      },
      error: () => { resourcesDone = true; checkDone(); }
    });

    this.bookingService.getCompanyBookings(companyId).subscribe({
      next: (bookings) => {
        this.allBookings = bookings;
        bookingsDone = true;
        checkDone();
      },
      error: () => { bookingsDone = true; checkDone(); }
    });

    this.bookingService.getCompanySettings().subscribe({
      next: (s) => {
        this.settings = s;
        settingsDone = true;
        checkDone();
      },
      error: () => { settingsDone = true; checkDone(); }
    });
  }

  // ---------------- RESOURCE STATUS ----------------

  getResourceStatus(resource: Resource): ResourceStatus {
    const now = new Date();
    const bookings = this.allBookings
      .filter(b => b.resourceId === resource.id)
      .map(b => ({ start: new Date(b.startTime), end: new Date(b.endTime) }))
      .sort((a, b) => a.start.getTime() - b.start.getTime());

    // Активное прямо сейчас
    const active = bookings.find(b => b.start <= now && b.end > now);
    if (active) {
      const nextFree = active.end;
      const nextH = nextFree.getHours().toString().padStart(2, '0');
      const nextM = nextFree.getMinutes().toString().padStart(2, '0');
      return {
        label: 'Занято',
        nextLabel: `Свободно с ${nextH}:${nextM}`,
        colorClass: 'bg-red-50 text-red-500',
        dotClass: 'bg-red-500',
        btnClass: 'bg-gray-100 text-gray-400 cursor-not-allowed'
      };
    }

    // Скоро будет занято (в течение 15 минут)
    const soon = bookings.find(b => {
      const diffMs = b.start.getTime() - now.getTime();
      return diffMs > 0 && diffMs <= 15 * 60 * 1000;
    });
    if (soon) {
      const diffMin = Math.round((soon.start.getTime() - now.getTime()) / 60000);
      return {
        label: 'Скоро',
        nextLabel: `Занято через ${diffMin} мин`,
        colorClass: 'bg-amber-50 text-amber-500',
        dotClass: 'bg-amber-500',
        btnClass: 'border-2 border-indigo-600 text-indigo-600 hover:bg-indigo-50'
      };
    }

    // Свободно
    // Ищем ближайшее будущее бронирование
    const next = bookings.find(b => b.start > now);
    let nextLabel = 'Свободно сейчас';
    if (next) {
      const diffMin = Math.round((next.start.getTime() - now.getTime()) / 60000);
      if (diffMin < 60) {
        nextLabel = `Свободно ещё ${diffMin} мин`;
      } else {
        const h = next.start.getHours().toString().padStart(2, '0');
        const m = next.start.getMinutes().toString().padStart(2, '0');
        nextLabel = `Занято с ${h}:${m}`;
      }
    }

    return {
      label: 'Свободно',
      nextLabel,
      colorClass: 'bg-emerald-50 text-emerald-600',
      dotClass: 'bg-emerald-500 animate-pulse',
      btnClass: 'bg-indigo-600 text-white hover:opacity-90'
    };
  }

  canBook(resource: Resource): boolean {
    const status = this.getResourceStatus(resource);
    return status.label !== 'Занято' && resource.quantity > 0;
  }

  // ---------------- FILTERS ----------------

  onSearch() { this.applyFilters(); }

  selectType(type: string) {
    this.selectedType = this.selectedType === type ? '' : type;
    this.applyFilters();
  }

  applyFilters() {
    let result = this.resources;
    if (this.searchQuery.trim()) {
      const q = this.searchQuery.toLowerCase();
      result = result.filter(r => r.name.toLowerCase().includes(q) || r.description.toLowerCase().includes(q));
    }
    if (this.selectedType) {
      result = result.filter(r => String(r.type_id) === this.selectedType);
    }
    this.filteredResources = result;
  }

  // ---------------- BOOKING MODAL ----------------

  openBooking(resource: Resource) {
    this.selectedResource = resource;
    this.selectedDate = this.todayStr();
    this.showBookingModal = true;
    this.bookingLoading = true;

    // Начальный слайдер с учётом текущего времени
    this.sliderStart = 0;
    this.sliderEnd = Math.min(60, this.maxDurationMinutes() || 60);

    this.bookingService.getResourceBookings(resource.id).subscribe({
      next: (bookings) => {
        this.existingBookings = bookings;
        this.bookingLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.existingBookings = [];
        this.bookingLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  closeBookingModal() {
    this.showBookingModal = false;
    this.selectedResource = null;
    this.existingBookings = [];
    this.isDragging = null;
  }

  onDateChange() {
    if (!this.selectedResource) return;
    this.sliderStart = 0;
    this.sliderEnd = Math.min(60, this.maxDurationMinutes() || 60);
    this.bookingLoading = true;
    this.bookingService.getResourceBookings(this.selectedResource.id).subscribe({
      next: (bookings) => {
        this.existingBookings = bookings;
        this.bookingLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.existingBookings = [];
        this.bookingLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  submitBooking() {
    if (!this.selectedResource) return;

    const [wh, wm] = this.effectiveWorkStart().split(':').map(Number);
    const startTotal = wh * 60 + wm + this.sliderStart;
    const endTotal = wh * 60 + wm + this.sliderEnd;

    const fmt = (total: number) =>
      `${Math.floor(total / 60).toString().padStart(2, '0')}:${(total % 60).toString().padStart(2, '0')}`;

    const startTime = `${this.selectedDate}T${fmt(startTotal)}:00.000Z`;
    const endTime = `${this.selectedDate}T${fmt(endTotal)}:00.000Z`;

    this.bookingSubmitting = true;

    this.bookingService.createBooking({
      resourceId: this.selectedResource.id,
      startTime,
      endTime
    }).subscribe({
      next: (booking) => {
        this.bookingSubmitting = false;
        const name = this.selectedResource!.name;
        // Добавляем новое бронирование в локальный список
        this.allBookings.push(booking);
        this.closeBookingModal();
        this.setBanner(`Бронирование «${name}» подтверждено`, 'success');
      },
      error: (err) => {
        this.bookingSubmitting = false;
        const msg = err.error?.message || 'Ошибка при бронировании';
        this.closeBookingModal();
        this.setBanner(msg, 'error');
      }
    });
  }

  // ---------------- QUICK SELECT ----------------

  quickSelect(minutes: number) {
    const max = this.maxDurationMinutes();
    const duration = max ? Math.min(minutes, max) : minutes;
    const totalMins = this.workDurationMinutes();
    let newEnd = this.sliderStart + duration;
    if (newEnd > totalMins) {
      newEnd = totalMins;
      this.sliderStart = Math.max(0, newEnd - duration);
    }
    this.sliderEnd = newEnd;
    this.cdr.detectChanges();
  }

  // ---------------- TIMELINE DRAG ----------------

  onMouseDown(event: MouseEvent, handle: 'start' | 'end' | 'move') {
    this.isDragging = handle;
    this.dragStartX = event.clientX;
    this.dragSliderStart = this.sliderStart;
    this.dragSliderEnd = this.sliderEnd;
    event.preventDefault();
    event.stopPropagation();
  }

  onMouseMove(event: MouseEvent) {
    if (!this.isDragging || !this.timelineEl) return;

    const rect = this.timelineEl.nativeElement.getBoundingClientRect();
    const totalMins = this.workDurationMinutes();
    const pxPerMin = rect.width / totalMins;
    const deltaMins = Math.round((event.clientX - this.dragStartX) / pxPerMin / 15) * 15;
    const maxDur = this.maxDurationMinutes();

    if (this.isDragging === 'move') {
      const duration = this.dragSliderEnd - this.dragSliderStart;
      const newStart = Math.max(0, Math.min(this.dragSliderStart + deltaMins, totalMins - duration));
      this.sliderStart = newStart;
      this.sliderEnd = newStart + duration;
    } else if (this.isDragging === 'start') {
      let newStart = Math.max(0, Math.min(this.dragSliderStart + deltaMins, this.sliderEnd - 15));
      if (maxDur) newStart = Math.max(newStart, this.sliderEnd - maxDur);
      this.sliderStart = newStart;
    } else if (this.isDragging === 'end') {
      let newEnd = Math.min(totalMins, Math.max(this.dragSliderEnd + deltaMins, this.sliderStart + 15));
      if (maxDur) newEnd = Math.min(newEnd, this.sliderStart + maxDur);
      this.sliderEnd = newEnd;
    }

    this.cdr.detectChanges();
  }

  onMouseUp() { this.isDragging = null; }

  // ---------------- TIMELINE HELPERS ----------------

  // Для сегодня — начало от текущего часа, для других дней — workStart
  effectiveWorkStart(): string {
    const isToday = this.selectedDate === this.todayStr();
    if (!isToday) return this.workStartStr();

    const now = new Date();
    const nowH = now.getHours();
    const [wh] = this.workStartStr().split(':').map(Number);
    const [eh] = this.workEndStr().split(':').map(Number);

    if (nowH >= eh) return this.workEndStr(); // рабочий день закончился
    const effectiveH = Math.max(nowH, wh);
    return `${effectiveH.toString().padStart(2, '0')}:00`;
  }

  workStartStr(): string { return this.settings?.workStart?.slice(0, 5) || '08:00'; }
  workEndStr(): string { return this.settings?.workEnd?.slice(0, 5) || '20:00'; }

  workDurationMinutes(): number {
    const [sh, sm] = this.effectiveWorkStart().split(':').map(Number);
    const [eh, em] = this.workEndStr().split(':').map(Number);
    return Math.max(0, (eh * 60 + em) - (sh * 60 + sm));
  }

  maxDurationMinutes(): number { return this.settings?.maxBookingDurationMinutes || 0; }

  sliderStartPct(): number {
    const total = this.workDurationMinutes();
    return total > 0 ? (this.sliderStart / total) * 100 : 0;
  }

  sliderWidthPct(): number {
    const total = this.workDurationMinutes();
    return total > 0 ? ((this.sliderEnd - this.sliderStart) / total) * 100 : 0;
  }

  sliderStartTime(): string {
    const [wh, wm] = this.effectiveWorkStart().split(':').map(Number);
    const total = wh * 60 + wm + this.sliderStart;
    return `${Math.floor(total / 60).toString().padStart(2, '0')}:${(total % 60).toString().padStart(2, '0')}`;
  }

  sliderEndTime(): string {
    const [wh, wm] = this.effectiveWorkStart().split(':').map(Number);
    const total = wh * 60 + wm + this.sliderEnd;
    return `${Math.floor(total / 60).toString().padStart(2, '0')}:${(total % 60).toString().padStart(2, '0')}`;
  }

  durationLabel(): string {
    const mins = this.sliderEnd - this.sliderStart;
    const h = Math.floor(mins / 60);
    const m = mins % 60;
    if (h === 0) return `${m} мин`;
    if (m === 0) return `${h} ч`;
    return `${h} ч ${m} мин`;
  }

  timelineHours(): string[] {
    const [sh] = this.effectiveWorkStart().split(':').map(Number);
    const [eh] = this.workEndStr().split(':').map(Number);
    const hours = [];
    for (let h = sh; h <= eh; h++) {
      hours.push(`${h.toString().padStart(2, '0')}:00`);
    }
    return hours;
  }

  bookedSlots(): { leftPct: number; widthPct: number }[] {
    const totalMins = this.workDurationMinutes();
    if (totalMins <= 0) return [];

    const [wh, wm] = this.effectiveWorkStart().split(':').map(Number);
    const workStartMins = wh * 60 + wm;

    return this.existingBookings
      .filter(b => b.startTime.startsWith(this.selectedDate))
      .map(b => {
        const start = new Date(b.startTime);
        const end = new Date(b.endTime);
        const startMins = start.getHours() * 60 + start.getMinutes() - workStartMins;
        const endMins = end.getHours() * 60 + end.getMinutes() - workStartMins;
        return {
          leftPct: Math.max(0, (startMins / totalMins) * 100),
          widthPct: Math.max(0, ((endMins - startMins) / totalMins) * 100)
        };
      });
  }

  // ---------------- UTILS ----------------

  todayStr(): string { return new Date().toISOString().slice(0, 10); }
  goProfile() { this.router.navigate(['/profile']); }
  logout() { this.authService.logout(); this.router.navigate(['/auth']); }

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
