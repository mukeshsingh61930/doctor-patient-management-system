import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonService } from 'src/app/service/common.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-add-slot',
  templateUrl: './add-slot.component.html',
  styleUrls: ['./add-slot.component.css']
})
export class AddSlotComponent {
  slotForm!: FormGroup;
  isUpdateMode = false;
  slotList: any;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private commonService: CommonService
  ) { }

  ngOnInit(): void {
    this.createForm();
    this.checkEditMode();
  }


  createForm() {
    this.slotForm = this.fb.group({
      slotId: [null],
      // slotDate: ['', Validators.required],
      startTime: ['', Validators.required],
      endTime: ['', Validators.required],
      slotStatus: ['AVAILABLE', Validators.required],
      // status: ['', Validators.required]
    });
  }

  checkEditMode() {
    this.route.params.subscribe(params => {
      const id = params['id'];

      if (id) {
        this.isUpdateMode = true;
        this.getSlotById(id);
      }
    });
  }
  getSlotById(id: any) {
    this.commonService.getSlotById(id).subscribe({
      next: (res: any) => {
        this.slotForm.patchValue({
          slotId: res.slotId,
          startTime: res.startTime,
          endTime: res.endTime,
          slotStatus: res.slotStatus
        });
      },
      error: (err: any) => {
        console.log('error getting the data', err);
      },
    });
  }

  submit() {
    if (this.slotForm.invalid) {
      this.slotForm.markAllAsTouched();
      return;
    }

    const payload = this.slotForm.value;

    if (this.isUpdateMode) {
      this.commonService.saveSlot(payload).subscribe({
        next: (res: any) => {

          Swal.fire({
            icon: 'success',
            title: 'Update!',
            text: 'Slot update successfully.',
            timer: 2000,
            showConfirmButton: false
          });
          this.router.navigate(['/patients/slots']);
        },
        error: (err: any) => {
          Swal.fire({
            icon: 'error',
            title: 'Error',
            text: this.commonService.getErrorMessage(err, 'Failed to update slot. Please try again.')
          });

          console.error("Error occurred in updateSlot", err);
        }
      });
    } else {
      this.commonService.saveSlot(payload).subscribe({
        next: (res: any) => {

          Swal.fire({
            icon: 'success',
            title: 'Saved!',
            text: 'Slot saved successfully.',
            timer: 2000,
            showConfirmButton: false
          });
          this.router.navigate(['/patients/slots']);
        },
        error: (err: any) => {
          Swal.fire({
            icon: 'error',
            title: 'Error',
            text: this.commonService.getErrorMessage(err, 'Failed to save slot. Please try again.')
          });

          console.error("Error occurred in saveSlot", err);
        }
      });

    }
  }

}
