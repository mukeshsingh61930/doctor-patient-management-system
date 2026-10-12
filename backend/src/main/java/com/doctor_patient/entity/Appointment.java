package com.doctor_patient.entity;

import java.time.LocalDate;

import com.doctor_patient.enums.AppointmentStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Appointment extends BaseModal{

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long appointmentId;
	
	@Enumerated(EnumType.STRING)
	private AppointmentStatus appointmentStatus;
	
	private LocalDate appointmentDate;
	
	@ManyToOne
	@JoinColumn(name = "patient_id")
	private Patient patient;
	
	@ManyToOne
	@JoinColumn(name = "slot_id")
	private Slot slot;

	@ManyToOne
	@JoinColumn(name = "doctor_id")
	private User doctor;

	private String doctorNotes;

	private String diagnosis;

	private String prescription;
}
