import { Component } from '@angular/core';
import { NgbOffcanvas } from '@ng-bootstrap/ng-bootstrap';
import { SideMenuComponent } from 'src/app/side-menu/side-menu.component';
import { AuthService, UserRole } from 'src/app/service/auth.service';
import { PermissionService } from 'src/app/service/permission.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {

  constructor(
    private offcanvasService: NgbOffcanvas,
    public authService: AuthService,
    public permissions: PermissionService
  ) { }

  toggleSideMenu() {
    this.offcanvasService.open(SideMenuComponent, {
      scroll: true,
      position: 'start',
      panelClass: 'custom-offcanvas-width'
    });
  }

  hasAnyRole(roles: UserRole[]): boolean {
    return this.authService.hasAnyRole(roles);
  }

  getDisplayName(): string {
    return this.authService.getName() || this.authService.getLoginUserId() || 'User';
  }

  getInitials(): string {
    const parts = this.getDisplayName().trim().split(/\s+/).filter(Boolean);

    if (parts.length >= 2) {
      return `${parts[0][0]}${parts[1][0]}`.toUpperCase();
    }

    return (parts[0] || 'U').slice(0, 2).toUpperCase();
  }

  logout() {
    this.authService.logout();
  }
}
