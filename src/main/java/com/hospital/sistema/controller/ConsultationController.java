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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hospital.sistema.dtos.AgendaResponseDto;
import com.hospital.sistema.dtos.ConsultationCreateDto;
import com.hospital.sistema.dtos.ConsultationResponseDto;
import com.hospital.sistema.dtos.ConsultationUpdateDto;
import com.hospital.sistema.entities.Doctor;
import com.hospital.sistema.entities.User;
import com.hospital.sistema.enums.ConsultationStatus;
import com.hospital.sistema.enums.Profile;
import com.hospital.sistema.repository.DoctorRepository;
import com.hospital.sistema.repository.UserRepository;
import com.hospital.sistema.service.AgendaService;
import com.hospital.sistema.service.ConsultationService;
import com.hospital.sistema.service.DoctorService;
import com.hospital.sistema.service.PatientService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/consultations")
@RequiredArgsConstructor
public class ConsultationController {

	private final ConsultationService service;
	private final PatientService patientService;
	private final DoctorService doctorService;
	private final AgendaService agendaService;
	private final UserRepository userRepository;
	private final DoctorRepository doctorRepository;

	@GetMapping
	public String list(@RequestParam(required = false) ConsultationStatus status, Model model, Principal principal) {

		User user = userRepository.findByEmail(principal.getName()).orElseThrow();

		List<ConsultationResponseDto> consultations;

		if (user.getProfile() == Profile.MEDICO) {
			Doctor doctor = doctorRepository.findByUser(user).orElseThrow();

			if (status != null) {
				consultations = service.findByDoctor(doctor.getId()).stream().filter(c -> c.getStatus() == status)
						.toList();
			} else {
				consultations = service.findByDoctor(doctor.getId());
			}
		} else {
			if (status != null) {
				consultations = service.findByStatus(status);
			} else {
				consultations = service.findAll();
			}
		}

		model.addAttribute("consultations", consultations);
		model.addAttribute("statuses", ConsultationStatus.values());
		model.addAttribute("selectedStatus", status);
		model.addAttribute("activeMenu", "consultations");
		model.addAttribute("title", "Consultas");
		model.addAttribute("pageTitle", "Lista de Consultas");
		return "consultations/list";
	}

	@GetMapping("/new")
	public String newForm(@RequestParam(required = false) Long patientId, @RequestParam(required = false) Long doctorId,
			@RequestParam(required = false) Long agendaId, Model model) {

		ConsultationCreateDto dto = new ConsultationCreateDto();

		if (patientId != null) {
			dto.setPatientId(patientId);
			model.addAttribute("selectedPatient", patientService.findById(patientId));
		}

		if (doctorId != null) {
			dto.setDoctorId(doctorId);
			model.addAttribute("selectedDoctor", doctorService.findById(doctorId));
		}

		if (agendaId != null) {
			AgendaResponseDto slot = agendaService.findById(agendaId);
			dto.setDate(slot.getDate());
			dto.setTime(slot.getStartTime());
			dto.setAgendaId(agendaId);
			model.addAttribute("slot", slot);
		}

		dto.setStatus(ConsultationStatus.AGENDADA);
		model.addAttribute("consultationDTO", dto);
		model.addAttribute("statuses", ConsultationStatus.values());
		model.addAttribute("activeMenu", "consultations");
		model.addAttribute("title", "Nova Consulta");
		model.addAttribute("pageTitle", "Nova Consulta");
		return "consultations/form";
	}

	@GetMapping("/select-patient")
	public String selectPatient(@RequestParam(required = false) String search, Model model) {

		if (search != null && !search.isBlank()) {
			model.addAttribute("patients", patientService.findByName(search));
			model.addAttribute("search", search);
		} else {
			model.addAttribute("patients", patientService.findAll());
		}

		model.addAttribute("activeMenu", "consultations");
		model.addAttribute("title", "Seleccionar Paciente");
		model.addAttribute("pageTitle", "Seleccionar Paciente");
		return "consultations/select-patient";
	}

