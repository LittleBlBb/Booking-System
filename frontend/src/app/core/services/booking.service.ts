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
  id: number;
  resourceId: number;
  userId: number;
  startTime: string;
  endTime: string;
  status: string;
}

export interface BookingPage {
  content: Booking[];
  totalPages: number;
  totalElements: number;
  last: boolean;
  number: number;
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

  getUserBookings(userId: number, page: number = 0, size: number = 20) {
    return this.http.get<BookingPage>(
      `${this.apiUrl}/bookings/${userId}/allByUser?page=${page}&size=${size}&sort=id&sort=desc`
    );
  }

  getBookingById(bookingId: number) {
    return this.http.get<Booking>(`${this.apiUrl}/bookings/getById?id=${bookingId}`);
  }

  createBooking(data: CreateBookingRequest) {
    return this.http.post<Booking>(`${this.apiUrl}/bookings/create`, data);
  }

  cancelBooking(bookingId: number) {
    return this.http.patch<Booking>(`${this.apiUrl}/bookings/cancel?id=${bookingId}`, null);
  }
}
