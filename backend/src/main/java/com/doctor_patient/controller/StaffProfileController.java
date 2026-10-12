package com.doctor_patient.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.doctor_patient.DTO.StaffRegistrationDTO;
import com.doctor_patient.entity.StaffProfile;
import com.doctor_patient.enums.Status;
import com.doctor_patient.service.StaffProfileService;

@RestController
@RequestMapping("/api/v1/staff")
public class StaffProfileController {

	private final StaffProfileService staffProfileService;

	public StaffProfileController(StaffProfileService staffProfileService) {
		this.staffProfileService = staffProfileService;
	}

	@PostMapping
	public ResponseEntity<StaffProfile> createStaff(@RequestBody StaffRegistrationDTO request) {
		StaffProfile staffProfile = staffProfileService.createStaff(request);
		return new ResponseEntity<>(staffProfile, HttpStatus.CREATED);
	}

	@GetMapping
	public ResponseEntity<List<StaffProfile>> getAllStaff() {
		List<StaffProfile> staffProfiles = staffProfileService.getAllStaff();
		return new ResponseEntity<>(staffProfiles, HttpStatus.OK);
	}

	@PutMapping("/{staffId}")
	public ResponseEntity<StaffProfile> updateStaff(@PathVariable Long staffId, @RequestBody StaffRegistrationDTO request) {
		StaffProfile staffProfile = staffProfileService.updateStaff(staffId, request);
		return new ResponseEntity<>(staffProfile, HttpStatus.OK);
	}

	@PatchMapping("/{staffId}/status/{status}")
	public ResponseEntity<StaffProfile> updateStatus(@PathVariable Long staffId, @PathVariable Status status) {
		StaffProfile staffProfile = staffProfileService.updateStatus(staffId, status);
		return new ResponseEntity<>(staffProfile, HttpStatus.OK);
	}
}
