package com.hospital.sistema.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.sistema.dtos.AttendanceCreateDto;
import com.hospital.sistema.dtos.AttendanceResponseDto;
import com.hospital.sistema.dtos.AttendanceUpdateDto;
import com.hospital.sistema.entities.Attendance;
import com.hospital.sistema.entities.Consultation;
import com.hospital.sistema.entities.Exam;
import com.hospital.sistema.enums.AttendanceStatus;
import com.hospital.sistema.enums.ConsultationStatus;
import com.hospital.sistema.repository.AttendanceRepository;
import com.hospital.sistema.repository.ConsultationRepository;
import com.hospital.sistema.repository.ExamRepository;
import com.hospital.sistema.service.exceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendanceService {

	private final AttendanceRepository repository;
	private final ConsultationRepository consultationRepository;
	private final ExamRepository examRepository;

	@Transactional(readOnly = true)
	public List<AttendanceResponseDto> findAll() {
		return repository.findAll().stream().map(this::entityToDto).toList();
	}

	@Transactional(readOnly = true)
	public AttendanceResponseDto findById(Long id) {
		return entityToDto(repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Atendimento não encontrado: " + id)));
	}

	@Transactional(readOnly = true)
	public Attendance findEntityById(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Atendimento não encontrado: " + id));
	}

	@Transactional(readOnly = true)
	public List<AttendanceResponseDto> findByPatient(Long patientId) {
		return repository.findByPatientId(patientId).stream().map(this::entityToDto).toList();
	}

	@Transactional(readOnly = true)
	public List<AttendanceResponseDto> findByPatientName(String name) {
		return repository.findByConsultationPatientNameContainingIgnoreCase(name).stream().map(this::entityToDto)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<AttendanceResponseDto> findByDoctor(Long doctorId) {
		return repository.findByDoctorId(doctorId).stream().map(this::entityToDto).toList();
	}

	@Transactional
	public void create(AttendanceCreateDto dto) {
		Consultation consultation = consultationRepository.findById(dto.getConsultationId())
				.orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada!"));

		if (repository.existsByConsultation(consultation)) {
			throw new RuntimeException("Esta consulta já tem um atendimento!");
		}

		Attendance attendance = new Attendance(dto.getObservations(), dto.getAttendanceDate(), consultation);
		attendance.setStatus(AttendanceStatus.PENDENTE);
		repository.save(attendance);
	}

	@Transactional
	public Long createAndReturnId(AttendanceCreateDto dto) {
		Consultation consultation = consultationRepository.findById(dto.getConsultationId())
				.orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada!"));

		if (repository.existsByConsultation(consultation)) {
			throw new RuntimeException("Esta consulta já tem um atendimento!");
		}

		Attendance attendance = new Attendance(dto.getObservations(), dto.getAttendanceDate(), consultation);
		attendance.setStatus(AttendanceStatus.PENDENTE);
		attendance.setDiagnosis(dto.getDiagnosis());
		attendance.setMedication(dto.getMedication());

		consultation.setStatus(ConsultationStatus.CONCLUIDA);
		consultationRepository.save(consultation);

		Attendance saved = repository.save(attendance);

		if (dto.isNeedsExam() && dto.getExamType() != null) {
			Exam exam = new Exam(dto.getExamType(), null,
					dto.getExamDate() != null ? dto.getExamDate() : dto.getAttendanceDate(), saved);
			examRepository.save(exam);
		}

		return saved.getId();
	}

	@Transactional
	public void update(Long id, AttendanceUpdateDto dto) {
		Attendance attendance = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Atendimento não encontrado: " + id));

		attendance.setObservations(dto.getObservations());
		attendance.setAttendanceDate(dto.getAttendanceDate());
		attendance.setDiagnosis(dto.getDiagnosis());
		attendance.setMedication(dto.getMedication());
		if (dto.getStatus() != null) {
			attendance.setStatus(dto.getStatus());
		}
		repository.save(attendance);
	}

	@Transactional
	public void conclude(Long id) {
		Attendance attendance = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Atendimento não encontrado: " + id));
		attendance.setStatus(AttendanceStatus.CONCLUIDO);
		repository.save(attendance);
	}

	@Transactional
	public void awaitExams(Long id) {
		Attendance attendance = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Atendimento não encontrado: " + id));
		attendance.setStatus(AttendanceStatus.AGUARDA_EXAMES);
		repository.save(attendance);
	}

	@Transactional
	public void delete(Long id) {
		if (!repository.existsById(id)) {
			throw new ResourceNotFoundException("Atendimento não encontrado: " + id);
		}
		repository.deleteById(id);
	}

	private AttendanceResponseDto entityToDto(Attendance entity) {
		AttendanceResponseDto dto = new AttendanceResponseDto();
		dto.setId(entity.getId());
		dto.setObservations(entity.getObservations());
		dto.setDiagnosis(entity.getDiagnosis());
		dto.setMedication(entity.getMedication());
		dto.setAttendanceDate(entity.getAttendanceDate());
		dto.setStatus(entity.getStatus());
		dto.setConsultationId(entity.getConsultation().getId());
		dto.setPatientName(entity.getConsultation().getPatient().getName());
		dto.setDoctorName(entity.getConsultation().getDoctor().getName());
		dto.setConsultationDate(entity.getConsultation().getDate());
		dto.setConsultationStatus(entity.getConsultation().getStatus());
		return dto;
	}
}