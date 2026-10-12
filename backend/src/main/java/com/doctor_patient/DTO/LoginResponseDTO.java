package com.doctor_patient.DTO;

import com.doctor_patient.enums.Status;
import com.doctor_patient.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDTO {

	private String token;
	private String tokenType;
	private String userId;
	private String name;
	private Role role;
	private Status status;
}
