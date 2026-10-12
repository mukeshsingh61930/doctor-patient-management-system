import { Component } from '@angular/core';
import { NgbActiveOffcanvas } from '@ng-bootstrap/ng-bootstrap';
import { AuthService, UserRole } from '../service/auth.service';
import { PermissionService } from '../service/permission.service';

@Component({
  selector: 'app-side-menu',
  templateUrl: './side-menu.component.html',
  styleUrls: ['./side-menu.component.css']
})
export class SideMenuComponent {
  constructor(
    public activeOffcanvas: NgbActiveOffcanvas,
    public authService: AuthService,
    public permissions: PermissionService
  ) { }

  close() {
    this.activeOffcanvas.close();
  }

  hasAnyRole(roles: UserRole[]): boolean {
    return this.authService.hasAnyRole(roles);
  }

  getDisplayName(): string {
    return this.authService.getName() || this.authService.getLoginUserId() || 'Clinic User';
  }

  getRole(): string {
    return this.authService.getRole() || 'USER';
  }

  getInitials(): string {
    const nameParts = this.getDisplayName().trim().split(/\s+/);
    const initials = nameParts.slice(0, 2).map(part => part.charAt(0)).join('');
    return (initials || 'DP').toUpperCase();
  }

  logout() {
    this.close();
    this.authService.logout();
  }
}
