import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Observable } from 'rxjs';
import { CommonService } from 'src/app/service/common.service';
import { PatientService } from 'src/app/service/patient.service';
import { PermissionService } from 'src/app/service/permission.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-patient-registration',
  templateUrl: './patient-registration.component.html',
  styleUrls: ['./patient-registration.component.css']
})
export class PatientRegistrationComponent {

  patientList: any;
  stateList: any[] = [];
  cityList: any[] = [];
  constructor(private patientService: PatientService,
    private commonService: CommonService,
    public permissions: PermissionService
  ) {

  }
  ngOnInit(): void {
    this.getAllpatient();
  }

  loadAllStates() {
    this.commonService.getAllState().subscribe({
      next: (res: any) => {
        this.stateList = res
      },
      error: (err: any) => {
        console.error("Error occurred in loadAllStates");
      }
    })
  }
  loadAllCity() {
    this.commonService.getAllCity().subscribe({
      next: (res: any) => {
        this.cityList = res
      },
      error: (err: any) => {
        console.error("Error occurred in loadAllCity");
      }
    })
  }

  getAllpatient() {
    this.patientService.getAllPatient().subscribe({
      next: (response) => {
        this.patientList = response;
        this.loadAllStates();
        this.loadAllCity();
      },
      error: (err) => {
        console.log(err);
      }
    })
  }

  deletePatientById(id: any) {
    Swal.fire({
      title: 'Are you sure?',
      text: 'This patient will be deleted permanently!',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Yes, delete it!'
    }).then((result) => {

      if (result.isConfirmed) {
        this.patientService.deletePatientById(id).subscribe({
          next: () => {
            Swal.fire(
              'Deleted!',
              'Patient has been deleted successfully.',
              'success'
            );

            // refresh list
            this.getAllpatient();
          },
          error: () => {
            Swal.fire(
              'Error!',
              'Something went wrong while deleting.',
              'error'
            );
          }
        });
      }

    });
  }

  isPatientBlocked(patient: any): boolean {
    return patient.isBlocked ?? patient.blocked;
  }

  togglePatientBlock(patient: any) {
    const isBlocked = this.isPatientBlocked(patient);
    const request$ = isBlocked
      ? this.patientService.unblockPatient(patient.patientId)
      : this.patientService.blockPatient(patient.patientId);

    request$.subscribe({
      next: () => {
        Swal.fire({
          icon: 'success',
          title: isBlocked ? 'Unblocked!' : 'Blocked!',
          text: isBlocked ? 'Patient can book appointments again.' : 'Patient cannot book appointments now.',
          timer: 1600,
          showConfirmButton: false
        });
        this.getAllpatient();
      },
      error: (err) => {
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: err?.error?.message || err?.error || 'Unable to update patient status.'
        });
      }
    });
  }


  getCityById(cityId: any): string {
    const city = this.cityList.find((t: any) => t.id === cityId);
    return city ? city.name : '';
  }
}
