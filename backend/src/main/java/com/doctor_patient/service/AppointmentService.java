package com.doctor_patient.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.doctor_patient.DTO.AppointmentDTO;
import com.doctor_patient.entity.Appointment;
import com.doctor_patient.entity.DoctorProfile;
import com.doctor_patient.entity.Patient;
import com.doctor_patient.entity.Slot;
import com.doctor_patient.entity.User;
import com.doctor_patient.enums.AppointmentStatus;
import com.doctor_patient.enums.Role;
import com.doctor_patient.enums.SlotStatus;
import com.doctor_patient.enums.Status;
import com.doctor_patient.repo.AppointmentRepo;
import com.doctor_patient.repo.DoctorProfileRepo;
import com.doctor_patient.repo.PatientRepo;
import com.doctor_patient.repo.SlotRepo;
import com.doctor_patient.repo.UserRepo;

@Service
public class AppointmentService {

	private final AppointmentRepo appointmentRepo;
	private final PatientRepo patientRepo;
	private final SlotRepo slotRepo;
	private final UserRepo userRepo;
	private final DoctorProfileRepo doctorProfileRepo;

	public AppointmentService(
			AppointmentRepo appointmentRepo,
			PatientRepo patientRepo,
			SlotRepo slotRepo,
			UserRepo userRepo,
			DoctorProfileRepo doctorProfileRepo) {
		this.appointmentRepo = appointmentRepo;
		this.patientRepo = patientRepo;
		this.slotRepo = slotRepo;
		this.userRepo = userRepo;
		this.doctorProfileRepo = doctorProfileRepo;
	}

	public Appointment save(AppointmentDTO appointmentDTO, Role actorRole, String actorUserId) {
		Patient patient = patientRepo.findById(appointmentDTO.getPatientId()).orElseThrow();
		if (actorRole == Role.PATIENT && !patient.getMobileNo().equals(actorUserId)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Patients can book appointments only for themselves");
		}
		if (patient.isBlocked()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Blocked patients cannot book appointments");
		}

		Slot slot = slotRepo.findById(appointmentDTO.getSlotId()).orElseThrow();
		if (slot.getSlotStatus() != SlotStatus.AVAILABLE) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selected slot is not available");
		}

		User doctor = resolveDoctor(appointmentDTO);
		if (doctor.getRole() != Role.DOCTOR || doctor.getStatus() != Status.ACTIVE) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please select an active doctor");
		}
		if (actorRole == Role.DOCTOR && !doctor.getUserId().equals(actorUserId)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Doctors can create appointments only in their own calendar");
		}

		boolean doctorConflictExists = appointmentRepo.existsDoctorSlotConflict(
				appointmentDTO.getAppointmentDate(),
				appointmentDTO.getSlotId(),
				doctor.getUserId(),
				AppointmentStatus.CANCELLED,
				appointmentDTO.getAppointmentId());

		if (doctorConflictExists) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "This doctor already has an appointment in the selected slot");
		}

		boolean patientConflictExists = appointmentRepo.existsPatientSlotConflict(
				appointmentDTO.getAppointmentDate(),
				appointmentDTO.getSlotId(),
				patient.getPatientId(),
				AppointmentStatus.CANCELLED,
				appointmentDTO.getAppointmentId());

		if (patientConflictExists) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Patient already has an appointment in the selected slot");
		}

		Appointment appointment = appointmentDTO.getAppointmentId() == null
				? new Appointment()
				: appointmentRepo.findById(appointmentDTO.getAppointmentId()).orElseThrow();
		appointment.setAppointmentDate(appointmentDTO.getAppointmentDate());
		appointment.setAppointmentStatus(appointmentDTO.getAppointmentStatus());
		appointment.setPatient(patient);
		appointment.setSlot(slot);
		appointment.setDoctor(doctor);
		appointment.setDoctorNotes(appointmentDTO.getDoctorNotes());
		appointment.setDiagnosis(appointmentDTO.getDiagnosis());
		appointment.setPrescription(appointmentDTO.getPrescription());
		return appointmentRepo.save(appointment);
	}

	private User resolveDoctor(AppointmentDTO appointmentDTO) {
		if (appointmentDTO.getDoctorId() != null) {
			DoctorProfile doctorProfile = doctorProfileRepo.findById(appointmentDTO.getDoctorId()).orElseThrow();
			return doctorProfile.getStaffProfile().getUser();
		}
		if (appointmentDTO.getDoctorUserId() != null && !appointmentDTO.getDoctorUserId().isBlank()) {
			return userRepo.findByUserId(appointmentDTO.getDoctorUserId()).orElseThrow();
		}
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Doctor is required");
	}

	public Appointment updateConsultation(Long appointmentId, AppointmentDTO appointmentDTO, Role actorRole, String actorUserId) {
		Appointment appointment = appointmentRepo.findById(appointmentId).orElseThrow();
		if (actorRole == Role.PATIENT) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Patients cannot update consultation details");
		}
		if (actorRole == Role.DOCTOR && !appointment.getDoctor().getUserId().equals(actorUserId)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Doctors can update only their own appointments");
		}
		appointment.setAppointmentStatus(appointmentDTO.getAppointmentStatus());
		appointment.setDoctorNotes(appointmentDTO.getDoctorNotes());
		appointment.setDiagnosis(appointmentDTO.getDiagnosis());
		appointment.setPrescription(appointmentDTO.getPrescription());
		return appointmentRepo.save(appointment);
	}
}
