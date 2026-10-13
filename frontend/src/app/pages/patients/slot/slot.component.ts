import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonService } from 'src/app/service/common.service';
import { PermissionService } from 'src/app/service/permission.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-slot',
  templateUrl: './slot.component.html',
  styleUrls: ['./slot.component.css']
})
export class SlotComponent {
  slotList: any;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private commonService: CommonService,
    public permissions: PermissionService
  ) { }

  ngOnInit(): void {
    this.getAllSlot();
  }

  getAllSlot() {
    this.commonService.getAllSlot().subscribe({
      next: (response) => {
        this.slotList = response;
      },
      error: (err) => {
        console.log(err);
      }
    })

  }

  deleteSlotById(id: any) {
    Swal.fire({
      title: 'Are you sure?',
      text: 'This slot will be deleted permanently!',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Yes, delete it!'
    }).then((result) => {

      if (result.isConfirmed) {
        this.commonService.deleteSlotById(id).subscribe({
          next: () => {
            Swal.fire(
              'Deleted!',
              'Slot has been deleted successfully.',
              'success'
            );

            // refresh list
            this.getAllSlot();
          },
          error: (err) => {
            Swal.fire(
              'Error!',
              this.commonService.getErrorMessage(err, 'Something went wrong while deleting.'),
              'error'
            );
          }
        });
      }

    });
  }

}
