import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface CompanySettings {
  companyId: number;
  maxBookingDurationMinutes: number;
  maxBookingsPerUser: number;
  workEnd: string;
  workStart: string;
}

@Injectable({ providedIn: 'root' })
export class CompanySettingsService {

  private apiUrl = `${environment.apiUrl}`;

  constructor(private http: HttpClient) {}

  getSettings(): Observable<CompanySettings> {
    return this.http.get<CompanySettings>(`${this.apiUrl}/company_settings/getCompanySettings`);
  }
}
