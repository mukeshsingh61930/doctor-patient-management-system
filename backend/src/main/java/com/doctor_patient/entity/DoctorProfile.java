package com.doctor_patient.entity;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
public class DoctorProfile extends BaseModal {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long doctorId;

	@OneToOne
	@JoinColumn(name = "staff_id")
	@JsonIgnore
	private StaffProfile staffProfile;

	private String specialization;

	private String registrationNo;

	private BigDecimal consultationFee;
}
