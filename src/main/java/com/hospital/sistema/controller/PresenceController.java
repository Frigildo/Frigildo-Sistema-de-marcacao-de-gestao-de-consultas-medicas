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

import com.hospital.sistema.dtos.PresenceCreateDto;
import com.hospital.sistema.dtos.PresenceResponseDto;
import com.hospital.sistema.dtos.PresenceUpdateDto;
import com.hospital.sistema.entities.Doctor;
import com.hospital.sistema.entities.User;
import com.hospital.sistema.enums.PresenceStatus;
import com.hospital.sistema.enums.Profile;
import com.hospital.sistema.repository.DoctorRepository;
import com.hospital.sistema.repository.UserRepository;
import com.hospital.sistema.service.ConsultationService;
import com.hospital.sistema.service.PresenceService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/presences")
@RequiredArgsConstructor
public class PresenceController {

	private final PresenceService service;
	private final ConsultationService consultationService;
	private final UserRepository userRepository;
	private final DoctorRepository doctorRepository;

	// ─────────────────────────────────────
	// HELPER
	// ─────────────────────────────────────

	private void addConsultations(Model model, String search, Principal principal) {
		User user = userRepository.findByEmail(principal.getName()).orElseThrow();

		if (user.getProfile() == Profile.MEDICO) {
			Doctor doctor = doctorRepository.findByUser(user).orElseThrow();
			model.addAttribute("consultations",
					consultationService.findScheduledWithoutPresenceByDoctor(doctor.getId(), search));
		} else {
			if (search != null && !search.isBlank()) {
				model.addAttribute("consultations", consultationService.findByPatientName(search));
			} else {
				model.addAttribute("consultations", consultationService.findAgendadasSemPresenca());
			}
		}

		if (search != null && !search.isBlank()) {
			model.addAttribute("search", search);
		}
	}

	// ─────────────────────────────────────
	// LISTA
	// ─────────────────────────────────────

	@GetMapping
	public String list(@RequestParam(required = false) PresenceStatus status, Model model) {
		if (status != null) {
			model.addAttribute("presences", service.findByStatus(status));
			model.addAttribute("selectedStatus", status);
		} else {
			model.addAttribute("presences", service.findAll());
		}
		model.addAttribute("statuses", PresenceStatus.values());
		model.addAttribute("activeMenu", "presences");
		model.addAttribute("title", "Presenças");
		model.addAttribute("pageTitle", "Lista de Presenças");
		return "presences/list";
	}

	// ─────────────────────────────────────
	// CRIAR
	// ─────────────────────────────────────

	@GetMapping("/new")
	public String newForm(@RequestParam(required = false) String search, Model model, Principal principal) {
		addConsultations(model, search, principal);
		model.addAttribute("presenceDTO", new PresenceCreateDto());
		model.addAttribute("statuses", PresenceStatus.values());
		model.addAttribute("activeMenu", "presences");
		model.addAttribute("title", "Nova Presença");
		model.addAttribute("pageTitle", "Nova Presença");
		return "presences/form";
	}

	@GetMapping("/new-for")
	public String newFormWithConsultation(@RequestParam Long consultationId, Model model, Principal principal) {
		addConsultations(model, null, principal);
		model.addAttribute("presenceDTO", new PresenceCreateDto());
		model.addAttribute("selectedConsultation", consultationService.findById(consultationId));
		model.addAttribute("statuses", PresenceStatus.values());
		model.addAttribute("activeMenu", "presences");
		model.addAttribute("title", "Nova Presença");
		model.addAttribute("pageTitle", "Nova Presença");
		return "presences/form";
	}

	@PostMapping("/save")
	public String save(@ModelAttribute("presenceDTO") PresenceCreateDto dto, Model model, Principal principal,
			RedirectAttributes attributes) {
		try {
			service.create(dto);
			attributes.addFlashAttribute("success", "Presença registada com sucesso!");
			return "redirect:/presences";
		} catch (Exception e) {
			addConsultations(model, null, principal);
			model.addAttribute("statuses", PresenceStatus.values());
			model.addAttribute("errorMessage", e.getMessage());
			model.addAttribute("activeMenu", "presences");
			model.addAttribute("title", "Nova Presença");
			model.addAttribute("pageTitle", "Nova Presença");
			if (dto.getConsultationId() != null) {
				try {
					model.addAttribute("selectedConsultation", consultationService.findById(dto.getConsultationId()));
				} catch (Exception ignored) {
				}
			}
			return "presences/form";
		}
	}

	// ─────────────────────────────────────
	// EDITAR
	// ─────────────────────────────────────

	@GetMapping("/{id}/edit")
	public String editForm(@PathVariable Long id, Model model) {
		PresenceResponseDto presence = service.findById(id);

		PresenceUpdateDto dto = new PresenceUpdateDto();
		dto.setStatus(presence.getStatus());
		dto.setJustification(presence.getJustification());

		model.addAttribute("presenceDTO", dto);
		model.addAttribute("presenceId", id);
		model.addAttribute("presence", presence);
		model.addAttribute("statuses", PresenceStatus.values());
		model.addAttribute("activeMenu", "presences");
		model.addAttribute("title", "Editar Presença");
		model.addAttribute("pageTitle", "Editar Presença");
		return "presences/form";
	}

	@PostMapping("/{id}/update")
	public String update(@PathVariable Long id, @ModelAttribute("presenceDTO") PresenceUpdateDto dto, Model model,
			RedirectAttributes attributes) {
		try {
			service.update(id, dto);
			attributes.addFlashAttribute("success", "Presença actualizada com sucesso!");
			return "redirect:/presences";
		} catch (Exception e) {
			PresenceResponseDto presence = service.findById(id);
			model.addAttribute("presenceDTO", dto);
			model.addAttribute("presenceId", id);
			model.addAttribute("presence", presence);
			model.addAttribute("statuses", PresenceStatus.values());
			model.addAttribute("errorMessage", e.getMessage());
			model.addAttribute("activeMenu", "presences");
			model.addAttribute("title", "Editar Presença");
			model.addAttribute("pageTitle", "Editar Presença");
			return "presences/form";
		}
	}

	// ─────────────────────────────────────
	// APAGAR
	// ─────────────────────────────────────

	@GetMapping("/{id}/delete")
	public String delete(@PathVariable Long id, RedirectAttributes attributes) {
		try {
			service.delete(id);
			attributes.addFlashAttribute("success", "Presença apagada com sucesso!");
		} catch (Exception e) {
			attributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/presences";
	}
}