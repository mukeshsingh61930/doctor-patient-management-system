package com.doctor_patient.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.doctor_patient.Exception.DuplicateResourceException;
import com.doctor_patient.entity.Patient;
import com.doctor_patient.entity.User;
import com.doctor_patient.enums.Role;
import com.doctor_patient.enums.Status;
import com.doctor_patient.repo.PatientRepo;
import com.doctor_patient.repo.UserRepo;

@Service
public class PatientService {

	private final PatientRepo patientRepo;
	private final UserRepo userRepo;
	private final PasswordEncoder passwordEncoder;

	public PatientService(PatientRepo patientRepo, UserRepo userRepo, PasswordEncoder passwordEncoder) {
		this.patientRepo = patientRepo;
		this.userRepo = userRepo;
		this.passwordEncoder = passwordEncoder;
	}

	public Patient savePatient(Patient patient) {
		validateUniqueMobile(patient);
		if (patient.getStatus() == null) {
			patient.setStatus(Status.ACTIVE);
		}
		String previousMobileNo = patient.getPatientId() == null
				? null
				: patientRepo.findById(patient.getPatientId()).map(Patient::getMobileNo).orElse(null);
		patient.setUser(buildPatientLogin(patient, previousMobileNo));
		Patient savedPatient = patientRepo.save(patient);
		return savedPatient;
	}

	public List<Patient> getAllPatientProfiles() {
		return patientRepo.findAll().stream()
				.filter(this::hasPatientLoginRole)
				.toList();
	}

	public Patient blockPatient(Long id, boolean blocked) {
		Patient patient = patientRepo.findById(id).orElseThrow();
		patient.setBlocked(blocked);
		patient.setStatus(blocked ? Status.INACTIVE : Status.ACTIVE);
		patient.setUser(buildPatientLogin(patient, patient.getMobileNo()));
		Patient savedPatient = patientRepo.save(patient);
		return savedPatient;
	}

	public Patient getPatientProfileById(Long id) {
		Patient patient = patientRepo.findById(id).orElseThrow();
		if (!hasPatientLoginRole(patient)) {
			throw new java.util.NoSuchElementException();
		}
		return patient;
	}

	private void validateUniqueMobile(Patient patient) {
		patientRepo.findByMobileNo(patient.getMobileNo())
				.filter(existing -> !existing.getPatientId().equals(patient.getPatientId()))
				.ifPresent(existing -> {
					throw new DuplicateResourceException("Mobile number already exists: " + patient.getMobileNo());
				});
		userRepo.findByUserId(patient.getMobileNo())
				.filter(existing -> existing.getRole() != Role.PATIENT)
				.ifPresent(existing -> {
					throw new DuplicateResourceException("This mobile number belongs to a staff user");
				});
	}

	private User buildPatientLogin(Patient patient, String previousMobileNo) {
		User user = previousMobileNo != null
				? userRepo.findByUserId(previousMobileNo).orElseGet(() -> userRepo.findByUserId(patient.getMobileNo()).orElseGet(User::new))
				: userRepo.findByUserId(patient.getMobileNo()).orElseGet(User::new);
		user.setName(patient.getPatientName());
		user.setUserId(patient.getMobileNo());
		user.setPassword(user.getPassword() == null ? passwordEncoder.encode("123") : user.getPassword());
		user.setRole(Role.PATIENT);
		user.setStatus(patient.isBlocked() ? Status.INACTIVE : patient.getStatus());
		user.setMobNo(patient.getMobileNo());
		return userRepo.save(user);
	}

	private boolean hasPatientLoginRole(Patient patient) {
		if (patient.getUser() != null) {
			return patient.getUser().getRole() == Role.PATIENT;
		}
		return userRepo.findByUserId(patient.getMobileNo())
				.map(user -> user.getRole() == Role.PATIENT)
				.orElse(false);
	}
}
