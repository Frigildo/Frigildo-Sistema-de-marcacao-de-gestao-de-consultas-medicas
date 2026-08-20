package com.hospital.sistema.service;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.sistema.dtos.PatientCreateDtoAdmin;
import com.hospital.sistema.dtos.PatientResponseDto;
import com.hospital.sistema.dtos.PatientUpdateDtoAdmin;
import com.hospital.sistema.entities.Patient;
import com.hospital.sistema.entities.User;
import com.hospital.sistema.repository.PatientRepository;
import com.hospital.sistema.repository.UserRepository;
import com.hospital.sistema.service.exceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PatientService {

	private final PatientRepository repository;
	private final UserRepository userRepository;

	// ─────────────────────────────────────
	// READ
	// ─────────────────────────────────────

	@Transactional(readOnly = true)
	public List<PatientResponseDto> findAll() {
		return repository.findAll().stream().map(this::entityToDto).toList();
	}

	@Transactional(readOnly = true)
	public PatientResponseDto findById(Long id) {
		return entityToDto(repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado: " + id)));
	}

	@Transactional(readOnly = true)
	public Patient findEntityById(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado: " + id));
	}

	@Transactional(readOnly = true)
	public List<PatientResponseDto> findByName(String name) {
		return repository.findByNameContainingIgnoreCase(name).stream().map(this::entityToDto).toList();
	}

	// ─────────────────────────────────────
	// CREATE
	// ─────────────────────────────────────

	@Transactional
	public void create(PatientCreateDtoAdmin dto) {
		User user = userRepository.findById(dto.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException("Utilizador não encontrado!"));

		// Verifica se utilizador já tem paciente
		if (repository.existsByUser(user)) {
			throw new RuntimeException("Este utilizador já tem um paciente " + "associado! Escolha outro utilizador.");
		}

		// Verifica data de nascimento
		if (dto.getDateOfBirth() != null && dto.getDateOfBirth().isAfter(LocalDate.now())) {
			throw new RuntimeException("A data de nascimento não pode " + "ser no futuro!");
		}

		Patient patient = new Patient(generatePatientNumber(), dto.getName(), dto.getDateOfBirth(), dto.getGender(),
				dto.getPhone(), dto.getAddress() != null ? dto.getAddress() : "", user);
		repository.save(patient);
	}

	// ─────────────────────────────────────
	// UPDATE
	// ─────────────────────────────────────

	@Transactional
	public void update(Long id, PatientUpdateDtoAdmin dto) {
		Patient patient = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado: " + id));

		// Verifica data de nascimento
		if (dto.getDateOfBirth() != null && dto.getDateOfBirth().isAfter(LocalDate.now())) {
			throw new RuntimeException("A data de nascimento não pode " + "ser no futuro!");
		}

		patient.setName(dto.getName());
		patient.setDateOfBirth(dto.getDateOfBirth());
		patient.setGender(dto.getGender());
		patient.setPhone(dto.getPhone());
		patient.setAddress(dto.getAddress());
		repository.save(patient);
	}

	// ─────────────────────────────────────
	// DELETE
	// ─────────────────────────────────────

	@Transactional
	public void delete(Long id) {
		if (!repository.existsById(id)) {
			throw new ResourceNotFoundException("Paciente não encontrado: " + id);
		}
		repository.deleteById(id);
	}

	// ─────────────────────────────────────
	// HELPER
	// ─────────────────────────────────────

	private String generatePatientNumber() {
		int year = Year.now().getValue();
		long count = repository.count() + 1;
		return String.format("PAC-%d-%03d", year, count);
	}

	private PatientResponseDto entityToDto(Patient entity) {
		PatientResponseDto dto = new PatientResponseDto();
		dto.setId(entity.getId());
		dto.setName(entity.getName());
		dto.setPatientNumber(entity.getPatientNumber());
		dto.setDateOfBirth(entity.getDateOfBirth());
		dto.setGender(entity.getGender());
		dto.setPhone(entity.getPhone());
		dto.setAddress(entity.getAddress());
		dto.setUserEmail(entity.getUser().getEmail());
		return dto;
	}
}