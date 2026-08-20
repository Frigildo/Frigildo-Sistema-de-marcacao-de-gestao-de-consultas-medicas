package com.hospital.sistema.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hospital.sistema.dtos.ConsultationCreateDto;
import com.hospital.sistema.dtos.ConsultationResponseDto;
import com.hospital.sistema.dtos.PatientCreateDtoAdmin;
import com.hospital.sistema.dtos.PatientResponseDto;
import com.hospital.sistema.dtos.PatientUpdateDtoAdmin;
import com.hospital.sistema.entities.Patient;
import com.hospital.sistema.entities.User;
import com.hospital.sistema.enums.ConsultationStatus;
import com.hospital.sistema.enums.Gender;
import com.hospital.sistema.repository.PatientRepository;
import com.hospital.sistema.repository.UserRepository;
import com.hospital.sistema.service.AttendanceService;
import com.hospital.sistema.service.ConsultationService;
import com.hospital.sistema.service.CounselingService;
import com.hospital.sistema.service.DoctorService;
import com.hospital.sistema.service.ExamService;
import com.hospital.sistema.service.PatientService;
import com.hospital.sistema.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {

	private final PatientService service;
	private final UserService userService;
	private final PatientRepository patientRepository;
	private final UserRepository userRepository;
	private final ConsultationService consultationService;
	private final AttendanceService attendanceService;
	private final ExamService examService;
	private final CounselingService counselingService;
	private final DoctorService doctorService;

	// ─────────────────────────────────────
	// Helper
	// ─────────────────────────────────────
	private Patient getLoggedPatient(Principal principal) {
		User user = userRepository.findByEmail(principal.getName())
				.orElseThrow(() -> new RuntimeException("Utilizador não encontrado!"));
		return patientRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Paciente não encontrado!"));
	}

	// ─────────────────────────────────────
	// ADMIN — CRUD
	// ─────────────────────────────────────

	@GetMapping
	public String list(@RequestParam(required = false) String search, Model model) {
		if (search != null && !search.isBlank()) {
			model.addAttribute("patients", service.findByName(search));
			model.addAttribute("search", search);
		} else {
			model.addAttribute("patients", service.findAll());
		}
		model.addAttribute("activeMenu", "patients");
		model.addAttribute("title", "Pacientes");
		model.addAttribute("pageTitle", "Lista de Pacientes");
		return "patients/list";
	}

	@GetMapping("/new")
	public String newForm(@RequestParam(required = false) Long userId, Model model) {

		PatientCreateDtoAdmin dto = new PatientCreateDtoAdmin();

		if (userId != null) {
			dto.setUserId(userId);
			model.addAttribute("selectedUser", userService.findById(userId));
		}

		model.addAttribute("patientDTO", dto);
		model.addAttribute("genders", Gender.values());
		model.addAttribute("activeMenu", "patients");
		model.addAttribute("title", "Novo Paciente");
		model.addAttribute("pageTitle", "Novo Paciente");
		return "patients/form";
	}

	@GetMapping("/select-user")
	public String selectUser(@RequestParam(required = false) String search, Model model) {

		if (search != null && !search.isBlank()) {
			model.addAttribute("users", userService.findByEmail(search));
			model.addAttribute("search", search);
		} else {
			model.addAttribute("users", userService.findAll());
		}

		model.addAttribute("activeMenu", "patients");
		model.addAttribute("title", "Seleccionar Utilizador");
		model.addAttribute("pageTitle", "Seleccionar Utilizador");
		return "patients/select-user";
	}

	@PostMapping("/save")
	public String save(@ModelAttribute("patientDTO") PatientCreateDtoAdmin dto, Model model,
			RedirectAttributes attributes) {
		try {
			service.create(dto);
			attributes.addFlashAttribute("success", "Paciente criado com sucesso!");
			return "redirect:/patients";
		} catch (Exception e) {
			model.addAttribute("patientDTO", dto);
			if (dto.getUserId() != null) {
				model.addAttribute("selectedUser", userService.findById(dto.getUserId()));
			}
			model.addAttribute("genders", Gender.values());
			model.addAttribute("errorMessage", e.getMessage());
			model.addAttribute("activeMenu", "patients");
			model.addAttribute("title", "Novo Paciente");
			model.addAttribute("pageTitle", "Novo Paciente");
			return "patients/form";
		}
	}

	@GetMapping("/{id}/edit")
	public String editForm(@PathVariable Long id, Model model) {
		PatientResponseDto patient = service.findById(id);

		PatientUpdateDtoAdmin dto = new PatientUpdateDtoAdmin();
		dto.setName(patient.getName());
		dto.setDateOfBirth(patient.getDateOfBirth());
		dto.setGender(patient.getGender());
		dto.setPhone(patient.getPhone());
		dto.setAddress(patient.getAddress());

		model.addAttribute("patientDTO", dto);
		model.addAttribute("patientId", id);
		model.addAttribute("patient", patient);
		model.addAttribute("genders", Gender.values());
		model.addAttribute("activeMenu", "patients");
		model.addAttribute("title", "Editar Paciente");
		model.addAttribute("pageTitle", "Editar Paciente");
		return "patients/form";
	}

	@PostMapping("/{id}/update")
	public String update(@PathVariable Long id, @ModelAttribute("patientDTO") PatientUpdateDtoAdmin dto, Model model,
			RedirectAttributes attributes) {
		try {
			service.update(id, dto);
			attributes.addFlashAttribute("success", "Paciente actualizado com sucesso!");
			return "redirect:/patients";
		} catch (Exception e) {
			model.addAttribute("patientDTO", dto);
			model.addAttribute("patientId", id);
			model.addAttribute("genders", Gender.values());
			model.addAttribute("errorMessage", e.getMessage());
			model.addAttribute("activeMenu", "patients");
			model.addAttribute("title", "Editar Paciente");
			model.addAttribute("pageTitle", "Editar Paciente");
			return "patients/form";
		}
	}

	@GetMapping("/{id}/delete")
	public String delete(@PathVariable Long id, RedirectAttributes attributes) {
		try {
			service.delete(id);
			attributes.addFlashAttribute("success", "Paciente apagado com sucesso!");
		} catch (Exception e) {
			attributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/patients";
	}

	// ─────────────────────────────────────
	// PACIENTE — VER PERFIL
	// ─────────────────────────────────────

	@GetMapping("/profile")
	public String profile(Principal principal, Model model) {
		Patient patient = getLoggedPatient(principal);
		model.addAttribute("patient", patient);
		model.addAttribute("activeMenu", "profile");
		model.addAttribute("title", "Meu Perfil");
		model.addAttribute("pageTitle", "Meu Perfil");
		return "patients/profile";
	}

	@GetMapping("/profile/edit")
	public String editProfile(Principal principal, Model model) {
		Patient patient = getLoggedPatient(principal);

		PatientUpdateDtoAdmin dto = new PatientUpdateDtoAdmin();
		dto.setName(patient.getName());
		dto.setDateOfBirth(patient.getDateOfBirth());
		dto.setGender(patient.getGender());
		dto.setPhone(patient.getPhone());
		dto.setAddress(patient.getAddress());

		model.addAttribute("patientDTO", dto);
		model.addAttribute("genders", Gender.values());
		model.addAttribute("activeMenu", "profile");
		model.addAttribute("title", "Editar Perfil");
		model.addAttribute("pageTitle", "Editar Perfil");
		return "patients/edit-profile";
	}

	@PostMapping("/profile/update")
	public String updateProfile(Principal principal, @ModelAttribute("patientDTO") PatientUpdateDtoAdmin dto,
			Model model, RedirectAttributes attributes) {
		try {
			Patient patient = getLoggedPatient(principal);
			service.update(patient.getId(), dto);
			attributes.addFlashAttribute("success", "Perfil actualizado com sucesso!");
			return "redirect:/patients/profile";
		} catch (Exception e) {
			model.addAttribute("patientDTO", dto);
			model.addAttribute("errorMessage", e.getMessage());
			model.addAttribute("genders", Gender.values());
			model.addAttribute("activeMenu", "profile");
			model.addAttribute("title", "Editar Perfil");
			model.addAttribute("pageTitle", "Editar Perfil");
			return "patients/edit-profile";
		}
	}

	// ─────────────────────────────────────
	// PACIENTE — HISTÓRICO
	// ─────────────────────────────────────

	@GetMapping("/history")
	public String history(Principal principal, Model model) {
		Patient patient = getLoggedPatient(principal);
		model.addAttribute("consultations", consultationService.findByPatient(patient.getId()));
		model.addAttribute("attendances", attendanceService.findByPatient(patient.getId()));
		model.addAttribute("exams", examService.findByPatient(patient.getId()));
		model.addAttribute("counselings", counselingService.findByPatient(patient.getId()));
		model.addAttribute("patient", patient);
		model.addAttribute("activeMenu", "history");
		model.addAttribute("title", "Histórico Clínico");
		model.addAttribute("pageTitle", "Meu Histórico Clínico");
		return "patients/history";
	}

	// ─────────────────────────────────────
	// PACIENTE — CONSULTAS
	// ─────────────────────────────────────

	@GetMapping("/consultations")
	public String myConsultations(Principal principal, Model model) {
		Patient patient = getLoggedPatient(principal);
		model.addAttribute("consultations", consultationService.findByPatient(patient.getId()));
		model.addAttribute("activeMenu", "myConsultations");
		model.addAttribute("title", "Minhas Consultas");
		model.addAttribute("pageTitle", "Minhas Consultas");
		return "patients/my-consultations";
	}

	@GetMapping("/consultations/new")
	public String newConsultationForm(Principal principal, Model model) {
		Patient patient = getLoggedPatient(principal);
		ConsultationCreateDto dto = new ConsultationCreateDto();
		dto.setPatientId(patient.getId());
		dto.setStatus(ConsultationStatus.AGENDADA);
		model.addAttribute("consultationDTO", dto);
		model.addAttribute("doctors", doctorService.findAll());
		model.addAttribute("activeMenu", "myConsultations");
		model.addAttribute("title", "Marcar Consulta");
		model.addAttribute("pageTitle", "Marcar Consulta");
		return "patients/mark-consultation";
	}

	@PostMapping("/consultations/save")
	public String saveConsultation(Principal principal, @ModelAttribute("consultationDTO") ConsultationCreateDto dto,
			Model model, RedirectAttributes attributes) {
		try {
			Patient patient = getLoggedPatient(principal);
			dto.setPatientId(patient.getId());
			dto.setStatus(ConsultationStatus.AGENDADA);
			consultationService.create(dto);
			attributes.addFlashAttribute("success", "Consulta marcada com sucesso!");
			return "redirect:/patients/consultations";
		} catch (Exception e) {
			model.addAttribute("doctors", doctorService.findAll());
			model.addAttribute("errorMessage", e.getMessage());
			model.addAttribute("activeMenu", "myConsultations");
			model.addAttribute("title", "Marcar Consulta");
			model.addAttribute("pageTitle", "Marcar Consulta");
			return "patients/mark-consultation";
		}
	}

	@GetMapping("/consultations/{id}/cancel")
	public String cancelConsultation(@PathVariable Long id, Principal principal, RedirectAttributes attributes) {
		try {
			Patient patient = getLoggedPatient(principal);
			ConsultationResponseDto consultation = consultationService.findById(id);
			if (!consultation.getPatientId().equals(patient.getId())) {
				throw new RuntimeException("Não tem permissão para " + "cancelar esta consulta!");
			}
			consultationService.cancel(id);
			attributes.addFlashAttribute("success", "Consulta cancelada com sucesso!");
		} catch (Exception e) {
			attributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/patients/consultations";
	}
}