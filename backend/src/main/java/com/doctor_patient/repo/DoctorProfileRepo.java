package com.doctor_patient.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doctor_patient.entity.DoctorProfile;
import com.doctor_patient.enums.Status;

@Repository
public interface DoctorProfileRepo extends JpaRepository<DoctorProfile, Long> {

	List<DoctorProfile> findByStaffProfile_User_StatusOrderByStaffProfile_User_NameAsc(Status status);

	Optional<DoctorProfile> findByStaffProfile_User_UserId(String userId);

	Optional<DoctorProfile> findByStaffProfile_StaffId(Long staffId);
}
