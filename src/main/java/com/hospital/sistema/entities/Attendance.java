package com.hospital.sistema.entities;

import java.io.Serializable;
import java.time.LocalDate;

import com.hospital.sistema.enums.AttendanceStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_attendance")
@Getter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Attendance implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;

	@Column(length = 500)
	private String observations;

	@NotNull
	@Column(nullable = false)
	private LocalDate attendanceDate;

	@Column(columnDefinition = "TEXT")
	private String diagnosis;

	@Column(columnDefinition = "TEXT")
	private String medication;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AttendanceStatus status = AttendanceStatus.PENDENTE;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "consultation_id", nullable = false, unique = true)
	private Consultation consultation;

	public Attendance(String observations, LocalDate attendanceDate, Consultation consultation) {
		this.observations = observations;
		this.attendanceDate = attendanceDate;
		this.consultation = consultation;
	}

	public void setObservations(String observations) {
		this.observations = observations;
	}

	public void setAttendanceDate(LocalDate date) {
		this.attendanceDate = date;
	}

	public void setConsultation(Consultation consultation) {
		this.consultation = consultation;
	}

	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}

	public void setMedication(String medication) {
		this.medication = medication;
	}

	public void setStatus(AttendanceStatus status) {
		this.status = status;
	}

}