package com.hospital.sistema.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.sistema.dtos.ConsultationCreateDto;
import com.hospital.sistema.dtos.ConsultationResponseDto;
import com.hospital.sistema.dtos.ConsultationUpdateDto;
import com.hospital.sistema.entities.Agenda;
import com.hospital.sistema.entities.Consultation;
import com.hospital.sistema.entities.Doctor;
import com.hospital.sistema.entities.Patient;
import com.hospital.sistema.enums.ConsultationStatus;
import com.hospital.sistema.repository.AgendaRepository;
import com.hospital.sistema.repository.AttendanceRepository;
import com.hospital.sistema.repository.ConsultationRepository;
import com.hospital.sistema.repository.DoctorRepository;
import com.hospital.sistema.repository.PatientRepository;
import com.hospital.sistema.repository.PresenceRepository;
import com.hospital.sistema.service.exceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConsultationService {

	private final ConsultationRepository repository;
	private final PatientRepository patientRepository;
	private final DoctorRepository doctorRepository;
	private final AgendaRepository agendaRepository;
	private final AttendanceRepository attendanceRepository;
	private final PresenceRepository presenceRepository;

	// ─────────────────────────────────────
	// READ
	// ─────────────────────────────────────

	@Transactional(readOnly = true)
	public List<ConsultationResponseDto> findAll() {
		return repository.findAll().stream().map(this::entityToDto).toList();
	}

	@Transactional(readOnly = true)
	public List<ConsultationResponseDto> findAvailableForAttendance() {
		return repository.findByStatus(ConsultationStatus.AGENDADA).stream()
				.filter(c -> !attendanceRepository.existsByConsultation(c)).map(this::entityToDto).toList();
	}

	@Transactional(readOnly = true)
	public ConsultationResponseDto findById(Long id) {
		return entityToDto(repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada: " + id)));
	}

	@Transactional(readOnly = true)
	public List<ConsultationResponseDto> findByPatient(Long patientId) {
		Patient patient = patientRepository.findById(patientId)
				.orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado!"));
		return repository.findByPatient(patient).stream().map(this::entityToDto).toList();
	}

	@Transactional(readOnly = true)
	public List<ConsultationResponseDto> findByDoctor(Long doctorId) {
		Doctor doctor = doctorRepository.findById(doctorId)
				.orElseThrow(() -> new ResourceNotFoundException("Médico não encontrado!"));
		return repository.findByDoctor(doctor).stream().map(this::entityToDto).toList();
	}

	@Transactional(readOnly = true)
	public List<ConsultationResponseDto> findByStatus(ConsultationStatus status) {
		return repository.findByStatus(status).stream().map(this::entityToDto).toList();
	}

	@Transactional(readOnly = true)
	public List<ConsultationResponseDto> findByDate(LocalDate date) {
		return repository.findByDate(date).stream().map(this::entityToDto).toList();
	}

	@Transactional(readOnly = true)
	public List<ConsultationResponseDto> findByPatientName(String name) {
		return repository.findAll().stream()
				.filter(c -> c.getPatient().getName().toLowerCase().contains(name.toLowerCase())).map(this::entityToDto)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<ConsultationResponseDto> findAvailableForAttendanceByDoctor(Long doctorId) {
		Doctor doctor = doctorRepository.findById(doctorId)
				.orElseThrow(() -> new ResourceNotFoundException("Médico não encontrado!"));

		return repository.findByDoctor(doctor).stream().filter(c -> c.getStatus() == ConsultationStatus.AGENDADA)
				.filter(c -> !attendanceRepository.existsByConsultation(c)).map(this::entityToDto).toList();
	}

	@Transactional(readOnly = true)
	public List<ConsultationResponseDto> findAgendadasSemPresenca() {
		return repository.findByStatus(ConsultationStatus.AGENDADA).stream()
				.filter(c -> !presenceRepository.existsByConsultation(c)).map(this::entityToDto).toList();
	}

	@Transactional(readOnly = true)
	public List<ConsultationResponseDto> findScheduledWithoutPresenceByDoctor(Long doctorId, String search) {
		Doctor doctor = doctorRepository.findById(doctorId)
				.orElseThrow(() -> new ResourceNotFoundException("Médico não encontrado!"));

		return repository.findByDoctor(doctor).stream().filter(c -> c.getStatus() == ConsultationStatus.AGENDADA)
				.filter(c -> !presenceRepository.existsByConsultation(c))
				.filter(c -> search == null || search.isBlank()
						|| c.getPatient().getName().toLowerCase().contains(search.toLowerCase()))
				.map(this::entityToDto).toList();
	}

	// ─────────────────────────────────────
	// CREATE
	// ─────────────────────────────────────

	@Transactional
	public void create(ConsultationCreateDto dto) {
		Patient patient = patientRepository.findById(dto.getPatientId())
				.orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado!"));

		Doctor doctor = doctorRepository.findById(dto.getDoctorId())
				.orElseThrow(() -> new ResourceNotFoundException("Médico não encontrado!"));

		System.out.println("agendaId recebido: " + dto.getAgendaId());

		if (dto.getAgendaId() != null) {
			Agenda agenda = agendaRepository.findById(dto.getAgendaId())
					.orElseThrow(() -> new ResourceNotFoundException("Horário não encontrado!"));

			System.out.println("agenda.isAvailable(): " + agenda.isAvailable());

			if (!agenda.isAvailable()) {
				throw new RuntimeException("Este horário já foi marcado! Escolha outro horário disponível.");
			}

			agenda.setAvailable(false);
			agendaRepository.save(agenda);

			Consultation consultation = new Consultation(dto.getDate(), dto.getTime(), dto.getStatus(), patient,
					doctor);
			consultation.setAgenda(agenda);
			repository.save(consultation);
		} else {
			Consultation consultation = new Consultation(dto.getDate(), dto.getTime(), dto.getStatus(), patient,
					doctor);
			repository.save(consultation);
		}
	}

	// ─────────────────────────────────────
	// UPDATE
	// ─────────────────────────────────────

	@Transactional
	public void update(Long id, ConsultationUpdateDto dto) {
		Consultation consultation = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada: " + id));

		consultation.setDate(dto.getDate());
		consultation.setTime(dto.getTime());
		consultation.setStatus(dto.getStatus());

		if (dto.getStatus() == ConsultationStatus.CANCELADA && consultation.getAgenda() != null) {
			consultation.getAgenda().setAvailable(true);
		}

		repository.save(consultation);
	}

	// ─────────────────────────────────────
	// DELETE
	// ─────────────────────────────────────

	@Transactional
	public void delete(Long id) {
		Consultation entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrado!" + id));
		entity.getAgenda().setAvailable(true);
		repository.deleteById(id);
	}

	// ─────────────────────────────────────
	// CANCEL
	// ─────────────────────────────────────

	@Transactional
	public void cancel(Long id) {
		Consultation consultation = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada: " + id));

		if (consultation.getStatus() == ConsultationStatus.CONCLUIDA) {
			throw new RuntimeException("Não é possível cancelar uma consulta já concluída!");
		}

		consultation.setStatus(ConsultationStatus.CANCELADA);

		if (consultation.getAgenda() != null) {
			Agenda agenda = consultation.getAgenda();
			agenda.setAvailable(true);
			agendaRepository.save(agenda);
		}

		repository.save(consultation);
	}

	// ─────────────────────────────────────
	// HELPER
	// ─────────────────────────────────────

	private ConsultationResponseDto entityToDto(Consultation entity) {
		ConsultationResponseDto dto = new ConsultationResponseDto();
		dto.setId(entity.getId());
		dto.setDate(entity.getDate());
		dto.setTime(entity.getTime());
		dto.setStatus(entity.getStatus());
		dto.setPatientId(entity.getPatient().getId());
		dto.setPatientName(entity.getPatient().getName());
		dto.setDoctorId(entity.getDoctor().getId());
		dto.setDoctorName(entity.getDoctor().getName());
		return dto;
	}
}