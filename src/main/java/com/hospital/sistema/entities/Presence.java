package com.hospital.sistema.entities;

import java.io.Serializable;

import com.hospital.sistema.enums.PresenceStatus;

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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_presence")
@Getter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Presence implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;

	@Enumerated(EnumType.STRING)
	private PresenceStatus status;

	@Column(length = 500)
	private String justification;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "consultation_id", nullable = false, unique = true)
	private Consultation consultation;

	public Presence(PresenceStatus status, String justification, Consultation consultation) {
		this.status = status;
		this.justification = justification;
		this.consultation = consultation;
	}

	public void setStatus(PresenceStatus status) {
		this.status = status;
	}

	public void setJustification(String justification) {
		this.justification = justification;
	}

	public void setConsultation(Consultation consultation) {
		this.consultation = consultation;
	}
}