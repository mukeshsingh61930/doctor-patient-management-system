package com.doctor_patient.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doctor_patient.entity.NurseProfile;

@Repository
public interface NurseProfileRepo extends JpaRepository<NurseProfile, Long> {

	Optional<NurseProfile> findByStaffProfile_StaffId(Long staffId);
}
