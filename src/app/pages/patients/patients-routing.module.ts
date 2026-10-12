import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AddPatientComponent } from './add-patient/add-patient.component';
import { AddSlotComponent } from './add-slot/add-slot.component';
import { PatientRegistrationComponent } from './patient-registration/patient-registration.component';
import { SlotComponent } from './slot/slot.component';
import { authGuard } from 'src/app/service/auth.guard';

const routes: Routes = [
  { path: '', component: PatientRegistrationComponent, canActivate: [authGuard], data: { roles: ['ADMIN', 'DOCTOR', 'NURSE', 'RECEPTIONIST'] } },
  { path: 'new', component: AddPatientComponent, canActivate: [authGuard], data: { roles: ['ADMIN', 'RECEPTIONIST'] } },
  { path: 'addPatient', redirectTo: 'new' },
  { path: 'slots', component: SlotComponent, canActivate: [authGuard], data: { roles: ['ADMIN'] } },
  { path: 'slots/new', component: AddSlotComponent, canActivate: [authGuard], data: { roles: ['ADMIN'] } },
  { path: 'slots/addSlot', redirectTo: 'slots/new' },
  { path: 'slots/:id/edit', component: AddSlotComponent, canActivate: [authGuard], data: { roles: ['ADMIN'] } },
  { path: 'slots/:id', component: AddSlotComponent, canActivate: [authGuard], data: { roles: ['ADMIN'] } },
  { path: ':id/edit', component: AddPatientComponent, canActivate: [authGuard], data: { roles: ['ADMIN'] } },
  { path: ':id', component: AddPatientComponent, canActivate: [authGuard], data: { roles: ['ADMIN'] } }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PatientsRoutingModule { }
