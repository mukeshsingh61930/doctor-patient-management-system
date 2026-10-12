package com.doctor_patient.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doctor_patient.entity.StaffProfile;

@Repository
public interface StaffProfileRepo extends JpaRepository<StaffProfile, Long> {

	boolean existsByEmployeeCode(String employeeCode);

	Optional<StaffProfile> findByUser_UserId(String userId);

	Optional<StaffProfile> findByEmployeeCode(String employeeCode);
}