	@GetMapping("/select-doctor")
	public String selectDoctor(@RequestParam(required = false) String search,
			@RequestParam(required = false) Long patientId, Model model) {

		if (search != null && !search.isBlank()) {
			model.addAttribute("slots", agendaService.findAvailable().stream()
					.filter(s -> s.getDoctorName().toLowerCase().contains(search.toLowerCase())).toList());
			model.addAttribute("search", search);
		} else {
			model.addAttribute("slots", agendaService.findAvailable());
		}

		if (patientId != null) {
			model.addAttribute("selectedPatient", patientService.findById(patientId));
		}

		model.addAttribute("patientId", patientId);
		model.addAttribute("activeMenu", "consultations");
		model.addAttribute("title", "Seleccionar Médico");
		model.addAttribute("pageTitle", "Seleccionar Médico e Horário");
		return "consultations/select-doctor";
	}

	@PostMapping("/save")
	public String save(@ModelAttribute("consultationDTO") ConsultationCreateDto dto, Model model,
			RedirectAttributes attributes) {
		try {
			service.create(dto);
			attributes.addFlashAttribute("success", "Consulta criada com sucesso!");
			return "redirect:/consultations";
		} catch (Exception e) {
			model.addAttribute("consultationDTO", dto);
			if (dto.getPatientId() != null) {
				model.addAttribute("selectedPatient", patientService.findById(dto.getPatientId()));
			}
			if (dto.getDoctorId() != null) {
				model.addAttribute("selectedDoctor", doctorService.findById(dto.getDoctorId()));
			}
			model.addAttribute("statuses", ConsultationStatus.values());
			model.addAttribute("errorMessage", e.getMessage());
			model.addAttribute("activeMenu", "consultations");
			model.addAttribute("title", "Nova Consulta");
			model.addAttribute("pageTitle", "Nova Consulta");
			return "consultations/form";
		}
	}

	@GetMapping("/{id}/edit")
	public String editForm(@PathVariable Long id, Model model) {
		ConsultationResponseDto consultation = service.findById(id);

		ConsultationUpdateDto dto = new ConsultationUpdateDto();
		dto.setDate(consultation.getDate());
		dto.setTime(consultation.getTime());
		dto.setStatus(consultation.getStatus());

		model.addAttribute("consultationDTO", dto);
		model.addAttribute("consultationId", id);
		model.addAttribute("consultation", consultation);
		model.addAttribute("statuses", ConsultationStatus.values());
		model.addAttribute("activeMenu", "consultations");
		model.addAttribute("title", "Editar Consulta");
		model.addAttribute("pageTitle", "Editar Consulta");
		return "consultations/form";
	}

	@PostMapping("/{id}/update")
	public String update(@PathVariable Long id, @ModelAttribute("consultationDTO") ConsultationUpdateDto dto,
			Model model, RedirectAttributes attributes) {
		try {
			service.update(id, dto);
			attributes.addFlashAttribute("success", "Consulta actualizada com sucesso!");
			return "redirect:/consultations";
		} catch (Exception e) {
			model.addAttribute("consultationDTO", dto);
			model.addAttribute("consultationId", id);
			model.addAttribute("statuses", ConsultationStatus.values());
			model.addAttribute("errorMessage", e.getMessage());
			model.addAttribute("activeMenu", "consultations");
			model.addAttribute("title", "Editar Consulta");
			model.addAttribute("pageTitle", "Editar Consulta");
			return "consultations/form";
		}
	}

	@GetMapping("/{id}/delete")
	public String delete(@PathVariable Long id, RedirectAttributes attributes) {
		try {
			service.delete(id);
			attributes.addFlashAttribute("success", "Consulta apagada com sucesso!");
		} catch (Exception e) {
			attributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/consultations";
	}
}