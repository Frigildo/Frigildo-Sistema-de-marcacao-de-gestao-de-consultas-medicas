package com.hospital.sistema.service;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.sistema.dtos.AgendaCreateDto;
import com.hospital.sistema.dtos.AgendaResponseDto;
import com.hospital.sistema.entities.Agenda;
import com.hospital.sistema.entities.Consultation;
import com.hospital.sistema.entities.Doctor;
import com.hospital.sistema.entities.Patient;
import com.hospital.sistema.entities.User;
import com.hospital.sistema.enums.ConsultationStatus;
import com.hospital.sistema.repository.AgendaRepository;
import com.hospital.sistema.repository.ConsultationRepository;
import com.hospital.sistema.repository.DoctorRepository;
import com.hospital.sistema.repository.PatientRepository;
import com.hospital.sistema.repository.UserRepository;
import com.hospital.sistema.service.exceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendaService {

	private final AgendaRepository repository;
	private final DoctorRepository doctorRepository;
	private final ConsultationRepository consultationRepository;
	private final PatientRepository patientRepository;
	private final UserRepository userRepository;

	// ─────────────────────────────────────
	// READ
	// ─────────────────────────────────────

	@Transactional(readOnly = true)
	public List<AgendaResponseDto> findByDoctor(Long doctorId) {
		Doctor doctor = doctorRepository.findById(doctorId)
				.orElseThrow(() -> new ResourceNotFoundException("Médico não encontrado!"));
		return repository.findByDoctor(doctor).stream().map(this::entityToDto).toList();
	}

	@Transactional(readOnly = true)
	public AgendaResponseDto findById(Long id) {
		return entityToDto(
				repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Horário não encontrado!")));
	}

	@Transactional(readOnly = true)
	public List<AgendaResponseDto> findAvailable() {
		return repository.findByAvailableTrue().stream().map(this::entityToDto).toList();
	}

	// ─────────────────────────────────────
	// ADMIN — cria horário para um médico
	// ─────────────────────────────────────

	@Transactional
	public void createByAdmin(AgendaCreateDto dto, Long doctorId) {
		Doctor doctor = doctorRepository.findById(doctorId)
				.orElseThrow(() -> new ResourceNotFoundException("Médico não encontrado!"));

		if (!dto.getEndTime().isAfter(dto.getStartTime())) {
			throw new RuntimeException("Hora de fim deve ser depois " + "da hora de início!");
		}

		Agenda agenda = new Agenda(dto.getDate(), dto.getStartTime(), dto.getEndTime(), doctor);
		repository.save(agenda);
	}

	// ─────────────────────────────────────
	// ADMIN — apaga horário
	// ─────────────────────────────────────

	@Transactional
	public void deleteByAdmin(Long id) {
		Agenda agenda = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Horário não encontrado!"));

		if (!agenda.isAvailable()) {
			throw new RuntimeException("Não é possível apagar um horário " + "já marcado!");
		}
		repository.deleteById(id);
	}

	// ─────────────────────────────────────
	// PACIENTE — marca consulta num horário
	// ─────────────────────────────────────

	@Transactional
	public void bookSlot(Long agendaId, Principal principal) {
		Agenda agenda = repository.findById(agendaId)
				.orElseThrow(() -> new ResourceNotFoundException("Horário não encontrado!"));

		if (!agenda.isAvailable()) {
			throw new RuntimeException("Este horário já não está disponível!");
		}

		User user = userRepository.findByEmail(principal.getName())
				.orElseThrow(() -> new ResourceNotFoundException("Utilizador não encontrado!"));

		Patient patient = patientRepository.findByUser(user)
				.orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado!"));

		Consultation consultation = new Consultation(agenda.getDate(), agenda.getStartTime(),
				ConsultationStatus.AGENDADA, patient, agenda.getDoctor());
		consultation.setAgenda(agenda);
		consultationRepository.save(consultation);

		agenda.setAvailable(false);
		repository.save(agenda);
	}

	// ─────────────────────────────────────
	// HELPER
	// ─────────────────────────────────────

	private AgendaResponseDto entityToDto(Agenda entity) {
		AgendaResponseDto dto = new AgendaResponseDto();
		dto.setId(entity.getId());
		dto.setDate(entity.getDate());
		dto.setStartTime(entity.getStartTime());
		dto.setEndTime(entity.getEndTime());
		dto.setAvailable(entity.isAvailable());
		dto.setDoctorId(entity.getDoctor().getId());
		dto.setDoctorName(entity.getDoctor().getName());
		dto.setDoctorSpecialty(
				entity.getDoctor().getSpecialty() != null ? entity.getDoctor().getSpecialty().getName() : "N/A");
		return dto;
	}
}