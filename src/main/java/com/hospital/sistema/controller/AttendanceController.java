package com.hospital.sistema.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hospital.sistema.dtos.AttendanceCreateDto;
import com.hospital.sistema.dtos.AttendanceResponseDto;
import com.hospital.sistema.dtos.AttendanceUpdateDto;
import com.hospital.sistema.dtos.ConsultationResponseDto;
import com.hospital.sistema.dtos.ExamCreateDto;
import com.hospital.sistema.dtos.ExamResponseDto;
import com.hospital.sistema.dtos.ExamUpdateDto;
import com.hospital.sistema.entities.Doctor;
import com.hospital.sistema.entities.User;
import com.hospital.sistema.enums.ExamType;
import com.hospital.sistema.enums.Profile;
import com.hospital.sistema.repository.DoctorRepository;
import com.hospital.sistema.repository.UserRepository;
import com.hospital.sistema.service.AttendanceService;
import com.hospital.sistema.service.ConsultationService;
import com.hospital.sistema.service.ExamService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/attendances")
@RequiredArgsConstructor
public class AttendanceController {

	private final AttendanceService service;
	private final ConsultationService consultationService;
	private final ExamService examService;
	private final UserRepository userRepository;
	private final DoctorRepository doctorRepository;

	// ─────────────────────────────────────
	// LISTA
	// ─────────────────────────────────────

	@GetMapping
	public String list(@RequestParam(required = false) String search, Model model, Principal principal) {

		User user = userRepository.findByEmail(principal.getName()).orElseThrow();

		List<AttendanceResponseDto> attendances;

		if (user.getProfile() == Profile.MEDICO) {
			Doctor doctor = doctorRepository.findByUser(user).orElseThrow();
			attendances = service.findByDoctor(doctor.getId());
		} else {
			attendances = service.findAll();
		}

		if (search != null && !search.isBlank()) {
			attendances = attendances.stream()
					.filter(a -> a.getPatientName().toLowerCase().contains(search.toLowerCase())).toList();
			model.addAttribute("search", search);
		}

		model.addAttribute("attendances", attendances);
		model.addAttribute("activeMenu", "attendances");
		model.addAttribute("title", "Atendimentos");
		model.addAttribute("pageTitle", "Lista de Atendimentos");
		return "attendances/list";
	}

	// ─────────────────────────────────────
	// CRIAR
	// ─────────────────────────────────────

	@GetMapping("/new")
	public String newForm(@RequestParam(required = false) Long consultationId, Model model) {

		AttendanceCreateDto dto = new AttendanceCreateDto();

		if (consultationId != null) {
			ConsultationResponseDto consultation = consultationService.findById(consultationId);
			dto.setConsultationId(consultationId);
			dto.setAttendanceDate(consultation.getDate());
			model.addAttribute("selectedConsultation", consultation);
		}

		model.addAttribute("attendanceDTO", dto);
		model.addAttribute("examTypes", ExamType.values());
		model.addAttribute("activeMenu", "attendances");
		model.addAttribute("title", "Novo Atendimento");
		model.addAttribute("pageTitle", "Novo Atendimento");
		return "attendances/form";
	}

	@GetMapping("/select-consultation")
	public String selectConsultation(@RequestParam(required = false) String search, Model model, Principal principal) {

		User user = userRepository.findByEmail(principal.getName()).orElseThrow();

		List<ConsultationResponseDto> consultations;
		if (user.getProfile() == Profile.MEDICO) {
			Doctor doctor = doctorRepository.findByUser(user).orElseThrow();
			consultations = consultationService.findAvailableForAttendanceByDoctor(doctor.getId());
		} else {
			consultations = consultationService.findAvailableForAttendance();
		}

		if (search != null && !search.isBlank()) {
			consultations = consultations.stream()
					.filter(c -> c.getPatientName().toLowerCase().contains(search.toLowerCase())).toList();
			model.addAttribute("search", search);
		}

		model.addAttribute("consultations", consultations);
		model.addAttribute("activeMenu", "attendances");
		model.addAttribute("title", "Seleccionar Consulta");
		model.addAttribute("pageTitle", "Seleccionar Consulta");
		return "attendances/select-consultation";
	}

