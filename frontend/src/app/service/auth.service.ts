import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from "@angular/common/http";
import { environment } from 'src/environments/environment';

export type UserRole = 'ADMIN' | 'DOCTOR' | 'NURSE' | 'RECEPTIONIST' | 'PATIENT';

@Injectable({ providedIn: 'root' })
export class AuthService {


  constructor(private _http: HttpClient,
    private _router: Router
  ) { }

  private baseUrl = `${environment.apiBaseUrl}/`;

  logout() {
    localStorage.removeItem('isLoggedIn');
    localStorage.removeItem('token');
    localStorage.removeItem('tokenType');
    localStorage.removeItem('userId');
    localStorage.removeItem('role');
    localStorage.removeItem('name');
    localStorage.removeItem('status');
    this._router.navigate(['/login']);
  }

  isLoggedIn(): boolean {
    return !!this.getToken() && localStorage.getItem('isLoggedIn') === 'true';
  }

  login(loginDTO: any) {
    return this._http.post(`${this.baseUrl}` + "login/loginUser", loginDTO);
  }

  getLoginUserId() {
    return localStorage.getItem('userId');
  }

  getToken() {
    return localStorage.getItem('token');
  }

  getTokenType() {
    return localStorage.getItem('tokenType') || 'Bearer';
  }

  getRole(): UserRole | null {
    return localStorage.getItem('role') as UserRole | null;
  }

  getName(): string {
    return localStorage.getItem('name') || '';
  }

  hasAnyRole(roles: UserRole[]): boolean {
    const role = this.getRole();
    return !!role && roles.includes(role);
  }
}
