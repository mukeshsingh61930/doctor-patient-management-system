package com.doctor_patient.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.doctor_patient.DTO.LoginDTO;
import com.doctor_patient.entity.User;
import com.doctor_patient.enums.Status;
import com.doctor_patient.repo.UserRepo;

@Service
public class LoginService {

	private final UserRepo userRepo;
	private final PasswordEncoder passwordEncoder;

	public LoginService(UserRepo userRepo, PasswordEncoder passwordEncoder) {
		this.userRepo = userRepo;
		this.passwordEncoder = passwordEncoder;
	}
	
	public Optional<User> validateUser(LoginDTO loginDTO) {
		return userRepo.findByUserId(loginDTO.getUserId())
				.filter(user -> user.getStatus() == null || user.getStatus() == Status.ACTIVE)
				.filter(user -> passwordMatches(loginDTO.getPassword(), user))
				.map(user -> migratePlainPassword(loginDTO.getPassword(), user));
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
}
