import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { authGuard } from 'src/app/service/auth.guard';
import { StaffManagementComponent } from './staff-management/staff-management.component';

const routes: Routes = [
  {
    path: '',
    component: StaffManagementComponent,
    canActivate: [authGuard],
    data: { roles: ['ADMIN'] }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class StaffRoutingModule { }
