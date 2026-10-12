import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgSelectModule } from '@ng-select/ng-select';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { PatientsRoutingModule } from './patients-routing.module';
import { SlotComponent } from './slot/slot.component';
import { AddSlotComponent } from './add-slot/add-slot.component';
import { PatientRegistrationComponent } from './patient-registration/patient-registration.component';
import { PatientFormModule } from './patient-form.module';


@NgModule({
  declarations: [
    SlotComponent,
    AddSlotComponent,
    PatientRegistrationComponent
  ],
  imports: [
    CommonModule,
    PatientsRoutingModule,
    NgSelectModule,
    ReactiveFormsModule,
    FormsModule,
    PatientFormModule
  ]
})
export class PatientsModule { }
