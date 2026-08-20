package com.hospital.sistema.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.sistema.dtos.CounselingCreateDto;
import com.hospital.sistema.dtos.CounselingResponseDto;
import com.hospital.sistema.dtos.CounselingUpdateDto;
import com.hospital.sistema.entities.Attendance;
import com.hospital.sistema.entities.Counseling;
import com.hospital.sistema.repository.AttendanceRepository;
import com.hospital.sistema.repository.CounselingRepository;
import com.hospital.sistema.service.exceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CounselingService {

	private final CounselingRepository repository;
	private final AttendanceRepository attendanceRepository;

	@Transactional(readOnly = true)
	public List<CounselingResponseDto> findAll() {
		return repository.findAll().stream().map(this::entityToDto).toList();
	}

	@Transactional(readOnly = true)
	public CounselingResponseDto findById(Long id) {
		return entityToDto(repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Aconselhamento não encontrado: " + id)));
	}

	@Transactional(readOnly = true)
	public Counseling findEntityById(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Aconselhamento não encontrado: " + id));
	}

	@Transactional(readOnly = true)
	public List<CounselingResponseDto> findByPatient(Long patientId) {
		return repository.findByPatientId(patientId).stream().map(this::entityToDto).toList();
	}

	@Transactional
	public void create(CounselingCreateDto dto) {
		Attendance attendance = attendanceRepository.findById(dto.getAttendanceId())
				.orElseThrow(() -> new ResourceNotFoundException("Atendimento não encontrado!"));

		// Verifica se atendimento já tem aconselhamento
		if (repository.existsByAttendance(attendance)) {
			throw new RuntimeException("Este atendimento já tem um " + "aconselhamento registado!");
		}

		Counseling counseling = new Counseling(dto.getDescription(), dto.getDate(), attendance);

		repository.save(counseling);
	}

	@Transactional
	public void update(Long id, CounselingUpdateDto dto) {
		Counseling counseling = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Aconselhamento não encontrado: " + id));

		counseling.setDescription(dto.getDescription());
		counseling.setDate(dto.getDate());
		// ← attendance não muda!

		repository.save(counseling);
	}

	@Transactional
	public void delete(Long id) {
		if (!repository.existsById(id)) {
			throw new ResourceNotFoundException("Aconselhamento não encontrado: " + id);
		}
		repository.deleteById(id);
	}

	private CounselingResponseDto entityToDto(Counseling entity) {
		CounselingResponseDto dto = new CounselingResponseDto();
		dto.setId(entity.getId());
		dto.setDescription(entity.getDescription());
		dto.setDate(entity.getDate());
		dto.setAttendanceId(entity.getAttendance().getId());
		dto.setPatientName(entity.getAttendance().getConsultation().getPatient().getName());
		dto.setDoctorName(entity.getAttendance().getConsultation().getDoctor().getName());
		return dto;
	}
}