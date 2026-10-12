package com.doctor_patient.DTO;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DoctorOptionDTO {

	private Long doctorId;
	private String name;
	private String specialization;
	private String registrationNo;
	private BigDecimal consultationFee;
}
