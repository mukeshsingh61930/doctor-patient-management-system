package com.doctor_patient.DTO;

import java.time.LocalDate;

import com.doctor_patient.enums.AppointmentStatus;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class AppointmentDTO {

	private Long appointmentId;
	@Enumerated(EnumType.STRING)
	private AppointmentStatus appointmentStatus;
	private LocalDate appointmentDate;
	private Long patientId;
	private Long slotId;
	private Long doctorId;
	private String doctorUserId;
	private String doctorNotes;
	private String diagnosis;
	private String prescription;
}
