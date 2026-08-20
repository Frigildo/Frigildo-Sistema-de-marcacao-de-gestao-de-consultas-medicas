package com.hospital.sistema.service;

import java.time.Year;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.sistema.dtos.DoctorCreateDtoAdmin;
import com.hospital.sistema.dtos.DoctorResponseDtoAdmin;
import com.hospital.sistema.dtos.DoctorUpdateDtoAdmin;
import com.hospital.sistema.entities.Doctor;
import com.hospital.sistema.repository.DoctorRepository;
import com.hospital.sistema.repository.UserRepository;
import com.hospital.sistema.service.exceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DoctorService {

	private final DoctorRepository repository;
	private final UserRepository userRepository;

	@Transactional(readOnly = true)
	public List<DoctorResponseDtoAdmin> findAll() {
		return repository.findAll().stream().map(this::entityToDto).toList();
	}

	@Transactional(readOnly = true)
	public DoctorResponseDtoAdmin findById(Long id) {
		return entityToDto(repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Médico não encontrado: " + id)));
	}

	@Transactional(readOnly = true)
	public Doctor findEntityById(Long id) {
		return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Médico não encontrado: " + id));
	}

	@Transactional(readOnly = true)
	public List<DoctorResponseDtoAdmin> findByName(String name) {
		return repository.findByNameContainingIgnoreCase(name).stream().map(this::entityToDto).toList();
	}

	@Transactional
	public void create(DoctorCreateDtoAdmin dto) {
		var user = userRepository.findById(dto.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException("Utilizador não encontrado!"));

		if (repository.existsByUser(user)) {
			throw new RuntimeException("Este utilizador já tem um médico!");
		}

		Doctor doctor = new Doctor(dto.getName(), dto.getCellPhone(), user);
		doctor.setEmployeeNumber(generateEmployeeNumber());
		repository.save(doctor);
	}

	@Transactional
	public void update(Long id, DoctorUpdateDtoAdmin dto) {
		Doctor doctor = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Médico não encontrado: " + id));
		doctor.setName(dto.getName());
		doctor.setPhone(dto.getCellPhone());
		repository.save(doctor);
	}

	@Transactional
	public void delete(Long id) {
		if (!repository.existsById(id)) {
			throw new ResourceNotFoundException("Médico não encontrado: " + id);
		}
		repository.deleteById(id);
	}

	private String generateEmployeeNumber() {
		int year = Year.now().getValue();
		long count = repository.count() + 1;
		return String.format("MED-%d-%03d", year, count);
	}

	private DoctorResponseDtoAdmin entityToDto(Doctor entity) {
		DoctorResponseDtoAdmin dto = new DoctorResponseDtoAdmin();
		dto.setId(entity.getId());
		dto.setName(entity.getName());
		dto.setEmployeeNumber(entity.getEmployeeNumber());
		dto.setCellPhone(entity.getPhone());
		dto.setUserId(entity.getUser().getId());
		dto.setUserEmail(entity.getUser().getEmail());
		// ← NOVO — specialtyName
		dto.setSpecialtyName(entity.getSpecialty() != null ? entity.getSpecialty().getName() : null);
		return dto;
	}
}