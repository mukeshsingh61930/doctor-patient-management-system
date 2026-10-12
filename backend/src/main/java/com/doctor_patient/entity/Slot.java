package com.doctor_patient.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import com.doctor_patient.enums.SlotStatus;
import com.doctor_patient.enums.Status;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Slot {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long slotId;
	
	private LocalTime startTime;
	private LocalTime endTime;
	
	@Enumerated(EnumType.STRING)
	private SlotStatus slotStatus;
}
