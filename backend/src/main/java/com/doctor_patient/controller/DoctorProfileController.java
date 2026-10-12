package com.doctor_patient.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.doctor_patient.DTO.DoctorOptionDTO;
import com.doctor_patient.entity.DoctorProfile;
import com.doctor_patient.enums.Role;
import com.doctor_patient.enums.Status;
import com.doctor_patient.repo.DoctorProfileRepo;
import com.doctor_patient.security.SecurityUtils;

@RestController
@RequestMapping("/api/v1/doctors")
public class DoctorProfileController {

	private final DoctorProfileRepo doctorProfileRepo;

	public DoctorProfileController(DoctorProfileRepo doctorProfileRepo) {
		this.doctorProfileRepo = doctorProfileRepo;
	}

	@GetMapping("/profiles")
	public ResponseEntity<List<DoctorProfile>> getDoctorProfiles() {
		return new ResponseEntity<>(doctorProfileRepo.findAll(), HttpStatus.OK);
	}

	@GetMapping("/profiles/{id}")
	public ResponseEntity<DoctorProfile> getDoctorProfile(@PathVariable Long id) {
		return new ResponseEntity<>(doctorProfileRepo.findById(id).orElseThrow(), HttpStatus.OK);
	}

	@GetMapping("/options")
	public ResponseEntity<List<DoctorOptionDTO>> getDoctorOptions() {
		List<DoctorOptionDTO> doctors = doctorProfileRepo
				.findByStaffProfile_User_StatusOrderByStaffProfile_User_NameAsc(Status.ACTIVE)
				.stream()
				.map(this::toOption)
				.toList();
		return new ResponseEntity<>(doctors, HttpStatus.OK);
	}

	@GetMapping("/by-user/{userId}")
	public ResponseEntity<DoctorOptionDTO> getDoctorByUserId(@PathVariable String userId, Authentication authentication) {
		SecurityUtils.requireOwnerOrRole(authentication, userId, Role.ADMIN);
		DoctorProfile doctorProfile = doctorProfileRepo.findByStaffProfile_User_UserId(userId).orElseThrow();
		return new ResponseEntity<>(toOption(doctorProfile), HttpStatus.OK);
	}

	private DoctorOptionDTO toOption(DoctorProfile doctorProfile) {
		return new DoctorOptionDTO(
				doctorProfile.getDoctorId(),
				doctorProfile.getStaffProfile().getUser().getName(),
				doctorProfile.getSpecialization(),
				doctorProfile.getRegistrationNo(),
				doctorProfile.getConsultationFee());
	}
}
