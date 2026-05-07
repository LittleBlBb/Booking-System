import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CompanySettings } from './booking.service';
import { Me } from './auth.service';
import { Resource } from './resource.service';

export interface CompanyUser {
  id: number;
  username: string;
  email: string;
  role: string;
  companyId: number | null;
  companyName: string | null;
}

export interface ResourceType {
  id: number;
  name: string;
}

@Injectable({ providedIn: 'root' })
export class AdminService {

  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  // --- Settings ---
  getSettings() {
    return this.http.get<CompanySettings>(`${this.apiUrl}/company_settings/getCompanySettings`);
  }

  createSettings(data: Partial<CompanySettings>) {
    return this.http.post<CompanySettings>(`${this.apiUrl}/company_settings/addSettings`, data);
  }

  updateSettings(data: Partial<CompanySettings>) {
    return this.http.patch<CompanySettings>(`${this.apiUrl}/company_settings/updateSettings`, data);
  }

  // --- Users ---
  getCompanyUsers(companyId: number) {
    return this.http.get<CompanyUser[]>(`${this.apiUrl}/companies/${companyId}/users`);
  }

  updateUserRole(userId: number, role: string) {
    return this.http.put<CompanyUser>(`${this.apiUrl}/users/updateUserRole`, { userId, role });
  }

  removeUserFromCompany(userId: number) {
    return this.http.delete<CompanyUser>(`${this.apiUrl}/users/deleteUserFromCompany?id=${userId}`);
  }

  // --- Resource types ---
  getResourceTypes() {
    return this.http.get<ResourceType[]>(`${this.apiUrl}/resource_types/findAll`);
  }

  createResourceType(name: string, companyId: number) {
    return this.http.post<ResourceType>(`${this.apiUrl}/resource_types/addResourceType`, { name, companyId });
  }

  updateResourceType(id: number, name: string) {
    return this.http.put<ResourceType>(`${this.apiUrl}/resource_types/update`, { id, name });
  }

  deleteResourceType(id: number) {
    return this.http.delete(`${this.apiUrl}/resource_types/delete?id=${id}`);
  }

  // --- Resources ---
  addResource(data: { name: string; description: string; resourceTypeId: number; companyId: number; quantity: number }) {
    return this.http.post<Resource>(`${this.apiUrl}/resources/addResource`, data);
  }

  editResource(data: { id: number; name: string; description: string; resourceTypeId: number; companyId: number; quantity: number }) {
    return this.http.put<Resource>(`${this.apiUrl}/resources/editResource`, data);
  }

  deleteResource(id: number) {
    return this.http.delete(`${this.apiUrl}/resources/deleteById?id=${id}`);
  }
}
