package com.doctor_patient.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.doctor_patient.DTO.AppointmentDTO;
import com.doctor_patient.entity.Appointment;
import com.doctor_patient.enums.Role;
import com.doctor_patient.repo.AppointmentRepo;
import com.doctor_patient.repo.PatientRepo;
import com.doctor_patient.repo.SlotRepo;
import com.doctor_patient.security.AuthenticatedUser;
import com.doctor_patient.security.SecurityUtils;
import com.doctor_patient.service.AppointmentService;
import com.doctor_patient.enums.AppointmentStatus;

@RestController
@RequestMapping("/api/v1/appointment")
public class AppointmentController {

	@Autowired
	AppointmentRepo appointmentRepo;
	
	@Autowired
	PatientRepo patientRepo;
	
	@Autowired
	SlotRepo slotRepo;

	@Autowired
	AppointmentService appointmentService;
	
	@PostMapping("/save")
	public ResponseEntity<Appointment> save(
			@RequestBody AppointmentDTO appointmentDTO,
			Authentication authentication){
		AuthenticatedUser actor = SecurityUtils.currentUser(authentication);
		Appointment appointmentSaved=appointmentService.save(appointmentDTO, actor.getRole(), actor.getUserId());
		return new ResponseEntity<>(appointmentSaved,HttpStatus.OK);
	}
	