	@PostMapping("/save")
	public String save(@ModelAttribute("attendanceDTO") AttendanceCreateDto dto, Model model,
			RedirectAttributes attributes) {
		try {
			service.createAndReturnId(dto);
			attributes.addFlashAttribute("success", "Atendimento criado com sucesso!");
			return "redirect:/attendances";
		} catch (Exception e) {
			model.addAttribute("attendanceDTO", dto);
			if (dto.getConsultationId() != null) {
				try {
					model.addAttribute("selectedConsultation", consultationService.findById(dto.getConsultationId()));
				} catch (Exception ignored) {
				}
			}
			model.addAttribute("examTypes", ExamType.values());
			model.addAttribute("errorMessage", e.getMessage());
			model.addAttribute("activeMenu", "attendances");
			model.addAttribute("title", "Novo Atendimento");
			model.addAttribute("pageTitle", "Novo Atendimento");
			return "attendances/form";
		}
	}

	// ─────────────────────────────────────
	// MÉDICO — inicia atendimento
	// ─────────────────────────────────────

	@GetMapping("/new/from-consultation/{consultationId}")
	public String newFromConsultation(@PathVariable Long consultationId, RedirectAttributes attributes) {
		try {
			AttendanceCreateDto dto = new AttendanceCreateDto();

			ConsultationResponseDto consultation = consultationService.findById(consultationId);
			dto.setConsultationId(consultationId);
			dto.setAttendanceDate(consultation.getDate());

			Long id = service.createAndReturnId(dto);
			attributes.addFlashAttribute("success", "Atendimento iniciado com sucesso!");

			// ← query param em vez de flash
			return "redirect:/attendances/" + id + "/edit?isNew=true";

		} catch (Exception e) {
			attributes.addFlashAttribute("error", e.getMessage());
			return "redirect:/consultations";
		}
	}

	// ─────────────────────────────────────
	// EDITAR
	// ─────────────────────────────────────

	@GetMapping("/{id}/edit")
	public String editForm(@PathVariable Long id,
			// ← @RequestParam em vez de
			// @ModelAttribute para Boolean
			@RequestParam(required = false, defaultValue = "false") boolean isNew, Model model) {

		AttendanceResponseDto attendance = service.findById(id);

		AttendanceUpdateDto dto = new AttendanceUpdateDto();
		dto.setObservations(attendance.getObservations());
		dto.setAttendanceDate(attendance.getAttendanceDate());
		dto.setDiagnosis(attendance.getDiagnosis());
		dto.setMedication(attendance.getMedication());
		dto.setStatus(attendance.getStatus());

		model.addAttribute("exams", examService.findByAttendance(id));
		model.addAttribute("attendanceDTO", dto);
		model.addAttribute("attendanceId", id);
		model.addAttribute("attendance", attendance);
		model.addAttribute("isNew", isNew);
		model.addAttribute("activeMenu", "attendances");
		model.addAttribute("title", isNew ? "Atendimento" : "Editar Atendimento");
		model.addAttribute("pageTitle", isNew ? "Atendimento" : "Editar Atendimento");
		return "attendances/form";
	}

	@PostMapping("/{id}/update")
	public String update(@PathVariable Long id, @ModelAttribute("attendanceDTO") AttendanceUpdateDto dto, Model model,
			RedirectAttributes attributes) {
		try {
			service.update(id, dto);
			attributes.addFlashAttribute("success", "Atendimento actualizado com sucesso!");
			return "redirect:/attendances/" + id + "/edit";
		} catch (Exception e) {
			model.addAttribute("attendanceDTO", dto);
			model.addAttribute("attendanceId", id);
			model.addAttribute("errorMessage", e.getMessage());
			model.addAttribute("activeMenu", "attendances");
			model.addAttribute("title", "Editar Atendimento");
			model.addAttribute("pageTitle", "Editar Atendimento");
			return "attendances/form";
		}
	}

	// ─────────────────────────────────────
	// CONCLUIR
	// ─────────────────────────────────────

