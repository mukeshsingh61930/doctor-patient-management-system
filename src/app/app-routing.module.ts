import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { AppointmentComponent } from './pages/appointment/appointment.component';
import { authGuard } from './service/auth.guard';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: 'register',
    loadChildren: () => import('./pages/patients/public-registration.module').then(m => m.PublicRegistrationModule)
  },
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  {
    path: 'dashboard',
    loadChildren: () => import('./home/home.module').then(m => m.HomeModule),
    canActivate: [authGuard],
    data: { roles: ['ADMIN', 'DOCTOR', 'NURSE', 'RECEPTIONIST', 'PATIENT'] }
  },
  {
    path: 'patients',
    loadChildren: () => import('./pages/patients/patients.module').then(m => m.PatientsModule),
    canActivate: [authGuard],
    data: { roles: ['ADMIN', 'DOCTOR', 'NURSE', 'RECEPTIONIST'] }
  },
  {
    path: 'staff',
    loadChildren: () => import('./pages/staff/staff.module').then(m => m.StaffModule),
    canActivate: [authGuard],
    data: { roles: ['ADMIN'] }
  },
  {
    path: 'appointments',
    component: AppointmentComponent,
    canActivate: [authGuard],
    data: { roles: ['ADMIN', 'DOCTOR', 'NURSE', 'RECEPTIONIST', 'PATIENT'] }
  },
  { path: 'home', redirectTo: 'dashboard' },
  { path: 'appointment', redirectTo: 'appointments' },
  { path: 'slot/addSlot', redirectTo: 'patients/slots/new' },
  { path: 'slot/:id', redirectTo: 'patients/slots/:id/edit' },
  { path: 'slot', redirectTo: 'patients/slots', pathMatch: 'full' },
  { path: '**', redirectTo: 'dashboard' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
