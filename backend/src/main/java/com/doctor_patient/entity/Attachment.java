package com.doctor_patient.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Attachment extends BaseModal{

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long attachmentId;
	private String attachmentName;
	private String attachmentType;
	private String imageUrl;
	@Column(columnDefinition = "LONGTEXT")
	private String data;
	
}
