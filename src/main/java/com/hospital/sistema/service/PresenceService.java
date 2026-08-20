package com.hospital.sistema.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.sistema.dtos.PresenceCreateDto;
import com.hospital.sistema.dtos.PresenceResponseDto;
import com.hospital.sistema.dtos.PresenceUpdateDto;
import com.hospital.sistema.entities.Consultation;
import com.hospital.sistema.entities.Presence;
import com.hospital.sistema.enums.PresenceStatus;
import com.hospital.sistema.repository.ConsultationRepository;
import com.hospital.sistema.repository.PresenceRepository;
import com.hospital.sistema.service.exceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PresenceService {

	private final PresenceRepository repository;
	private final ConsultationRepository consultationRepository;

	@Transactional(readOnly = true)
	public List<PresenceResponseDto> findAll() {
		return repository.findAll().stream().map(this::entityToDto).toList();
	}

	@Transactional(readOnly = true)
	public PresenceResponseDto findById(Long id) {
		return entityToDto(repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Presença não encontrada: " + id)));
	}

	@Transactional(readOnly = true)
	public List<PresenceResponseDto> findByStatus(PresenceStatus status) {
		return repository.findByStatus(status).stream().map(this::entityToDto).toList();
	}

	@Transactional
	public void create(PresenceCreateDto dto) {
		Consultation consultation = consultationRepository.findById(dto.getConsultationId())
				.orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada!"));

		if (repository.existsByConsultation(consultation)) {
			throw new RuntimeException("Esta consulta já tem uma presença registada!");
		}

		if (dto.getStatus() == PresenceStatus.AUSENTE
				&& (dto.getJustification() == null || dto.getJustification().isBlank())) {
			throw new RuntimeException("Justificação obrigatória para ausência!");
		}

		Presence presence = new Presence(dto.getStatus(), dto.getJustification(), consultation);
		repository.save(presence);
	}

	@Transactional
	public void update(Long id, PresenceUpdateDto dto) {
		Presence presence = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Presença não encontrada: " + id));

		if (dto.getStatus() == PresenceStatus.AUSENTE
				&& (dto.getJustification() == null || dto.getJustification().isBlank())) {
			throw new RuntimeException("Justificação obrigatória para ausência!");
		}

		presence.setStatus(dto.getStatus());
		presence.setJustification(dto.getJustification());
		repository.save(presence);
	}

	@Transactional
	public void delete(Long id) {
		if (!repository.existsById(id)) {
			throw new ResourceNotFoundException("Presença não encontrada: " + id);
		}
		repository.deleteById(id);
	}

	private PresenceResponseDto entityToDto(Presence entity) {
		PresenceResponseDto dto = new PresenceResponseDto();
		dto.setId(entity.getId());
		dto.setStatus(entity.getStatus());
		dto.setJustification(entity.getJustification());
		dto.setConsultationId(entity.getConsultation().getId());
		dto.setPatientName(entity.getConsultation().getPatient().getName());
		dto.setDoctorName(entity.getConsultation().getDoctor().getName());
		return dto;
	}
}