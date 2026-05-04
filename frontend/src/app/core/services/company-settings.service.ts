import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CompanySettings {
  companyId: number;
  maxBookingDurationMinutes: number;
  maxBookingsPerUser: number;
  workEnd: string;
  workStart: string;
}

@Injectable({ providedIn: 'root' })
export class CompanySettingsService {
  private base = 'http://localhost:8080/api/company_settings';

  constructor(private http: HttpClient) {}

  getSettings(): Observable<CompanySettings> {
    return this.http.get<CompanySettings>(`${this.base}/getCompanySettings`);
  }
}
