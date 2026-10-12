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

  logout() {
    this.close();
    this.authService.logout();
  }
}
