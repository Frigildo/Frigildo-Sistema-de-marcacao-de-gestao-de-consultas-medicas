package com.hospital.sistema.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hospital.sistema.dtos.AgendaCreateDto;
import com.hospital.sistema.service.AgendaService;
import com.hospital.sistema.service.DoctorService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AgendaController {

	private final AgendaService agendaService;
	private final DoctorService doctorService;

	// ─────────────────────────────────────
	// ADMIN — gere horários
	// ─────────────────────────────────────

	@GetMapping("/agenda")
	public String agenda(@RequestParam(required = false) Long doctorId, Model model) {

		model.addAttribute("agendaDTO", new AgendaCreateDto());

		if (doctorId != null) {
			model.addAttribute("selectedDoctor", doctorService.findById(doctorId));
			model.addAttribute("slots", agendaService.findByDoctor(doctorId));
			model.addAttribute("doctorId", doctorId);
		} else {
			model.addAttribute("selectedDoctor", null);
			model.addAttribute("slots", null);
		}

		model.addAttribute("activeMenu", "agenda");
		model.addAttribute("title", "Agenda");
		model.addAttribute("pageTitle", "Gestão de Horários");
		return "agenda/admin-agenda";
	}

	@GetMapping("/agenda/select-doctor")
	public String selectDoctor(@RequestParam(required = false) String search, Model model) {

		if (search != null && !search.isBlank()) {
			model.addAttribute("doctors", doctorService.findByName(search));
			model.addAttribute("search", search);
		} else {
			model.addAttribute("doctors", doctorService.findAll());
		}

		model.addAttribute("activeMenu", "agenda");
		model.addAttribute("title", "Seleccionar Médico");
		model.addAttribute("pageTitle", "Seleccionar Médico");
		return "agenda/select-doctor";
	}

	@PostMapping("/agenda/save")
	public String saveSlot(@RequestParam Long doctorId, @ModelAttribute("agendaDTO") AgendaCreateDto dto,
			RedirectAttributes attributes) {
		try {
			agendaService.createByAdmin(dto, doctorId);
			attributes.addFlashAttribute("success", "Horário adicionado com sucesso!");
		} catch (Exception e) {
			attributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/agenda?doctorId=" + doctorId;
	}

	@GetMapping("/agenda/{id}/delete")
	public String deleteSlot(@PathVariable Long id, @RequestParam Long doctorId, RedirectAttributes attributes) {
		try {
			agendaService.deleteByAdmin(id);
			attributes.addFlashAttribute("success", "Horário removido com sucesso!");
		} catch (Exception e) {
			attributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/agenda?doctorId=" + doctorId;
	}

	// ─────────────────────────────────────
	// PACIENTE — ver horários disponíveis
	// ─────────────────────────────────────

	@GetMapping("/available-slots")
	public String availableSlots(Model model) {
		model.addAttribute("slots", agendaService.findAvailable());
		model.addAttribute("activeMenu", "availableSlots");
		model.addAttribute("title", "Horários Disponíveis");
		model.addAttribute("pageTitle", "Horários Disponíveis");
		return "agenda/available-slots";
	}

	@GetMapping("/available-slots/{id}/book")
	public String bookSlot(@PathVariable Long id, Principal principal, RedirectAttributes attributes) {
		try {
			agendaService.bookSlot(id, principal);
			attributes.addFlashAttribute("success", "Consulta marcada com sucesso!");
			return "redirect:/patients/consultations";
		} catch (Exception e) {
			attributes.addFlashAttribute("error", e.getMessage());
			return "redirect:/available-slots";
		}
	}
}