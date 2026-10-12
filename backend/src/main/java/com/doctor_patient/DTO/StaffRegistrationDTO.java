package com.doctor_patient.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.doctor_patient.enums.Role;
import com.doctor_patient.enums.Status;

import lombok.Data;

@Data
public class StaffRegistrationDTO {

	private String name;
	private String userId;
	private String password;
	private String mobNo;
	private Role role;
	private Status status;
	private String employeeCode;
	private String department;
	private String designation;
	private String qualification;
	private LocalDate joiningDate;
	private String address;
	private String specialization;
	private String registrationNo;
	private BigDecimal consultationFee;
	private String shift;
	private String ward;
}
