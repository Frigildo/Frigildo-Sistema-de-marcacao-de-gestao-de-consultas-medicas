package com.hospital.sistema.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.sistema.dtos.AdministratorCreateDtoAdmin;
import com.hospital.sistema.dtos.AdministratorResponseDtoAdmin;
import com.hospital.sistema.dtos.AdministratorUpdateDtoAdmin;
import com.hospital.sistema.entities.Administrator;
import com.hospital.sistema.entities.User;
import com.hospital.sistema.repository.AdministratorRepository;
import com.hospital.sistema.repository.UserRepository;
import com.hospital.sistema.service.exceptions.ResourceNotFoundException;
import com.hospital.sistema.utils.NumberGenerator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdministratorService {

	private final AdministratorRepository repository;
	private final UserRepository userRepository;

	@Transactional(readOnly = true)
	public List<AdministratorResponseDtoAdmin> findAll() {
		return repository.findAll().stream().map(this::entityToDto).toList();
	}

	@Transactional(readOnly = true)
	public AdministratorResponseDtoAdmin findById(Long id) {
		return entityToDto(repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Administrador não encontrado: " + id)));
	}

	@Transactional(readOnly = true)
	public List<AdministratorResponseDtoAdmin> findByName(String name) {
		return repository.findByNameContainingIgnoreCase(name).stream().map(this::entityToDto).toList();
	}

	@Transactional
	public void create(AdministratorCreateDtoAdmin dto) {
		User user = userRepository.findById(dto.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException("Utilizador não encontrado!"));

		if (repository.findByUser(user).isPresent()) {
			throw new RuntimeException("Este utilizador já tem um administrador ligado!");
		}

		Administrator entity = dtoToEntity(dto, user);
		entity.setEmployeeNumber(NumberGenerator.generate("ADM", repository.count()));
		repository.save(entity);
	}

	@Transactional
	public void update(Long id, AdministratorUpdateDtoAdmin dto) {
		Administrator administrator = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Administrador não encontrado: " + id));

		administrator.setName(dto.getName());

		repository.save(administrator);
	}

	@Transactional
	public void delete(Long id) {
		if (!repository.existsById(id)) {
			throw new ResourceNotFoundException("Administrador não encontrado: " + id);
		}
		repository.deleteById(id);
	}

	// Helpers

	private AdministratorResponseDtoAdmin entityToDto(Administrator entity) {
		AdministratorResponseDtoAdmin dto = new AdministratorResponseDtoAdmin();
		dto.setId(entity.getId());
		dto.setName(entity.getName());
		dto.setEmployeeNumber(entity.getEmployeeNumber());
		dto.setUserEmail(entity.getUser().getEmail());
		return dto;
	}

	private Administrator dtoToEntity(AdministratorCreateDtoAdmin dto, User user) {
		return new Administrator(dto.getName(), user);
	}

}