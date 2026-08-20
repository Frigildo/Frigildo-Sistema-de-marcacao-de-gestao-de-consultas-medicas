package com.hospital.sistema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hospital.sistema.dtos.CounselingCreateDto;
import com.hospital.sistema.dtos.CounselingResponseDto;
import com.hospital.sistema.dtos.CounselingUpdateDto;
import com.hospital.sistema.service.AttendanceService;
import com.hospital.sistema.service.CounselingService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/counselings")
@RequiredArgsConstructor
public class CounselingController {

	private final CounselingService service;
	private final AttendanceService attendanceService;

	@GetMapping
	public String list(Model model) {
		model.addAttribute("counselings", service.findAll());
		model.addAttribute("activeMenu", "counselings");
		model.addAttribute("title", "Aconselhamentos");
		model.addAttribute("pageTitle", "Lista de Aconselhamentos");
		return "counselings/list";
	}

	@GetMapping("/new")
	public String newForm(@RequestParam(required = false) Long attendanceId, Model model) {

		CounselingCreateDto dto = new CounselingCreateDto();

		// Se attendanceId foi passado
		if (attendanceId != null) {
			dto.setAttendanceId(attendanceId);
			model.addAttribute("selectedAttendance", attendanceService.findById(attendanceId));
		}

		model.addAttribute("counselingDTO", dto);
		model.addAttribute("activeMenu", "counselings");
		model.addAttribute("title", "Novo Aconselhamento");
		model.addAttribute("pageTitle", "Novo Aconselhamento");
		return "counselings/form";
	}

	@PostMapping("/save")
	public String save(@ModelAttribute("counselingDTO") CounselingCreateDto dto, Model model,
			RedirectAttributes attributes) {
		try {
			service.create(dto);
			attributes.addFlashAttribute("success", "Aconselhamento criado com sucesso!");
			return "redirect:/counselings";
		} catch (Exception e) {
			// ← IMPORTANTE: sempre envia o DTO
			model.addAttribute("counselingDTO", dto);
			if (dto.getAttendanceId() != null) {
				model.addAttribute("selectedAttendance", attendanceService.findById(dto.getAttendanceId()));
			}
			model.addAttribute("errorMessage", e.getMessage());
			model.addAttribute("activeMenu", "counselings");
			model.addAttribute("title", "Novo Aconselhamento");
			model.addAttribute("pageTitle", "Novo Aconselhamento");
			return "counselings/form";
		}
	}

	@GetMapping("/{id}/edit")
	public String editForm(@PathVariable Long id, Model model) {
		CounselingResponseDto counseling = service.findById(id);

		CounselingUpdateDto dto = new CounselingUpdateDto();
		dto.setDescription(counseling.getDescription());
		dto.setDate(counseling.getDate());

		model.addAttribute("counselingDTO", dto);
		model.addAttribute("counselingId", id);
		model.addAttribute("counseling", counseling);
		model.addAttribute("activeMenu", "counselings");
		model.addAttribute("title", "Editar Aconselhamento");
		model.addAttribute("pageTitle", "Editar Aconselhamento");
		return "counselings/form";
	}

	@PostMapping("/{id}/update")
	public String update(@PathVariable Long id, @ModelAttribute("counselingDTO") CounselingUpdateDto dto, Model model,
			RedirectAttributes attributes) {
		try {
			service.update(id, dto);
			attributes.addFlashAttribute("success", "Aconselhamento actualizado com sucesso!");
			return "redirect:/counselings";
		} catch (Exception e) {
			// ← IMPORTANTE: sempre envia o DTO
			model.addAttribute("counselingDTO", dto);
			model.addAttribute("counselingId", id);
			model.addAttribute("errorMessage", e.getMessage());
			model.addAttribute("activeMenu", "counselings");
			model.addAttribute("title", "Editar Aconselhamento");
			model.addAttribute("pageTitle", "Editar Aconselhamento");
			return "counselings/form";
		}
	}

	@GetMapping("/{id}/delete")
	public String delete(@PathVariable Long id, RedirectAttributes attributes) {
		try {
			service.delete(id);
			attributes.addFlashAttribute("success", "Aconselhamento apagado com sucesso!");
		} catch (Exception e) {
			attributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/counselings";
	}

	@GetMapping("/select-attendance")
	public String selectAttendance(@RequestParam(required = false) String search, Model model) {

		if (search != null && !search.isBlank()) {
			model.addAttribute("attendances", attendanceService.findAll().stream()
					.filter(a -> a.getPatientName().toLowerCase().contains(search.toLowerCase())).toList());
			model.addAttribute("search", search);
		} else {
			model.addAttribute("attendances", attendanceService.findAll());
		}

		model.addAttribute("activeMenu", "counselings");
		model.addAttribute("title", "Seleccionar Atendimento");
		model.addAttribute("pageTitle", "Seleccionar Atendimento");
		return "counselings/select-attendance";
	}
}