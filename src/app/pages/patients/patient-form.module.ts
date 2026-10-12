import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NgSelectModule } from '@ng-select/ng-select';
import { AddPatientComponent } from './add-patient/add-patient.component';

@NgModule({
  declarations: [
    AddPatientComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    NgSelectModule
  ],
  exports: [
    AddPatientComponent
  ]
})
export class PatientFormModule { }
