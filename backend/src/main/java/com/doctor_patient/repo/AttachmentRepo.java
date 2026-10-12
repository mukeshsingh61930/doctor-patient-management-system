package com.doctor_patient.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doctor_patient.entity.Attachment;

@Repository
public interface AttachmentRepo extends JpaRepository<Attachment,Long>{

}
