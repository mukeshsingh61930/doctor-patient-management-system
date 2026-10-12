package com.doctor_patient.entity;

import com.doctor_patient.enums.Status;
import com.doctor_patient.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "`user`")
public class User {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long sId;
	private String name;
	private String userId;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;
	@Enumerated(EnumType.STRING)
	private Role role;
	@Enumerated(EnumType.STRING)
	private Status status;
	private String mobNo;
}
