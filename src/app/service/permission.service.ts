import { Injectable } from '@angular/core';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class PermissionService {

  constructor(private authService: AuthService) { }

  canViewPatients(): boolean {
    return this.authService.hasAnyRole(['ADMIN', 'DOCTOR', 'NURSE', 'RECEPTIONIST']);
  }

  canRegisterPatients(): boolean {
    return this.authService.hasAnyRole(['ADMIN', 'RECEPTIONIST']);
  }

  canManagePatients(): boolean {
    return this.authService.hasAnyRole(['ADMIN']);
  }

  canManageSlots(): boolean {
    return this.authService.hasAnyRole(['ADMIN']);
  }

  canManageStaff(): boolean {
    return this.authService.hasAnyRole(['ADMIN']);
  }

  canViewAllAppointments(): boolean {
    return this.authService.hasAnyRole(['ADMIN', 'NURSE', 'RECEPTIONIST']);
  }

  canBookForAnyPatient(): boolean {
    return this.authService.hasAnyRole(['ADMIN', 'DOCTOR', 'RECEPTIONIST']);
  }

  canBookOwnAppointment(): boolean {
    return this.authService.hasAnyRole(['PATIENT']);
  }

  canUpdateConsultation(): boolean {
    return this.authService.hasAnyRole(['ADMIN', 'DOCTOR']);
  }
}