	@GetMapping("/{id}/conclude")
	public String conclude(@PathVariable Long id, RedirectAttributes attributes) {
		try {
			service.conclude(id);
			attributes.addFlashAttribute("success", "Atendimento concluído com sucesso!");
		} catch (Exception e) {
			attributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/attendances";
	}

	// ─────────────────────────────────────
	// AGUARDAR EXAMES
	// ─────────────────────────────────────

	@GetMapping("/{id}/await-exams")
	public String awaitExams(@PathVariable Long id, RedirectAttributes attributes) {
		try {
			service.awaitExams(id);
			attributes.addFlashAttribute("success", "Atendimento registado como a aguardar resultados de exames.");
		} catch (Exception e) {
			attributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/attendances";
	}

	// ─────────────────────────────────────
	// APAGAR
	// ─────────────────────────────────────

	@GetMapping("/{id}/delete")
	public String delete(@PathVariable Long id, RedirectAttributes attributes) {
		try {
			service.delete(id);
			attributes.addFlashAttribute("success", "Atendimento apagado com sucesso!");
		} catch (Exception e) {
			attributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/attendances";
	}

	// ─────────────────────────────────────
	// EXAMES
	// ─────────────────────────────────────

	@GetMapping("/{id}/exams/new")
	public String newExamForm(@PathVariable Long id, Model model) {
		AttendanceResponseDto attendance = service.findById(id);

		model.addAttribute("examDTO", new ExamCreateDto());
		model.addAttribute("examTypes", ExamType.values());
		model.addAttribute("attendanceId", id);
		model.addAttribute("attendance", attendance);
		model.addAttribute("activeMenu", "attendances");
		model.addAttribute("title", "Novo Exame");
		model.addAttribute("pageTitle", "Adicionar Exame ao Atendimento");
		return "exams/form";
	}

	@PostMapping("/{id}/exams/save")
	public String saveExam(@PathVariable Long id, @ModelAttribute("examDTO") ExamCreateDto dto,
			@RequestParam(value = "file", required = false) MultipartFile file, // ← ADICIONADO
			Model model, RedirectAttributes attributes) {
		try {
			dto.setAttendanceId(id);
			dto.setFile(file); // ← ADICIONADO
			examService.create(dto);
			attributes.addFlashAttribute("success", "Exame adicionado com sucesso!");
			return "redirect:/attendances/" + id + "/edit";
		} catch (Exception e) {
			model.addAttribute("examDTO", dto);
			model.addAttribute("examTypes", ExamType.values());
			model.addAttribute("attendanceId", id);
			model.addAttribute("attendance", service.findById(id));
			model.addAttribute("errorMessage", e.getMessage());
			model.addAttribute("activeMenu", "attendances");
			model.addAttribute("title", "Novo Exame");
			model.addAttribute("pageTitle", "Adicionar Exame");
			return "exams/form";
		}
	}

	@GetMapping("/{attendanceId}/exams/{examId}/result")
	public String editExamResult(@PathVariable Long attendanceId, @PathVariable Long examId, Model model) {
		ExamResponseDto exam = examService.findById(examId);

		ExamUpdateDto dto = new ExamUpdateDto();
		dto.setType(exam.getType());
		dto.setResult(exam.getResult());
		dto.setDate(exam.getDate());

		model.addAttribute("examDTO", dto);
		model.addAttribute("examId", examId);
		model.addAttribute("attendanceId", attendanceId);
		model.addAttribute("exam", exam);
		model.addAttribute("examTypes", ExamType.values());
		model.addAttribute("activeMenu", "attendances");
		model.addAttribute("title", "Resultado do Exame");
		model.addAttribute("pageTitle", "Anexar Resultado do Exame");
		return "exams/exam-result";
	}

	@PostMapping("/{attendanceId}/exams/{examId}/result")
	public String saveExamResult(@PathVariable Long attendanceId, @PathVariable Long examId,
			@ModelAttribute("examDTO") ExamUpdateDto dto,
			@RequestParam(value = "file", required = false) MultipartFile file, // ← ADICIONADO
			RedirectAttributes attributes) {
		try {
			dto.setFile(file); // ← ADICIONADO
			examService.update(examId, dto);
			attributes.addFlashAttribute("success", "Resultado guardado com sucesso!");
		} catch (Exception e) {
			attributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/attendances/" + attendanceId + "/edit";
	}
}