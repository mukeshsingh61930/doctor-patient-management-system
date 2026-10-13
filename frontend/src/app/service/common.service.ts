import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "src/environments/environment";

@Injectable({
  providedIn: 'root'
})
export class CommonService {

  constructor(private _http: HttpClient) { }

  private baseUrl = `${environment.apiBaseUrl}/`;

  getErrorMessage(error: any, fallback = 'Something went wrong. Please try again.'): string {
    if (!error) {
      return fallback;
    }

    if (error.status === 0) {
      return 'Unable to connect to server. Please check backend service.';
    }

    const body = error.error;

    if (typeof body === 'string' && body.trim()) {
      return body;
    }

    if (body?.message) {
      return body.message;
    }

    if (body?.error && typeof body.error === 'string') {
      return body.error;
    }

    if (Array.isArray(body?.errors) && body.errors.length > 0) {
      return body.errors
        .map((item: any) => item?.defaultMessage || item?.message || item)
        .filter(Boolean)
        .join(', ');
    }

    if (error.message && !error.message.includes('Http failure response')) {
      return error.message;
    }

    return fallback;
  }

  getAllState() {
    return this._http.get(`${this.baseUrl}` + "states/allState");
  }

  getAllCity() {
    return this._http.get(`${this.baseUrl}` + "cities/allCity");
  }

  getCitiesByStateId(stateId: any) {
    return this._http.get(`${this.baseUrl}` + "cities/stateId/" + stateId);
  }

  getCitiesByStateName(stateName: any) {
    return this._http.get(`${this.baseUrl}` + "cities/stateName/" + stateName);
  }

  saveAttachment(attachment: any) {
    return this._http.post(`${this.baseUrl}` + "attachment/saveAttachment", attachment);
  }

  getStateById(stateId: any) {
    return this._http.get(`${this.baseUrl}` + "states/" + stateId);
  }

  getCityById(cityId: any) {
    return this._http.get(`${this.baseUrl}` + "cities/" + cityId);
  }

  saveSlot(slot: any) {
    return this._http.post(`${this.baseUrl}` + "slot/save", slot);
  }

  getAllSlot() {
    return this._http.get(`${this.baseUrl}` + "slot/allSlot");
  }

  deleteSlotById(id: any) {
    return this._http.delete(`${this.baseUrl}slot/${id}`);
  }

  getSlotById(id: any) {
    return this._http.get(`${this.baseUrl}slot/${id}`);
  }

  saveAppointment(appointment: any) {
    return this._http.post(`${this.baseUrl}` + "appointment/save", appointment);
  }

  getAllAppointment() {
    return this._http.get(`${this.baseUrl}` + "appointment/allAppointment");
  }

  getAppointmentsByDate(date: string) {
    return this._http.get(`${this.baseUrl}` + "appointment/date/" + date);
  }

  getBookedSlotIdsByDate(date: string) {
    return this._http.get(`${this.baseUrl}` + "appointment/booked-slots/date/" + date);
  }

  getBookedSlotIdsByDateAndDoctor(date: string, doctorId: number | string) {
    return this._http.get(`${this.baseUrl}` + "appointment/booked-slots/date/" + date + "/doctor-profile/" + doctorId);
  }

  getAppointmentsByPatient(mobileNo: string) {
    return this._http.get(`${this.baseUrl}` + "appointment/patient/" + mobileNo);
  }

  getAppointmentsByDoctor(doctorUserId: string) {
    return this._http.get(`${this.baseUrl}` + "appointment/doctor/" + doctorUserId);
  }

  updateConsultation(appointmentId: number, appointment: any) {
    return this._http.post(`${this.baseUrl}` + "appointment/" + appointmentId + "/consultation", appointment);
  }

  getDashboard() {
    return this._http.get(`${this.baseUrl}` + "appointment/dashboard");
  }

  getPatientDashboard(mobileNo: string) {
    return this._http.get(`${this.baseUrl}` + "appointment/dashboard/patient/" + mobileNo);
  }

  getDoctorDashboard(doctorUserId: string) {
    return this._http.get(`${this.baseUrl}` + "appointment/dashboard/doctor/" + doctorUserId);
  }
}
