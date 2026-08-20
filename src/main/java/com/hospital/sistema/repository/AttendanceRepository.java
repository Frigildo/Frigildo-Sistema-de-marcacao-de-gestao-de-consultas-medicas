package com.hospital.sistema.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hospital.sistema.entities.Attendance;
import com.hospital.sistema.entities.Consultation;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

	Optional<Attendance> findByConsultation(Consultation consultation);

	boolean existsByConsultation(Consultation consultation);

	List<Attendance> findByAttendanceDate(LocalDate date);

	@Query("SELECT a FROM Attendance a " + "WHERE a.consultation.patient.id = :patientId")
	List<Attendance> findByPatientId(@Param("patientId") Long patientId);

	@Query("SELECT a FROM Attendance a " + "WHERE a.consultation.doctor.id = :doctorId")
	List<Attendance> findByDoctorId(@Param("doctorId") Long doctorId);

	@Query("SELECT c FROM Consultation c WHERE c.doctor.id = :doctorId "
			+ "AND c.status = com.hospital.sistema.enums.ConsultationStatus.AGENDADA "
			+ "AND NOT EXISTS (SELECT a FROM Attendance a WHERE a.consultation = c)")
	List<Consultation> findAvailableForAttendanceByDoctor(@Param("doctorId") Long doctorId);

	List<Attendance> findByConsultationPatientNameContainingIgnoreCase(String name);
}