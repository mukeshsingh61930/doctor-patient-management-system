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

  logout() {
    this.authService.logout();
  }
}
