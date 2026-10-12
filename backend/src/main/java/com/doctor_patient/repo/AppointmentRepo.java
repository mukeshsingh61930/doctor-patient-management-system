package com.doctor_patient.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.doctor_patient.entity.Appointment;
import com.doctor_patient.enums.AppointmentStatus;

@Repository
public interface AppointmentRepo extends JpaRepository<Appointment,Long>{

	@Query("select count(a) > 0 from Appointment a "
			+ "where a.appointmentDate = :appointmentDate "
			+ "and a.slot.slotId = :slotId "
			+ "and a.doctor.userId = :doctorUserId "
			+ "and a.appointmentStatus <> :excludedStatus "
			+ "and (:appointmentId is null or a.appointmentId <> :appointmentId)")
	boolean existsDoctorSlotConflict(
			@Param("appointmentDate") LocalDate appointmentDate,
			@Param("slotId") Long slotId,
			@Param("doctorUserId") String doctorUserId,
			@Param("excludedStatus") AppointmentStatus excludedStatus,
			@Param("appointmentId") Long appointmentId);

	@Query("select count(a) > 0 from Appointment a "
			+ "where a.appointmentDate = :appointmentDate "
			+ "and a.slot.slotId = :slotId "
			+ "and a.patient.patientId = :patientId "
			+ "and a.appointmentStatus <> :excludedStatus "
			+ "and (:appointmentId is null or a.appointmentId <> :appointmentId)")
	boolean existsPatientSlotConflict(
			@Param("appointmentDate") LocalDate appointmentDate,
			@Param("slotId") Long slotId,
			@Param("patientId") Long patientId,
			@Param("excludedStatus") AppointmentStatus excludedStatus,
			@Param("appointmentId") Long appointmentId);

	List<Appointment> findByAppointmentDateOrderBySlot_StartTimeAsc(LocalDate appointmentDate);

	List<Appointment> findByPatient_MobileNoOrderByAppointmentDateAscSlot_StartTimeAsc(String mobileNo);

	List<Appointment> findByDoctor_UserIdOrderByAppointmentDateAscSlot_StartTimeAsc(String doctorUserId);

	List<Appointment> findTop5ByAppointmentDateGreaterThanEqualOrderByAppointmentDateAscSlot_StartTimeAsc(LocalDate appointmentDate);

	List<Appointment> findTop5ByPatient_MobileNoAndAppointmentDateGreaterThanEqualOrderByAppointmentDateAscSlot_StartTimeAsc(String mobileNo, LocalDate appointmentDate);

	List<Appointment> findTop5ByDoctor_UserIdAndAppointmentDateGreaterThanEqualOrderByAppointmentDateAscSlot_StartTimeAsc(String doctorUserId, LocalDate appointmentDate);

	long countByAppointmentDate(LocalDate appointmentDate);

	long countByPatient_MobileNoAndAppointmentDate(String mobileNo, LocalDate appointmentDate);

	long countByDoctor_UserIdAndAppointmentDate(String doctorUserId, LocalDate appointmentDate);

	long countByAppointmentStatus(AppointmentStatus appointmentStatus);

	long countByPatient_MobileNoAndAppointmentStatus(String mobileNo, AppointmentStatus appointmentStatus);

	long countByDoctor_UserIdAndAppointmentStatus(String doctorUserId, AppointmentStatus appointmentStatus);

	@Query("select count(distinct a.patient.patientId) from Appointment a "
			+ "where a.doctor.userId = :doctorUserId "
			+ "and a.appointmentStatus <> :excludedStatus")
	long countDistinctPatientsByDoctorUserId(
			@Param("doctorUserId") String doctorUserId,
			@Param("excludedStatus") AppointmentStatus excludedStatus);

	@Query("select a.slot.slotId from Appointment a "
			+ "where a.appointmentDate = :appointmentDate "
			+ "and a.appointmentStatus <> :excludedStatus")
	List<Long> findBookedSlotIdsByDate(
			@Param("appointmentDate") LocalDate appointmentDate,
			@Param("excludedStatus") AppointmentStatus excludedStatus);

	@Query("select a.slot.slotId from Appointment a "
			+ "where a.appointmentDate = :appointmentDate "
			+ "and a.doctor.userId = :doctorUserId "
			+ "and a.appointmentStatus <> :excludedStatus")
	List<Long> findBookedSlotIdsByDateAndDoctor(
			@Param("appointmentDate") LocalDate appointmentDate,
			@Param("doctorUserId") String doctorUserId,
			@Param("excludedStatus") AppointmentStatus excludedStatus);

	@Query("select a.slot.slotId from Appointment a "
			+ "where a.appointmentDate = :appointmentDate "
			+ "and a.doctor.sId = (select dp.staffProfile.user.sId from DoctorProfile dp where dp.doctorId = :doctorId) "
			+ "and a.appointmentStatus <> :excludedStatus")
	List<Long> findBookedSlotIdsByDateAndDoctorId(
			@Param("appointmentDate") LocalDate appointmentDate,
			@Param("doctorId") Long doctorId,
			@Param("excludedStatus") AppointmentStatus excludedStatus);
}
