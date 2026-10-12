package com.doctor_patient.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.doctor_patient.entity.NurseProfile;
import com.doctor_patient.repo.NurseProfileRepo;

@RestController
@RequestMapping("/api/v1/nurses")
public class NurseProfileController {

	private final NurseProfileRepo nurseProfileRepo;

	public NurseProfileController(NurseProfileRepo nurseProfileRepo) {
		this.nurseProfileRepo = nurseProfileRepo;
	}

	@GetMapping("/profiles")
	public ResponseEntity<List<NurseProfile>> getNurseProfiles() {
		return new ResponseEntity<>(nurseProfileRepo.findAll(), HttpStatus.OK);
	}

	@GetMapping("/profiles/{id}")
	public ResponseEntity<NurseProfile> getNurseProfile(@PathVariable Long id) {
		return new ResponseEntity<>(nurseProfileRepo.findById(id).orElseThrow(), HttpStatus.OK);
	}
}
