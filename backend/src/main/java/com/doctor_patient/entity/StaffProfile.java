package com.doctor_patient.entity;

import java.time.LocalDate;

import com.doctor_patient.enums.Status;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
@Entity
public class StaffProfile extends BaseModal {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long staffId;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "user_account_id")
	private User user;

	private String employeeCode;

	private String department;

	private String designation;

	private String qualification;

	private LocalDate joiningDate;

	private String address;

	@Enumerated(EnumType.STRING)
	private Status status;

	@OneToOne(mappedBy = "staffProfile")
	private DoctorProfile doctorProfile;

	@OneToOne(mappedBy = "staffProfile")
	private NurseProfile nurseProfile;
}
