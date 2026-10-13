import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserRole } from 'src/app/service/auth.service';
import { CommonService } from 'src/app/service/common.service';
import { StaffService } from 'src/app/service/staff.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-staff-management',
  templateUrl: './staff-management.component.html',
  styleUrls: ['./staff-management.component.css']
})
export class StaffManagementComponent implements OnInit {

  staffForm!: FormGroup;
  staffProfiles: any[] = [];
  roleOptions: UserRole[] = ['ADMIN', 'DOCTOR', 'NURSE', 'RECEPTIONIST'];
  editingStaffId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private staffService: StaffService,
    private commonService: CommonService
  ) { }

  ngOnInit(): void {
    this.staffForm = this.fb.group({
      name: ['', Validators.required],
      userId: ['', Validators.required],
      mobNo: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
      password: ['', Validators.required],
      role: ['DOCTOR', Validators.required],
      status: ['ACTIVE'],
      employeeCode: [''],
      department: [''],
      designation: [''],
      qualification: [''],
      joiningDate: [''],
      address: [''],
      specialization: [''],
      registrationNo: [''],
      consultationFee: [null],
      shift: [''],
      ward: ['']
    });

    this.loadStaffUsers();
  }

  loadStaffUsers() {
    this.staffService.getStaffUsers().subscribe({
      next: (response: any) => {
        this.staffProfiles = response || [];
      },
      error: (err) => console.log('error loading staff users', err)
    });
  }

  saveStaffUser() {
    if (this.staffForm.invalid) {
      this.staffForm.markAllAsTouched();
      return;
    }

    const request = this.editingStaffId
      ? this.staffService.updateStaffUser(this.editingStaffId, this.staffForm.value)
      : this.staffService.createStaffUser(this.staffForm.value);

    request.subscribe({
      next: () => {
        Swal.fire({
          icon: 'success',
          title: this.editingStaffId ? 'Updated!' : 'Created!',
          text: this.editingStaffId ? 'Staff profile updated successfully.' : 'Staff profile created successfully.',
          timer: 1600,
          showConfirmButton: false
        });
        this.resetForm();
        this.loadStaffUsers();
      },
      error: (err) => {
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: this.commonService.getErrorMessage(err, 'Unable to save staff profile.')
        });
      }
    });
  }

  editStaff(staffProfile: any) {
    this.editingStaffId = staffProfile.staffId;
    this.staffForm.reset({
      name: staffProfile.user?.name || '',
      userId: staffProfile.user?.userId || '',
      mobNo: staffProfile.user?.mobNo || '',
      password: '',
      role: staffProfile.user?.role || 'DOCTOR',
      status: staffProfile.status || staffProfile.user?.status || 'ACTIVE',
      employeeCode: staffProfile.employeeCode || '',
      department: staffProfile.department || '',
      designation: staffProfile.designation || '',
      qualification: staffProfile.qualification || '',
      joiningDate: staffProfile.joiningDate || '',
      address: staffProfile.address || '',
      specialization: staffProfile.doctorProfile?.specialization || '',
      registrationNo: staffProfile.doctorProfile?.registrationNo || '',
      consultationFee: staffProfile.doctorProfile?.consultationFee || null,
      shift: staffProfile.nurseProfile?.shift || '',
      ward: staffProfile.nurseProfile?.ward || ''
    });
  }

  resetForm() {
    this.editingStaffId = null;
    this.staffForm.reset({
      role: 'DOCTOR',
      status: 'ACTIVE'
    });
  }

  toggleStatus(staffProfile: any) {
    const nextStatus = staffProfile.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';

    this.staffService.updateStaffStatus(staffProfile.staffId, nextStatus).subscribe({
      next: () => {
        this.loadStaffUsers();
      },
      error: (err) => {
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: this.commonService.getErrorMessage(err, 'Unable to update staff status.')
        });
      }
    });
  }

  isDoctorRole(): boolean {
    return this.staffForm.get('role')?.value === 'DOCTOR';
  }

  isNurseRole(): boolean {
    return this.staffForm.get('role')?.value === 'NURSE';
  }
}
