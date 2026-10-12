import { Component, OnInit } from '@angular/core';
import { AuthService } from '../service/auth.service';
import { CommonService } from '../service/common.service';
import { PermissionService } from '../service/permission.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  dashboard = {
    totalPatients: 0,
    todayAppointments: 0,
    pendingAppointments: 0,
    bookedAppointments: 0,
    upcomingAppointments: [] as any[]
  };

  constructor(
    private commonService: CommonService,
    private authService: AuthService,
    public permissions: PermissionService
  ) { }

  ngOnInit(): void {
    this.loadDashboard();
  }

  loadDashboard() {
    if (this.authService.getRole() === 'DOCTOR') {
      this.loadDoctorDashboard();
      return;
    }

    if (!this.permissions.canViewAllAppointments()) {
      this.loadPatientDashboard();
      return;
    }

    this.commonService.getDashboard().subscribe({
      next: (response: any) => {
        this.dashboard = {
          totalPatients: response?.totalPatients || 0,
          todayAppointments: response?.todayAppointments || 0,
          pendingAppointments: response?.pendingAppointments || 0,
          bookedAppointments: response?.bookedAppointments || 0,
          upcomingAppointments: response?.upcomingAppointments || []
        };
      },
      error: (err) => console.log('error loading dashboard', err)
    });
  }

  private loadPatientDashboard() {
    const userId = this.authService.getLoginUserId();
    if (!userId) {
      return;
    }

    this.commonService.getPatientDashboard(userId).subscribe({
      next: (response: any) => {
        this.dashboard = {
          totalPatients: response?.totalPatients || 1,
          todayAppointments: response?.todayAppointments || 0,
          pendingAppointments: response?.pendingAppointments || 0,
          bookedAppointments: response?.bookedAppointments || 0,
          upcomingAppointments: response?.upcomingAppointments || []
        };
      },
      error: (err) => console.log('error loading patient dashboard', err)
    });
  }

  private loadDoctorDashboard() {
    const userId = this.authService.getLoginUserId();
    if (!userId) {
      return;
    }

    this.commonService.getDoctorDashboard(userId).subscribe({
      next: (response: any) => {
        this.dashboard = {
          totalPatients: response?.totalPatients || 0,
          todayAppointments: response?.todayAppointments || 0,
          pendingAppointments: response?.pendingAppointments || 0,
          bookedAppointments: response?.bookedAppointments || 0,
          upcomingAppointments: response?.upcomingAppointments || []
        };
      },
      error: (err) => console.log('error loading doctor dashboard', err)
    });
  }

  getPrimaryMetricLabel(): string {
    const role = this.authService.getRole();
    if (role === 'DOCTOR') {
      return 'My Patients';
    }
    if (role === 'PATIENT') {
      return 'My Profile';
    }
    return 'Total Patients';
  }
}
