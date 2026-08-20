package com.hospital.sistema.controller;

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

import com.hospital.sistema.dtos.ExamCreateDto;
import com.hospital.sistema.dtos.ExamResponseDto;
import com.hospital.sistema.dtos.ExamUpdateDto;
import com.hospital.sistema.enums.ExamType;
import com.hospital.sistema.service.AttendanceService;
import com.hospital.sistema.service.ExamService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/exams")
@RequiredArgsConstructor
public class ExamController {

	private final ExamService service;
	private final AttendanceService attendanceService;

	// ─────────────────────────────────────
	// LISTA
	// ─────────────────────────────────────

	@GetMapping
	public String list(@RequestParam(required = false) ExamType search, Model model) {
		if (search != null) {
			model.addAttribute("exams", service.findByType(search));
			model.addAttribute("search", search);
		} else {
			model.addAttribute("exams", service.findAll());
		}
		model.addAttribute("examTypes", ExamType.values());
		model.addAttribute("activeMenu", "exams");
		model.addAttribute("title", "Exames");
		model.addAttribute("pageTitle", "Lista de Exames");
		return "exams/list";
	}

	// ─────────────────────────────────────
	// CRIAR
	// ─────────────────────────────────────

	@GetMapping("/new")
	public String newForm(@RequestParam(required = false) Long attendanceId, Model model) {

		ExamCreateDto dto = new ExamCreateDto();

		if (attendanceId != null) {
			dto.setAttendanceId(attendanceId);
			model.addAttribute("attendance", attendanceService.findById(attendanceId));
			model.addAttribute("attendanceId", attendanceId);
		}

		model.addAttribute("examDTO", dto);
		model.addAttribute("examTypes", ExamType.values());
		model.addAttribute("activeMenu", "exams");
		model.addAttribute("title", "Novo Exame");
		model.addAttribute("pageTitle", "Novo Exame");
		return "exams/form";
	}

	@PostMapping("/save")
	public String save(@ModelAttribute("examDTO") ExamCreateDto dto,
			@RequestParam(value = "file", required = false) MultipartFile file, Model model,
			RedirectAttributes attributes) {
		try {
			dto.setFile(file);
			service.create(dto);
			attributes.addFlashAttribute("success", "Exame criado com sucesso!");

			if (dto.getAttendanceId() != null) {
				return "redirect:/attendances/" + dto.getAttendanceId() + "/edit";
			}
			return "redirect:/exams";
		} catch (Exception e) {
			model.addAttribute("examDTO", dto);
			model.addAttribute("examTypes", ExamType.values());
			if (dto.getAttendanceId() != null) {
				model.addAttribute("attendance", attendanceService.findById(dto.getAttendanceId()));
				model.addAttribute("attendanceId", dto.getAttendanceId());
			}
			model.addAttribute("errorMessage", e.getMessage());
			model.addAttribute("activeMenu", "exams");
			model.addAttribute("title", "Novo Exame");
			model.addAttribute("pageTitle", "Novo Exame");
			return "exams/form";
		}
	}

	// ─────────────────────────────────────
	// EDITAR
	// ─────────────────────────────────────

	@GetMapping("/{id}/edit")
	public String editForm(@PathVariable Long id, Model model) {
		ExamResponseDto exam = service.findById(id);

		ExamUpdateDto dto = new ExamUpdateDto();
		dto.setType(exam.getType());
		dto.setResult(exam.getResult());
		dto.setDate(exam.getDate());

		model.addAttribute("examDTO", dto);
		model.addAttribute("examId", id);
		model.addAttribute("exam", exam);
		model.addAttribute("examTypes", ExamType.values());
		model.addAttribute("activeMenu", "exams");
		model.addAttribute("title", "Editar Exame");
		model.addAttribute("pageTitle", "Editar Exame");
		return "exams/form";
	}

	@PostMapping("/{id}/update")
	public String update(@PathVariable Long id, @ModelAttribute("examDTO") ExamUpdateDto dto,
			@RequestParam(value = "file", required = false) MultipartFile file, Model model,
			RedirectAttributes attributes) {
		try {
			dto.setFile(file);
			service.update(id, dto);
			attributes.addFlashAttribute("success", "Exame actualizado com sucesso!");
			return "redirect:/exams";
		} catch (Exception e) {
			model.addAttribute("examDTO", dto);
			model.addAttribute("examId", id);
			model.addAttribute("examTypes", ExamType.values());
			model.addAttribute("errorMessage", e.getMessage());
			model.addAttribute("activeMenu", "exams");
			model.addAttribute("title", "Editar Exame");
			model.addAttribute("pageTitle", "Editar Exame");
			return "exams/form";
		}
	}

	// ─────────────────────────────────────
	// APAGAR
	// ─────────────────────────────────────

	@GetMapping("/{id}/delete")
	public String delete(@PathVariable Long id, RedirectAttributes attributes) {
		try {
			service.delete(id);
			attributes.addFlashAttribute("success", "Exame apagado com sucesso!");
		} catch (Exception e) {
			attributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/exams";
	}
}