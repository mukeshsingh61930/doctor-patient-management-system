package com.doctor_patient.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doctor_patient.entity.Patient;

@Repository
public interface PatientRepo extends JpaRepository<Patient,Long>{

	Optional<Patient> findByMobileNo(String mobileNo);
}
