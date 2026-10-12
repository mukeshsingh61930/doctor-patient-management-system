import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PatientService {

  constructor(private _http: HttpClient) { }

  private baseUrl = `${environment.apiBaseUrl}/patient`;


  savaPatient(patient: any) {
    return this._http.post(`${this.baseUrl}/register`, patient);
  }

  getAllPatient() {
    return this._http.get(`${this.baseUrl}/allPatient`);
  }

  deletePatientById(id: any) {
    return this._http.delete(`${this.baseUrl}/${id}`);
  }
  getPatientById(id: any) {
    return this._http.get(`${this.baseUrl}/${id}`);
  }

  getPatientByMobileNo(mobileNo: string) {
    return this._http.get(`${this.baseUrl}/by-mobile/${mobileNo}`);
  }

  blockPatient(id: any) {
    return this._http.patch(`${this.baseUrl}/${id}/block`, {});
  }

  unblockPatient(id: any) {
    return this._http.patch(`${this.baseUrl}/${id}/unblock`, {});
  }

}
