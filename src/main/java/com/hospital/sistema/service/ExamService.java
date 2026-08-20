package com.hospital.sistema.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.sistema.dtos.ExamCreateDto;
import com.hospital.sistema.dtos.ExamResponseDto;
import com.hospital.sistema.dtos.ExamUpdateDto;
import com.hospital.sistema.entities.Attendance;
import com.hospital.sistema.entities.Exam;
import com.hospital.sistema.enums.ExamType;
import com.hospital.sistema.repository.AttendanceRepository;
import com.hospital.sistema.repository.ExamRepository;
import com.hospital.sistema.service.exceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExamService {

	private final ExamRepository repository;
	private final AttendanceRepository attendanceRepository;

	// ─────────────────────────────────────
	// READ
	// ─────────────────────────────────────

	@Transactional(readOnly = true)
	public List<ExamResponseDto> findAll() {
		return repository.findAll().stream().map(this::entityToDto).toList();
	}

	@Transactional(readOnly = true)
	public ExamResponseDto findById(Long id) {
		return entityToDto(repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Exame não encontrado: " + id)));
	}

	@Transactional(readOnly = true)
	public List<ExamResponseDto> findByAttendance(Long attendanceId) {
		Attendance attendance = attendanceRepository.findById(attendanceId)
				.orElseThrow(() -> new ResourceNotFoundException("Atendimento não encontrado: " + attendanceId));
		return repository.findByAttendance(attendance).stream().map(this::entityToDto).toList();
	}

	@Transactional(readOnly = true)
	public List<ExamResponseDto> findByType(ExamType type) {
		return repository.findByType(type).stream().map(this::entityToDto).toList();
	}

	@Transactional(readOnly = true)
	public List<ExamResponseDto> findByPatient(Long patientId) {
		return repository.findByPatientId(patientId).stream().map(this::entityToDto).toList();
	}

	// ─────────────────────────────────────
	// CREATE
	// ─────────────────────────────────────

	@Transactional
	public void create(ExamCreateDto dto) {
		Attendance attendance = attendanceRepository.findById(dto.getAttendanceId())
				.orElseThrow(() -> new ResourceNotFoundException("Atendimento não encontrado!"));

		Exam exam = new Exam(dto.getType(), dto.getResult(), dto.getDate(), attendance);

		// Guarda ficheiro se foi enviado
		if (dto.getFile() != null && !dto.getFile().isEmpty()) {
			try {
				String uploadDir = "uploads/exams/";
				Path uploadPath = Paths.get(uploadDir);

				if (!Files.exists(uploadPath)) {
					Files.createDirectories(uploadPath);
				}

				String fileName = System.currentTimeMillis() + "_" + dto.getFile().getOriginalFilename();

				Path filePath = uploadPath.resolve(fileName);

				Files.copy(dto.getFile().getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

				exam.setFileName(dto.getFile().getOriginalFilename());
				exam.setFilePath(uploadDir + fileName);
				exam.setFileType(dto.getFile().getContentType());

			} catch (IOException e) {
				throw new RuntimeException("Erro ao guardar ficheiro: " + e.getMessage());
			}
		}

		repository.save(exam);
	}

	// ─────────────────────────────────────
	// UPDATE
	// ─────────────────────────────────────

	@Transactional
	public void update(Long id, ExamUpdateDto dto) {
		Exam exam = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Exame não encontrado: " + id));

		exam.setType(dto.getType());
		exam.setResult(dto.getResult());
		exam.setDate(dto.getDate());

		// Guarda ficheiro se foi enviado
		if (dto.getFile() != null && !dto.getFile().isEmpty()) {
			try {
				String uploadDir = "uploads/exams/";
				Path uploadPath = Paths.get(uploadDir);

				if (!Files.exists(uploadPath)) {
					Files.createDirectories(uploadPath);
				}

				String fileName = System.currentTimeMillis() + "_" + dto.getFile().getOriginalFilename();

				Path filePath = uploadPath.resolve(fileName);

				Files.copy(dto.getFile().getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

				exam.setFileName(dto.getFile().getOriginalFilename());
				exam.setFilePath(uploadDir + fileName);
				exam.setFileType(dto.getFile().getContentType());

			} catch (IOException e) {
				throw new RuntimeException("Erro ao guardar ficheiro: " + e.getMessage());
			}
		}

		repository.save(exam);
	}

	// ─────────────────────────────────────
	// DELETE
	// ─────────────────────────────────────

	@Transactional
	public void delete(Long id) {
		if (!repository.existsById(id)) {
			throw new ResourceNotFoundException("Exame não encontrado: " + id);
		}
		repository.deleteById(id);
	}

	// ─────────────────────────────────────
	// HELPER
	// ─────────────────────────────────────

	private ExamResponseDto entityToDto(Exam entity) {
		ExamResponseDto dto = new ExamResponseDto();
		dto.setId(entity.getId());
		dto.setType(entity.getType());
		dto.setResult(entity.getResult());
		dto.setDate(entity.getDate());
		dto.setAttendanceId(entity.getAttendance().getId());
		dto.setPatientName(entity.getAttendance().getConsultation().getPatient().getName());
		dto.setDoctorName(entity.getAttendance().getConsultation().getDoctor().getName());
		dto.setFileName(entity.getFileName());
		dto.setFilePath(entity.getFilePath());
		dto.setFileType(entity.getFileType());
		return dto;
	}
}