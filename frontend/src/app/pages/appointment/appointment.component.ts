import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CalendarOptions, EventInput } from '@fullcalendar/core';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';
import timeGridPlugin from '@fullcalendar/timegrid';
import { CommonService } from 'src/app/service/common.service';
import { AuthService } from 'src/app/service/auth.service';
import { PatientService } from 'src/app/service/patient.service';
import { PermissionService } from 'src/app/service/permission.service';
import { StaffService } from 'src/app/service/staff.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-appointment',
  templateUrl: './appointment.component.html',
  styleUrls: ['./appointment.component.css']
})
export class AppointmentComponent implements OnInit {

  appointmentForm!: FormGroup;
  slots: any[] = [];
  patientList: any[] = [];
  doctorList: any[] = [];
  appointments: any[] = [];
  bookedSlotIds = new Set<number>();
  today = new Date().toISOString().slice(0, 10);
  consultationForm!: FormGroup;
  selectedAppointment: any;
  statusOptions = ['CONFIRM', 'PENDING', 'CANCELLED', 'BOOKED'];

  calendarOptions: CalendarOptions = {
    plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin],
    initialView: 'dayGridMonth',
    height: 'auto',
    selectable: true,
    nowIndicator: true,
    headerToolbar: {
      left: 'prev,next today',
      center: 'title',
      right: 'dayGridMonth,timeGridWeek,timeGridDay'
    },
    dateClick: (arg) => this.onCalendarDateClick(arg.dateStr),
    events: []
  };

  constructor(
    private fb: FormBuilder,
    private commonService: CommonService,
    private patientService: PatientService,
    public authService: AuthService,
    public permissions: PermissionService,
    private staffService: StaffService
  ) { }

  ngOnInit(): void {
    this.initForm();
    this.initConsultationForm();
    this.getAllPatient();
    this.getAllDoctor();
    this.getAllSlot();
    this.loadAppointments();

    this.appointmentForm.get('appointmentDate')?.valueChanges.subscribe((date: string) => {
      this.loadAppointmentsForDate(date);
    });
    this.appointmentForm.get('doctorId')?.valueChanges.subscribe(() => {
      this.loadAppointmentsForDate(this.appointmentForm.get('appointmentDate')?.value);
    });
  }

  initForm() {
    this.appointmentForm = this.fb.group({
      appointmentId: [null],
      appointmentDate: [this.today, Validators.required],
      appointmentStatus: ['BOOKED', Validators.required],
      patientId: ['', Validators.required],
      slotId: ['', Validators.required],
      doctorId: ['', Validators.required]
    });
  }

  initConsultationForm() {
    this.consultationForm = this.fb.group({
      appointmentStatus: ['CONFIRM', Validators.required],
      doctorNotes: [''],
      diagnosis: [''],
      prescription: ['']
    });
  }

  getAllSlot() {
    this.commonService.getAllSlot().subscribe({
      next: (res: any) => {
        this.slots = res || [];
        this.loadAppointmentsForDate(this.appointmentForm.get('appointmentDate')?.value);
      },
      error: (err: any) => console.log('error getting slot data', err)
    });
  }

  getAllPatient() {
    if (this.permissions.canBookForAnyPatient()) {
      this.patientService.getAllPatient().subscribe({
        next: (response: any) => {
          this.patientList = response || [];
        },
        error: (err) => console.log(err)
      });
      return;
    }

    if (!this.permissions.canBookOwnAppointment()) {
      return;
    }

    const userId = this.authService.getLoginUserId();
    if (!userId) {
      return;
    }

    this.patientService.getPatientByMobileNo(userId).subscribe({
      next: (patient: any) => {
        this.patientList = patient ? [patient] : [];
        if (patient && !this.isPatientBlocked(patient)) {
          this.appointmentForm.patchValue({ patientId: patient.patientId });
        }
      },
      error: (err) => console.log(err)
    });
  }

  getAllDoctor() {
    const role = this.authService.getRole();
    if (role === 'DOCTOR') {
      const doctorUserId = this.authService.getLoginUserId();
      if (!doctorUserId) {
        return;
      }
      this.staffService.getDoctorByUserId(doctorUserId).subscribe({
        next: (doctor: any) => {
          this.doctorList = doctor ? [doctor] : [];
          if (doctor?.doctorId) {
            this.appointmentForm.patchValue({ doctorId: doctor.doctorId });
          }
        },
        error: (err) => console.log('error loading logged in doctor', err)
      });
      return;
    }

    if (role === 'NURSE') {
      return;
    }

    this.staffService.getDoctors().subscribe({
      next: (response: any) => {
        this.doctorList = response || [];
      },
      error: (err) => console.log('error loading doctors', err)
    });
  }

  loadAppointments() {
    let request$;
    const role = this.authService.getRole();
    const userId = this.authService.getLoginUserId() || '';

    if (role === 'ADMIN' || role === 'NURSE' || role === 'RECEPTIONIST') {
      request$ = this.commonService.getAllAppointment();
    } else if (role === 'DOCTOR') {
      request$ = this.commonService.getAppointmentsByDoctor(userId);
    } else {
      request$ = this.commonService.getAppointmentsByPatient(userId);
    }

    request$.subscribe({
      next: (response: any) => {
        this.appointments = response || [];
        this.calendarOptions = {
          ...this.calendarOptions,
          events: this.mapAppointmentsToEvents(this.appointments)
        };
      },
      error: (err) => console.log('error getting appointment data', err)
    });
  }

  loadAppointmentsForDate(date: string) {
    const doctorId = this.appointmentForm.get('doctorId')?.value;
    if (!date || !doctorId) {
      this.bookedSlotIds.clear();
      return;
    }

    this.commonService.getBookedSlotIdsByDateAndDoctor(date, doctorId).subscribe({
      next: (response: any) => {
        this.bookedSlotIds = new Set((response || []).map((slotId: any) => Number(slotId)));

        const selectedSlotId = Number(this.appointmentForm.get('slotId')?.value);
        if (selectedSlotId && this.bookedSlotIds.has(selectedSlotId)) {
          this.appointmentForm.patchValue({ slotId: '' });
        }
      },
      error: (err) => console.log('error getting appointments by date', err)
    });
  }

  onCalendarDateClick(date: string) {
    this.appointmentForm.patchValue({ appointmentDate: date });
  }

  bookAppointment() {
    if (this.appointmentForm.invalid) {
      this.appointmentForm.markAllAsTouched();
      return;
    }

    const selectedSlotId = Number(this.appointmentForm.get('slotId')?.value);
    const selectedSlot = this.slots.find((slot: any) => Number(slot.slotId) === selectedSlotId);
    if (selectedSlot && this.isSlotBooked(selectedSlot)) {
      Swal.fire({
        title: 'Slot unavailable',
        text: 'This slot is already booked for the selected date.',
        icon: 'warning',
        confirmButtonText: 'OK'
      });
      return;
    }

    this.commonService.saveAppointment(this.appointmentForm.value).subscribe({
      next: () => {
        Swal.fire({
          icon: 'success',
          title: 'Booked!',
          text: 'Appointment booked successfully.',
          timer: 1600,
          showConfirmButton: false
        });
        this.appointmentForm.patchValue({ slotId: '' });
        this.loadAppointments();
        this.loadAppointmentsForDate(this.appointmentForm.get('appointmentDate')?.value);
      },
      error: (err: any) => {
        Swal.fire({
          title: 'Error!',
          text: err?.error?.message || err?.error || 'Please try again.',
          icon: 'error',
          confirmButtonText: 'OK'
        });
      }
    });
  }

  selectAppointment(appointment: any) {
    this.selectedAppointment = appointment;
    this.consultationForm.patchValue({
      appointmentStatus: appointment.appointmentStatus || 'CONFIRM',
      doctorNotes: appointment.doctorNotes || '',
      diagnosis: appointment.diagnosis || '',
      prescription: appointment.prescription || ''
    });
  }

  updateConsultation() {
    if (!this.selectedAppointment || this.consultationForm.invalid) {
      return;
    }

    this.commonService.updateConsultation(this.selectedAppointment.appointmentId, this.consultationForm.value).subscribe({
      next: () => {
        Swal.fire({
          icon: 'success',
          title: 'Updated!',
          text: 'Appointment updated successfully.',
          timer: 1600,
          showConfirmButton: false
        });
        this.selectedAppointment = null;
        this.loadAppointments();
        this.loadAppointmentsForDate(this.appointmentForm.get('appointmentDate')?.value);
      },
      error: (err: any) => {
        Swal.fire({
          title: 'Error!',
          text: err?.error?.message || err?.error || 'Please try again.',
          icon: 'error',
          confirmButtonText: 'OK'
        });
      }
    });
  }

  canUpdateAppointment(appointment: any): boolean {
    const role = this.authService.getRole();
    if (role === 'ADMIN') {
      return true;
    }
    return role === 'DOCTOR' && appointment.doctor?.userId === this.authService.getLoginUserId();
  }

  canShowBookingForm(): boolean {
    return this.permissions.canBookForAnyPatient() || this.permissions.canBookOwnAppointment();
  }

  isSlotBooked(slot: any): boolean {
    return this.bookedSlotIds.has(Number(slot.slotId)) || (slot.slotStatus && slot.slotStatus !== 'AVAILABLE');
  }

  isPatientBlocked(patient: any): boolean {
    return patient.isBlocked ?? patient.blocked;
  }

  isPatientRole(): boolean {
    return this.authService.getRole() === 'PATIENT';
  }

  private mapAppointmentsToEvents(appointments: any[]): EventInput[] {
    return appointments.map((appointment: any) => {
      const startTime = appointment.slot?.startTime;
      const endTime = appointment.slot?.endTime;
      const patientName = appointment.patient?.patientName || 'Appointment';
      const doctorName = appointment.doctor?.name ? ` with Dr. ${appointment.doctor.name}` : '';
      const timeText = startTime && endTime ? ` (${startTime} - ${endTime})` : '';

      return {
        id: String(appointment.appointmentId),
        title: `${patientName}${doctorName}${timeText}`,
        start: startTime ? `${appointment.appointmentDate}T${startTime}` : appointment.appointmentDate,
        end: endTime ? `${appointment.appointmentDate}T${endTime}` : undefined,
        color: this.getEventColor(appointment.appointmentStatus)
      };
    });
  }

  private getEventColor(status: string): string {
    if (status === 'CANCELLED') {
      return '#6c757d';
    }
    if (status === 'PENDING') {
      return '#ffc107';
    }
    return '#198754';
  }
}
