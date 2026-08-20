package com.hospital.sistema.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.sistema.entities.Consultation;
import com.hospital.sistema.entities.Doctor;
import com.hospital.sistema.entities.Patient;
import com.hospital.sistema.enums.ConsultationStatus;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {

	// Buscar por paciente
	List<Consultation> findByPatient(Patient patient);

	// Buscar por médico
	List<Consultation> findByDoctor(Doctor doctor);

	// Buscar por estado
	List<Consultation> findByStatus(ConsultationStatus status);

	// Buscar por paciente e estado
	List<Consultation> findByPatientAndStatus(Patient patient, ConsultationStatus status);

	// Buscar por data
	List<Consultation> findByDate(LocalDate date);

}