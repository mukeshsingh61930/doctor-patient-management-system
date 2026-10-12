package com.doctor_patient.entity;

import java.util.List;

//import jakarta.persistence.*;

import com.doctor_patient.enums.Gender;
import com.doctor_patient.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
@Entity
public class Patient extends BaseModal{

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long patientId;
	
	private String PatientName;
	
	private String mobileNo;
	
	@Enumerated(EnumType.STRING)
	private Status status;
	
	private String illnessHistory;
	
	@Enumerated(EnumType.STRING)
	private Gender gender;
	
	private String state;
	
	private String city;
	@JsonProperty("isBlocked")
	private boolean isBlocked;
	
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "attachment_id")
	private Attachment attachment;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_account_id")
	private User user;
	
	@OneToMany(mappedBy = "patient")
	@JsonIgnore
	private List<Appointment> appointments;


	
}
