package com.hospital.sistema.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

import com.hospital.sistema.enums.ConsultationStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_consultation")
@Getter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Consultation implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;

	@NotNull
	@Column(nullable = false)
	private LocalDate date;

	@NotNull
	@Column(nullable = false)
	private LocalTime time;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ConsultationStatus status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "patient_id", nullable = false)
	private Patient patient;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "doctor_id", nullable = false)
	private Doctor doctor;

	@ManyToOne
	@JoinColumn(name = "agenda_id")
	private Agenda agenda;

	public Consultation(LocalDate date, LocalTime time, ConsultationStatus status, Patient patient, Doctor doctor) {
		this.date = date;
		this.time = time;
		this.status = status;
		this.patient = patient;
		this.doctor = doctor;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	public void setStatus(ConsultationStatus status) {
		this.status = status;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}

	public void setAgenda(Agenda agenda) {
		this.agenda = agenda;
	}
}