package com.doctor_patient.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.doctor_patient.DTO.StaffRegistrationDTO;
import com.doctor_patient.Exception.DuplicateResourceException;
import com.doctor_patient.entity.DoctorProfile;
import com.doctor_patient.entity.NurseProfile;
import com.doctor_patient.entity.StaffProfile;
import com.doctor_patient.entity.User;
import com.doctor_patient.enums.Role;
import com.doctor_patient.enums.Status;
import com.doctor_patient.repo.DoctorProfileRepo;
import com.doctor_patient.repo.NurseProfileRepo;
import com.doctor_patient.repo.StaffProfileRepo;
import com.doctor_patient.repo.UserRepo;

@Service
public class StaffProfileService {

	private final StaffProfileRepo staffProfileRepo;
	private final DoctorProfileRepo doctorProfileRepo;
	private final NurseProfileRepo nurseProfileRepo;
	private final UserRepo userRepo;
	private final PasswordEncoder passwordEncoder;

	public StaffProfileService(
			StaffProfileRepo staffProfileRepo,
			DoctorProfileRepo doctorProfileRepo,
			NurseProfileRepo nurseProfileRepo,
			UserRepo userRepo,
			PasswordEncoder passwordEncoder) {
		this.staffProfileRepo = staffProfileRepo;
		this.doctorProfileRepo = doctorProfileRepo;
		this.nurseProfileRepo = nurseProfileRepo;
		this.userRepo = userRepo;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public StaffProfile createStaff(StaffRegistrationDTO request) {
		validateRequest(request, null);

		User user = new User();
		applyUserFields(user, request);

		StaffProfile staffProfile = new StaffProfile();
		staffProfile.setUser(user);
		applyStaffFields(staffProfile, request);
		staffProfile.setStatus(user.getStatus());

		StaffProfile savedStaffProfile = staffProfileRepo.save(staffProfile);
		syncRoleProfile(savedStaffProfile, request);

		return savedStaffProfile;
	}

	@Transactional
	public StaffProfile updateStaff(Long staffId, StaffRegistrationDTO request) {
		validateRequest(request, staffId);

		StaffProfile staffProfile = staffProfileRepo.findById(staffId).orElseThrow();
		applyUserFields(staffProfile.getUser(), request);
		applyStaffFields(staffProfile, request);
		staffProfile.setStatus(staffProfile.getUser().getStatus());

		StaffProfile savedStaffProfile = staffProfileRepo.save(staffProfile);
		syncRoleProfile(savedStaffProfile, request);
		return savedStaffProfile;
	}

	public List<StaffProfile> getAllStaff() {
		return staffProfileRepo.findAll();
	}

	@Transactional
	public StaffProfile updateStatus(Long staffId, Status status) {
		StaffProfile staffProfile = staffProfileRepo.findById(staffId).orElseThrow();
		staffProfile.setStatus(status);
		staffProfile.getUser().setStatus(status);
		return staffProfileRepo.save(staffProfile);
	}

	private void validateRequest(StaffRegistrationDTO request, Long currentStaffId) {
		if (request.getRole() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role is required");
		}
		if (request.getRole() == Role.PATIENT) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Create patient users from patient registration");
		}
		if (isBlank(request.getName()) || isBlank(request.getUserId()) || isBlank(request.getPassword())
				|| isBlank(request.getMobNo())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name, role, userId, password and mobileNo are required");
		}

		userRepo.findByUserId(request.getUserId()).ifPresent(existingUser -> {
			boolean sameStaff = staffProfileRepo.findByUser_UserId(existingUser.getUserId())
					.map(StaffProfile::getStaffId)
					.filter(existingStaffId -> existingStaffId.equals(currentStaffId))
					.isPresent();

			if (!sameStaff) {
				throw new DuplicateResourceException("UserId already exists: " + request.getUserId());
			}
		});

		if (!isBlank(request.getEmployeeCode())) {
			staffProfileRepo.findByEmployeeCode(request.getEmployeeCode()).ifPresent(existingStaff -> {
				if (!existingStaff.getStaffId().equals(currentStaffId)) {
					throw new DuplicateResourceException("Employee code already exists: " + request.getEmployeeCode());
				}
			});
		}
	}

	private void applyUserFields(User user, StaffRegistrationDTO request) {
		user.setName(request.getName());
		user.setUserId(request.getUserId());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setMobNo(request.getMobNo());
		user.setRole(request.getRole());
		user.setStatus(request.getStatus() == null ? Status.ACTIVE : request.getStatus());
	}

	private void applyStaffFields(StaffProfile staffProfile, StaffRegistrationDTO request) {
		staffProfile.setEmployeeCode(request.getEmployeeCode());
		staffProfile.setDepartment(request.getDepartment());
		staffProfile.setDesignation(request.getDesignation());
		staffProfile.setQualification(request.getQualification());
		staffProfile.setJoiningDate(request.getJoiningDate());
		staffProfile.setAddress(request.getAddress());
	}

	private void syncRoleProfile(StaffProfile staffProfile, StaffRegistrationDTO request) {
		if (request.getRole() == Role.DOCTOR) {
			deleteNurseProfile(staffProfile);
			DoctorProfile doctorProfile = doctorProfileRepo.findByStaffProfile_StaffId(staffProfile.getStaffId())
					.orElseGet(DoctorProfile::new);
			doctorProfile.setStaffProfile(staffProfile);
			doctorProfile.setSpecialization(request.getSpecialization());
			doctorProfile.setRegistrationNo(request.getRegistrationNo());
			doctorProfile.setConsultationFee(request.getConsultationFee());
			doctorProfileRepo.save(doctorProfile);
			staffProfile.setDoctorProfile(doctorProfile);
			staffProfile.setNurseProfile(null);
			return;
		}

		if (request.getRole() == Role.NURSE) {
			deleteDoctorProfile(staffProfile);
			NurseProfile nurseProfile = nurseProfileRepo.findByStaffProfile_StaffId(staffProfile.getStaffId())
					.orElseGet(NurseProfile::new);
			nurseProfile.setStaffProfile(staffProfile);
			nurseProfile.setShift(request.getShift());
			nurseProfile.setWard(request.getWard());
			nurseProfileRepo.save(nurseProfile);
			staffProfile.setDoctorProfile(null);
			staffProfile.setNurseProfile(nurseProfile);
			return;
		}

		deleteDoctorProfile(staffProfile);
		deleteNurseProfile(staffProfile);
		staffProfile.setDoctorProfile(null);
		staffProfile.setNurseProfile(null);
	}

	private void deleteDoctorProfile(StaffProfile staffProfile) {
		doctorProfileRepo.findByStaffProfile_StaffId(staffProfile.getStaffId()).ifPresent(doctorProfileRepo::delete);
	}

	private void deleteNurseProfile(StaffProfile staffProfile) {
		nurseProfileRepo.findByStaffProfile_StaffId(staffProfile.getStaffId()).ifPresent(nurseProfileRepo::delete);
	}

	private boolean isBlank(String value) {
		return value == null || value.isBlank();
	}
}
