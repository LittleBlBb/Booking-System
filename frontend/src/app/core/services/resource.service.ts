import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

export interface Resource {
  id: number;
  name: string;
  description: string;
  quantity: number;
  company_id: number;
  type_id: number;
}

@Injectable({ providedIn: 'root' })
export class ResourceService {

  private apiUrl = `${environment.apiUrl}`;

  constructor(private http: HttpClient) {}

  getResources(companyId: number) {
    return this.http.get<Resource[]>(`${this.apiUrl}/resources/${companyId}/findAll/`);
  }
}
