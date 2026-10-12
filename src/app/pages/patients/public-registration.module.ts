import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PatientFormModule } from './patient-form.module';
import { PublicRegistrationRoutingModule } from './public-registration-routing.module';

@NgModule({
  imports: [
    CommonModule,
    PatientFormModule,
    PublicRegistrationRoutingModule
  ]
})
export class PublicRegistrationModule { }
