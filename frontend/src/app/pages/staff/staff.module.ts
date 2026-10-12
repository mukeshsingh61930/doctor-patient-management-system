import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { StaffRoutingModule } from './staff-routing.module';
import { StaffManagementComponent } from './staff-management/staff-management.component';

@NgModule({
  declarations: [
    StaffManagementComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    StaffRoutingModule
  ]
})
export class StaffModule { }
