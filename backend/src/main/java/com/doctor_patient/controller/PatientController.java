package com.doctor_patient.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.doctor_patient.entity.Patient;
import com.doctor_patient.enums.Role;
import com.doctor_patient.repo.PatientRepo;
import com.doctor_patient.security.SecurityUtils;
import com.doctor_patient.service.PatientService;

@RestController
@RequestMapping("/api/v1/patient")
public class PatientController {

	@Autowired
	PatientRepo patientRepo;
	
	@Autowired
	PatientService patientService;
	
	@PostMapping("/register")
	public ResponseEntity<Patient> savePatient(
			@RequestBody Patient patient,
			Authentication authentication){
		if (patient.getPatientId() != null && !SecurityUtils.hasRole(authentication, Role.ADMIN)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admin can update patient records");
		}
		Patient patientSaved=patientService.savePatient(patient);
		return new ResponseEntity<>(patientSaved,HttpStatus.OK);
	}
	
	@GetMapping("/allPatient")
	public ResponseEntity<List<Patient>> getAllRegisterPatient(){
		List<Patient> allPatientList=patientService.getAllPatientProfiles();
		return new ResponseEntity<>(allPatientList,HttpStatus.OK);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deletePatient(@PathVariable Long  id){
		patientRepo.deleteById(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Patient> getPatientById(@PathVariable Long  id){
		Patient patient=patientService.getPatientProfileById(id);
		return new ResponseEntity<>(patient,HttpStatus.OK);
	}

	@GetMapping("/by-mobile/{mobileNo}")
	public ResponseEntity<Patient> getPatientByMobileNo(@PathVariable String mobileNo, Authentication authentication){
		SecurityUtils.requireOwnerOrRole(authentication, mobileNo, Role.ADMIN, Role.DOCTOR, Role.NURSE, Role.RECEPTIONIST);
		Patient patient=patientRepo.findByMobileNo(mobileNo).orElseThrow();
		return new ResponseEntity<>(patient,HttpStatus.OK);
	}

	@PatchMapping("/{id}/block")
	public ResponseEntity<Patient> blockPatient(@PathVariable Long id){
		Patient patient=patientService.blockPatient(id, true);
		return new ResponseEntity<>(patient,HttpStatus.OK);
	}

	@PatchMapping("/{id}/unblock")
	public ResponseEntity<Patient> unblockPatient(@PathVariable Long id){
		Patient patient=patientService.blockPatient(id, false);
		return new ResponseEntity<>(patient,HttpStatus.OK);
	}
}
