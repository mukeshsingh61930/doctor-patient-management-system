import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class StaffService {

  private baseUrl = `${environment.apiBaseUrl}/user`;

  constructor(private http: HttpClient) { }

  getStaffUsers() {
    return this.http.get(`${environment.apiBaseUrl}/staff`);
  }

  getDoctors() {
    return this.http.get(`${environment.apiBaseUrl}/doctors/options`);
  }

  getDoctorByUserId(userId: string) {
    return this.http.get(`${environment.apiBaseUrl}/doctors/by-user/${userId}`);
  }

  createStaffUser(user: any) {
    return this.http.post(`${environment.apiBaseUrl}/staff`, user);
  }

  updateStaffUser(id: number, user: any) {
    return this.http.put(`${environment.apiBaseUrl}/staff/${id}`, user);
  }

  updateStaffStatus(id: number, status: 'ACTIVE' | 'INACTIVE') {
    return this.http.patch(`${environment.apiBaseUrl}/staff/${id}/status/${status}`, {});
  }
}
