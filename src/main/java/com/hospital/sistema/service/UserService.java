package com.hospital.sistema.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.sistema.dtos.UserCreateDtoAdmin;
import com.hospital.sistema.dtos.UserCreateDtoPatient;
import com.hospital.sistema.dtos.UserResponseDto;
import com.hospital.sistema.dtos.UserUpdateDtoAdmin;
import com.hospital.sistema.entities.User;
import com.hospital.sistema.enums.Profile;
import com.hospital.sistema.repository.UserRepository;
import com.hospital.sistema.service.exceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	@Transactional(readOnly = true)
	public List<UserResponseDto> findAll() {
		return userRepository.findAll().stream().map(user -> toResponseDto(user)).toList();
	}

	@Transactional(readOnly = true)
	public UserResponseDto findById(Long id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Utilizador não encontrado!"));
		return toResponseDto(user);
	}

	@Transactional(readOnly = true)
	public List<UserResponseDto> findByEmail(String email) {
		return userRepository.findByEmailContainingIgnoreCase(email).stream().map(this::toResponseDto).toList();
	}

	// Admin create
	@Transactional
	public void save(UserCreateDtoAdmin dto) {
		if (userRepository.existsByEmail(dto.getEmail())) {
			throw new RuntimeException("Email já está registado!");
		}

		User user = new User(dto.getEmail(), passwordEncoder.encode(dto.getPassword()), dto.getProfile());

		userRepository.save(user);
	}

	// Patient create
	@Transactional
	public void register(UserCreateDtoPatient dto) {
		if (!dto.getPassword().equals(dto.getConfirmPassword())) {
			throw new RuntimeException("Passwords não coincidem!");
		}
		if (userRepository.existsByEmail(dto.getEmail())) {
			throw new RuntimeException("Email já está registado!");
		}
		User user = new User(dto.getEmail(), passwordEncoder.encode(dto.getPassword()), Profile.PACIENTE);

		userRepository.save(user);
	}

	@Transactional
	public void update(Long id, UserUpdateDtoAdmin dto) {
		User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Utilizador não encontrado!"));

		if (!user.getEmail().equals(dto.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
			throw new RuntimeException("Email já está registado!");
		}

		user.setEmail(dto.getEmail());
		user.setProfile(dto.getProfile());

		if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
			user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
		}

		userRepository.save(user);
	}

	@Transactional
	public void delete(Long id) {
		if (!userRepository.existsById(id)) {
			throw new ResourceNotFoundException("Utilizador não encontrado: " + id);
		}
		userRepository.deleteById(id);
	}

	private UserResponseDto toResponseDto(User user) {
		UserResponseDto dto = new UserResponseDto();

		dto.setId(user.getId());
		dto.setEmail(user.getEmail());
		dto.setProfile(user.getProfile());

		return dto;
	}
}
