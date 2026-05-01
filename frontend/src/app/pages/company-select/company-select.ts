import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { CompanyService, Company } from '../../core/services/company.service';

type BannerType = 'success' | 'error' | 'info' | null;

@Component({
  selector: 'app-company-select',
  standalone: true,
  imports: [CommonModule, FormsModule],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  templateUrl: './company-select.html',
})
export class CompanySelectComponent implements OnInit {

  // --- Список компаний ---
  companies: Company[] = [];
  filteredCompanies: Company[] = [];
  searchQuery = '';

  // --- Создание компании ---
  newCompanyName = '';
  createError = '';

  // --- Состояние ---
  pendingCompanyId: number | null = null;
  loadingJoin: number | null = null;

  // --- Баннер ---
  banner: string | null = null;
  bannerKind: BannerType = null;
  private bannerTimer: any = null;

  constructor(
    private router: Router,
    private companyService: CompanyService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.loadCompanies();
  }

  // ---------------- COMPANIES ----------------

  loadCompanies() {
    this.companyService.getAllCompanies().subscribe({
      next: (list) => {
        this.companies = list;
        this.filteredCompanies = list;
        this.cdr.detectChanges();
      },
      error: () => {
        this.setBanner('Не удалось загрузить список компаний', 'error');
      }
    });
  }

  onSearch() {
    const q = this.searchQuery.toLowerCase().trim();
    this.filteredCompanies = q
      ? this.companies.filter(c => c.name.toLowerCase().includes(q))
      : this.companies;
  }

  // ---------------- CREATE ----------------

  createCompany() {
    this.createError = '';

    if (!this.newCompanyName.trim()) {
      this.createError = 'Введите название компании';
      return;
    }

    this.companyService.createCompany(this.newCompanyName.trim()).subscribe({
      next: () => {
        this.setBanner('Компания создана!', 'success');
        setTimeout(() => this.router.navigate(['/dashboard']), 1000);
      },
      error: (err: HttpErrorResponse) => {
        const msg = err.error?.message || 'Ошибка при создании компании';
        this.setBanner(msg, 'error');
      }
    });
  }

  // ---------------- JOIN ----------------

  joinRequest(company: Company) {
    this.loadingJoin = company.id;

    this.companyService.joinRequest(company.id).subscribe({
      next: () => {
        this.pendingCompanyId = company.id;
        this.loadingJoin = null;
        this.setBanner(`Заявка в «${company.name}» отправлена`, 'success');
        this.cdr.detectChanges();
      },
      error: (err: HttpErrorResponse) => {
        this.loadingJoin = null;
        const msg = err.error?.message || 'Ошибка при отправке заявки';
        this.setBanner(msg, 'error');
        this.cdr.detectChanges();
      }
    });
  }

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