	@GetMapping("/allAppointment")
	public ResponseEntity<List<Appointment>> getAllAppointment(){
		List<Appointment> allAppointmentList=appointmentRepo.findAll();
		return new ResponseEntity<>(allAppointmentList,HttpStatus.OK);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteAppointment(@PathVariable Long  id){
		appointmentRepo.deleteById(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Appointment> getAppointmentById(@PathVariable Long  id){
		Appointment appointment=appointmentRepo.findById(id).orElseThrow();
		return new ResponseEntity<>(appointment,HttpStatus.OK);
	}

	@GetMapping("/date/{date}")
	public ResponseEntity<List<Appointment>> getAppointmentByDate(@PathVariable LocalDate date){
		List<Appointment> appointmentList=appointmentRepo.findByAppointmentDateOrderBySlot_StartTimeAsc(date);
		return new ResponseEntity<>(appointmentList,HttpStatus.OK);
	}

	@GetMapping("/booked-slots/date/{date}")
	public ResponseEntity<List<Long>> getBookedSlotIdsByDate(@PathVariable LocalDate date){
		List<Long> bookedSlotIds=appointmentRepo.findBookedSlotIdsByDate(date, AppointmentStatus.CANCELLED);
		return new ResponseEntity<>(bookedSlotIds,HttpStatus.OK);
	}

	@GetMapping("/booked-slots/date/{date}/doctor/{doctorUserId}")
	public ResponseEntity<List<Long>> getBookedSlotIdsByDateAndDoctor(@PathVariable LocalDate date, @PathVariable String doctorUserId){
		List<Long> bookedSlotIds=appointmentRepo.findBookedSlotIdsByDateAndDoctor(date, doctorUserId, AppointmentStatus.CANCELLED);
		return new ResponseEntity<>(bookedSlotIds,HttpStatus.OK);
	}

	@GetMapping("/booked-slots/date/{date}/doctor-profile/{doctorId}")
	public ResponseEntity<List<Long>> getBookedSlotIdsByDateAndDoctorId(@PathVariable LocalDate date, @PathVariable Long doctorId){
		List<Long> bookedSlotIds=appointmentRepo.findBookedSlotIdsByDateAndDoctorId(date, doctorId, AppointmentStatus.CANCELLED);
		return new ResponseEntity<>(bookedSlotIds,HttpStatus.OK);
	}

	@GetMapping("/patient/{mobileNo}")
	public ResponseEntity<List<Appointment>> getAppointmentByPatient(@PathVariable String mobileNo, Authentication authentication){
		SecurityUtils.requireOwnerOrRole(authentication, mobileNo, Role.ADMIN, Role.DOCTOR, Role.NURSE, Role.RECEPTIONIST);
		List<Appointment> appointmentList=appointmentRepo.findByPatient_MobileNoOrderByAppointmentDateAscSlot_StartTimeAsc(mobileNo);
		return new ResponseEntity<>(appointmentList,HttpStatus.OK);
	}

	@GetMapping("/doctor/{doctorUserId}")
	public ResponseEntity<List<Appointment>> getAppointmentByDoctor(@PathVariable String doctorUserId, Authentication authentication){
		SecurityUtils.requireOwnerOrRole(authentication, doctorUserId, Role.ADMIN);
		List<Appointment> appointmentList=appointmentRepo.findByDoctor_UserIdOrderByAppointmentDateAscSlot_StartTimeAsc(doctorUserId);
		return new ResponseEntity<>(appointmentList,HttpStatus.OK);
	}

	@PostMapping("/{id}/consultation")
	public ResponseEntity<Appointment> updateConsultation(
			@PathVariable Long id,
			@RequestBody AppointmentDTO appointmentDTO,
			Authentication authentication){
		AuthenticatedUser actor = SecurityUtils.currentUser(authentication);
		Appointment appointment=appointmentService.updateConsultation(id, appointmentDTO, actor.getRole(), actor.getUserId());
		return new ResponseEntity<>(appointment,HttpStatus.OK);
	}

	@GetMapping("/dashboard")
	public ResponseEntity<Map<String, Object>> getDashboard(){
		Map<String, Object> dashboard = new HashMap<>();
		LocalDate today = LocalDate.now();
		dashboard.put("totalPatients", patientRepo.count());
		dashboard.put("todayAppointments", appointmentRepo.countByAppointmentDate(today));
		dashboard.put("pendingAppointments", appointmentRepo.countByAppointmentStatus(AppointmentStatus.PENDING));
		dashboard.put("bookedAppointments", appointmentRepo.countByAppointmentStatus(AppointmentStatus.BOOKED));
		dashboard.put("upcomingAppointments", appointmentRepo.findTop5ByAppointmentDateGreaterThanEqualOrderByAppointmentDateAscSlot_StartTimeAsc(today));
		return new ResponseEntity<>(dashboard,HttpStatus.OK);
	}

	@GetMapping("/dashboard/patient/{mobileNo}")
	public ResponseEntity<Map<String, Object>> getPatientDashboard(@PathVariable String mobileNo, Authentication authentication){
		SecurityUtils.requireOwnerOrRole(authentication, mobileNo, Role.ADMIN, Role.DOCTOR, Role.NURSE, Role.RECEPTIONIST);
		Map<String, Object> dashboard = new HashMap<>();
		LocalDate today = LocalDate.now();
		dashboard.put("totalPatients", 1);
		dashboard.put("todayAppointments", appointmentRepo.countByPatient_MobileNoAndAppointmentDate(mobileNo, today));
		dashboard.put("pendingAppointments", appointmentRepo.countByPatient_MobileNoAndAppointmentStatus(mobileNo, AppointmentStatus.PENDING));
		dashboard.put("bookedAppointments", appointmentRepo.countByPatient_MobileNoAndAppointmentStatus(mobileNo, AppointmentStatus.BOOKED));
		dashboard.put("upcomingAppointments", appointmentRepo.findTop5ByPatient_MobileNoAndAppointmentDateGreaterThanEqualOrderByAppointmentDateAscSlot_StartTimeAsc(mobileNo, today));
		return new ResponseEntity<>(dashboard,HttpStatus.OK);
	}

	@GetMapping("/dashboard/doctor/{doctorUserId}")
	public ResponseEntity<Map<String, Object>> getDoctorDashboard(@PathVariable String doctorUserId, Authentication authentication){
		SecurityUtils.requireOwnerOrRole(authentication, doctorUserId, Role.ADMIN);
		Map<String, Object> dashboard = new HashMap<>();
		LocalDate today = LocalDate.now();
		dashboard.put("totalPatients", appointmentRepo.countDistinctPatientsByDoctorUserId(doctorUserId, AppointmentStatus.CANCELLED));
		dashboard.put("todayAppointments", appointmentRepo.countByDoctor_UserIdAndAppointmentDate(doctorUserId, today));
		dashboard.put("pendingAppointments", appointmentRepo.countByDoctor_UserIdAndAppointmentStatus(doctorUserId, AppointmentStatus.PENDING));
		dashboard.put("bookedAppointments", appointmentRepo.countByDoctor_UserIdAndAppointmentStatus(doctorUserId, AppointmentStatus.BOOKED));
		dashboard.put("upcomingAppointments", appointmentRepo.findTop5ByDoctor_UserIdAndAppointmentDateGreaterThanEqualOrderByAppointmentDateAscSlot_StartTimeAsc(doctorUserId, today));
		return new ResponseEntity<>(dashboard,HttpStatus.OK);
	}
}
