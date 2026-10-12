package com.doctor_patient.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.doctor_patient.DTO.LoginDTO;
import com.doctor_patient.DTO.LoginResponseDTO;
import com.doctor_patient.entity.User;
import com.doctor_patient.security.JwtService;
import com.doctor_patient.service.LoginService;


@RestController
@RequestMapping("/api/v1/login")
public class LoginController {

	private final LoginService loginService;
	private final JwtService jwtService;

	public LoginController(LoginService loginService, JwtService jwtService) {
		this.loginService = loginService;
		this.jwtService = jwtService;
	}
	
	@PostMapping("/loginUser")
	public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
	    Optional<User> loggedInUser = loginService.validateUser(loginDTO);
	    if (loggedInUser.isPresent()) {
	    	User user = loggedInUser.get();
	        return ResponseEntity.ok(new LoginResponseDTO(
	        		jwtService.generateToken(user),
	        		"Bearer",
	        		user.getUserId(),
	        		user.getName(),
	        		user.getRole(),
	        		user.getStatus()));
	    } else {
	        return ResponseEntity
	                .status(HttpStatus.UNAUTHORIZED)
	                .body("Invalid username/password or inactive user");
	    }
	}

}
