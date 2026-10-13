package com.doctor_patient.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.doctor_patient.DTO.LoginDTO;
import com.doctor_patient.entity.Patient;
import com.doctor_patient.entity.User;
import com.doctor_patient.enums.Role;
import com.doctor_patient.enums.Status;
import com.doctor_patient.repo.PatientRepo;
import com.doctor_patient.repo.UserRepo;

@Service
public class LoginService {

	private final UserRepo userRepo;
	private final PatientRepo patientRepo;
	private final PasswordEncoder passwordEncoder;

	public LoginService(UserRepo userRepo, PatientRepo patientRepo, PasswordEncoder passwordEncoder) {
		this.userRepo = userRepo;
		this.patientRepo = patientRepo;
		this.passwordEncoder = passwordEncoder;
	}
	
	public User validateUser(LoginDTO loginDTO) {
		if (loginDTO == null || isBlank(loginDTO.getUserId())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User Id is required");
		}
		if (isBlank(loginDTO.getPassword())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required");
		}

		User user = userRepo.findByUserId(loginDTO.getUserId().trim())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User Id does not exist"));

		if (isBlockedPatient(user)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,
					"Your patient account is blocked. Please contact clinic administrator.");
		}

		if (user.getStatus() != null && user.getStatus() != Status.ACTIVE) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,
					"Your account is inactive. Please contact administrator.");
		}

		if (!passwordMatches(loginDTO.getPassword(), user)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect password");
		}

		return migratePlainPassword(loginDTO.getPassword(), user);
	}

	private boolean passwordMatches(String rawPassword, User user) {
		String storedPassword = user.getPassword();
		if (rawPassword == null || storedPassword == null) {
			return false;
		}
		if (isBcryptHash(storedPassword)) {
			return passwordEncoder.matches(rawPassword, storedPassword);
		}
		return storedPassword.equals(rawPassword);
	}

	private User migratePlainPassword(String rawPassword, User user) {
		if (!isBcryptHash(user.getPassword())) {
			user.setPassword(passwordEncoder.encode(rawPassword));
			return userRepo.save(user);
		}
		return user;
	}

	private boolean isBcryptHash(String password) {
		return password != null
				&& (password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$"));
	}

	private boolean isBlockedPatient(User user) {
		if (user.getRole() != Role.PATIENT) {
			return false;
		}

		String mobileNo = isBlank(user.getMobNo()) ? user.getUserId() : user.getMobNo();
		return patientRepo.findByMobileNo(mobileNo)
				.map(Patient::isBlocked)
				.orElse(false);
	}

	private boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}
}
