import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

export interface Company {
  id: number;
  name: string;
}

export interface JoinRequest {
  id: number;
  status: string;
  company: Company;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class CompanyService {

  private apiUrl = `${environment.apiUrl}`;

  constructor(private http: HttpClient) {}

  getAllCompanies() {
    return this.http.get<Company[]>(`${this.apiUrl}/companies/all`);
  }

  createCompany(name: string) {
    return this.http.post<Company>(`${this.apiUrl}/companies/create`, { name });
  }

  joinRequest(companyId: number) {
    return this.http.post<JoinRequest>(
      `${this.apiUrl}/company/join-request?id=${companyId}`,
      ''
    );
  }
}
