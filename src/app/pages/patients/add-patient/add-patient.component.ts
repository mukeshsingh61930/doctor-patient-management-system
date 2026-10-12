import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { State } from 'src/app/models/State';
import { CommonService } from 'src/app/service/common.service';
import { PatientService } from 'src/app/service/patient.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-add-patient',
  templateUrl: './add-patient.component.html',
  styleUrls: ['./add-patient.component.css']
})
export class AddPatientComponent {

  patientForm: FormGroup | any;
  uploadSuccess: boolean = false;
  uploadError: string | null = null;
  stateList: State[] = [];
  cityList: string[] = [];
  photoFile: File | null = null;
  photoPreview: string | ArrayBuffer | null = null;
  defaultPhoto: string = 'assets/images/noUser.jpg';
  attachment = this.createAttachmentObject();
  attachmentType: string = "patient_logo";
  buttonText: any;
  patient: any;
  isPublicRegistration = false;

  constructor(private fb: FormBuilder,
    private commonService: CommonService,
    private patientService: PatientService,
    private activatedRoute: ActivatedRoute,
    private router: Router
  ) {

  }
  ngOnInit(): void {
    this.isPublicRegistration = this.activatedRoute.snapshot.data['publicRegistration'] === true;
    this.initForm();
    this.loadAllStates();
    this.getActivatedRouteDetails();
  }

  initForm() {
    this.patientForm = this.fb.group({
      patientId: [null],
      patientName: ['', Validators.required],
      mobileNo: [null, [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
      status: [this.isPublicRegistration ? 'ACTIVE' : '', Validators.required],
      illnessHistory: [''],
      gender: ['', Validators.required],
      state: [null],
      city: [null],
      isBlocked: [false],
      attachment: this.fb.group({
        attachmentId: [null],
        attachmentName: [''],
        attachmentType: ['patient_logo'],
        data: [null]
      })
    })
  }

  loadAllStates() {
    this.commonService.getAllState().subscribe({
      next: (res: any) => {
        this.stateList = res as State[];
        const selectedState = this.patientForm?.get('state')?.value;
        if (selectedState) {
          this.onSelectStateLoadCities(selectedState);
        }
      },
      error: (err: any) => {
        console.error("Error occurred in loadAllStates");
      }
    })
  }

  onSelectStateLoadCities(stateName: any) {
    const selectedState = this.stateList.find(state => state.name === stateName);
    if (selectedState) {
      this.commonService.getCitiesByStateId(selectedState.id).subscribe({
        next: (res: any) => {
          this.cityList = res.map((city: any) => city.name);
        },
        error: (err: any) => {
          console.error("Error occurred in onSelectStateLoadCities", err);
        }
      });
    }
  }

  createAttachmentObject() {
    return {
      attachmentId: null,
      attachmentName: '',
      attachmentType: '',
      imageUrl: '',
      data: ''
    };
  }
  onPhotoSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.photoFile = file;
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.photoPreview = e.target?.result as string | ArrayBuffer;
        this.patientForm.get('attachment')?.patchValue({
          attachmentName: file.name,
          data: this.photoPreview
        });
      };
      reader.readAsDataURL(file);
    }
  }

  getActivatedRouteDetails() {
    if (this.isPublicRegistration) {
      this.buttonText = "Register";
      return;
    }

    this.activatedRoute.params.subscribe((params: { [x: string]: any; }) => {
      let id = params["id"];
      if (id != null) {
        this.buttonText = "Update";
        this.getPatientById(id);
      } else {
        this.buttonText = "Save";
      }
    });
  }

  getPatientById(id: any) {
    this.patientService.getPatientById(id).subscribe({
      next: (res: any) => {
        this.patient = res;
        if (this.patient) {
          this.photoPreview = this.patient.attachment?.data || null;
          this.patientForm.patchValue({
            patientId: this.patient.patientId,
            patientName: this.patient.patientName,
            mobileNo: this.patient.mobileNo,
            status: this.patient.status,
            illnessHistory: this.patient.illnessHistory,
            gender: this.patient.gender,
            state: this.patient.state,
            city: this.patient.city,
            isBlocked: this.patient.isBlocked ?? this.patient.blocked,

            attachment: {
              attachmentId: this.patient.attachment?.attachmentId || null,
              attachmentName: this.patient.attachment?.attachmentName || '',
              attachmentType: this.patient.attachment?.attachmentType || 'patient_logo',
              data: this.patient.attachment?.data || null
            }
          });
        } else {
          console.log(`No data found for id: ${id}`);
        }
      },
      error: (err: any) => {
        console.log('error getting the data', err);
      },
    });
  }


  submit() {
    if (this.isPublicRegistration) {
      this.patientForm.patchValue({ status: 'ACTIVE', isBlocked: false });
    }

    if (this.patientForm.invalid) {
      this.patientForm.markAllAsTouched();
      return;
    }

    this.savePatient(this.patientForm.value);

  }

  saveAttachment(attachment: any) {
    this.commonService.saveAttachment(attachment).subscribe({
      next: (res: any) => {
        this.patientForm.patchValue({ attachmentId: res.attachmentId });
        this.savePatient(this.patientForm.value);
      },
      error: (err: any) => {
        console.error("Error occurred in saveAttachment");
      }
    })
  }

  savePatient(patient: any) {
    this.patientService.savaPatient(patient).subscribe({
      next: (res: any) => {

        Swal.fire({
          icon: 'success',
          title: this.isPublicRegistration ? 'Registered!' : 'Saved!',
          text: this.isPublicRegistration ? 'Your account has been created. Please login with your mobile number and default password 123.' : 'Patient saved successfully.',
          timer: 2000,
          showConfirmButton: false
        });
        this.router.navigate([this.isPublicRegistration ? '/login' : '/patients']);
      },
      error: (err: any) => {
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: err?.error?.message || err?.error || 'Failed to save patient. Please try again.'
        });

        console.error("Error occurred in savePatient", err);
      }
    });
  }




}
