import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export interface CompanySettings {
  companyId: number | null;
  maxBookingDurationMinutes: number | null;
  maxBookingsPerUser: number | null;
  workStart: string | null;
  workEnd: string | null;
}

export interface Booking {
  resourceId: number;
  userId: number;
  startTime: string;
  endTime: string;
  status: string;
}

export interface CreateBookingRequest {
  resourceId: number;
  startTime: string;
  endTime: string;
}

@Injectable({ providedIn: 'root' })
export class BookingService {

  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  getCompanySettings() {
    return this.http.get<CompanySettings>(`${this.apiUrl}/company_settings/getCompanySettings`);
  }

  getCompanyBookings(companyId: number) {
    return this.http.get<Booking[]>(`${this.apiUrl}/bookings/${companyId}/all?status=ACTIVE`);
  }

  getResourceBookings(resourceId: number) {
    return this.http.get<Booking[]>(`${this.apiUrl}/bookings/${resourceId}/bookings?status=ACTIVE`);
  }

  createBooking(data: CreateBookingRequest) {
    return this.http.post<Booking>(`${this.apiUrl}/bookings/create`, data);
  }
}
