package com.doctor_patient.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.doctor_patient.entity.Attachment;
import com.doctor_patient.repo.AttachmentRepo;

@RestController
@RequestMapping("/api/v1/attachment")
public class AttachmentController {

	@Autowired
	AttachmentRepo attachmentRepo;
	
	@PostMapping("/saveAttachment")
	public ResponseEntity<Attachment> login(@RequestBody Attachment attachment) {
		Attachment savedAttachment=attachmentRepo.save(attachment);
		return new ResponseEntity<>(savedAttachment,HttpStatus.CREATED);
	}
}
