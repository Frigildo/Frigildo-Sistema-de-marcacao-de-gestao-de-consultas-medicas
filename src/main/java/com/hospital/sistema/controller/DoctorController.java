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

import com.hospital.sistema.dtos.DoctorCreateDtoAdmin;
import com.hospital.sistema.dtos.DoctorResponseDtoAdmin;
import com.hospital.sistema.dtos.DoctorUpdateDtoAdmin;
import com.hospital.sistema.service.DoctorService;
import com.hospital.sistema.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorController {

	private final DoctorService service;
	private final UserService userService;

	@GetMapping
	public String list(@RequestParam(required = false) String search, Model model) {
		if (search != null && !search.isBlank()) {
			model.addAttribute("doctors", service.findByName(search));
			model.addAttribute("search", search);
		} else {
			model.addAttribute("doctors", service.findAll());
		}
		model.addAttribute("activeMenu", "doctors");
		model.addAttribute("title", "Médicos");
		model.addAttribute("pageTitle", "Lista de Médicos");
		return "doctors/list";
	}

	@GetMapping("/new")
	public String newForm(@RequestParam(required = false) Long userId, Model model) {

		DoctorCreateDtoAdmin dto = new DoctorCreateDtoAdmin();

		// Se userId foi passado via select page
		if (userId != null) {
			dto.setUserId(userId);
			model.addAttribute("selectedUser", userService.findById(userId));
		}

		model.addAttribute("doctorDTO", dto);
		model.addAttribute("activeMenu", "doctors");
		model.addAttribute("title", "Novo Médico");
		model.addAttribute("pageTitle", "Novo Médico");
		return "doctors/form";
	}

	// Página de selecção de utilizador
	@GetMapping("/select-user")
	public String selectUser(@RequestParam(required = false) String search,
			@RequestParam(required = false) String returnTo, Model model) {

		if (search != null && !search.isBlank()) {
			model.addAttribute("users", userService.findByEmail(search));
			model.addAttribute("search", search);
		} else {
			model.addAttribute("users", userService.findAll());
		}

		model.addAttribute("returnTo", returnTo != null ? returnTo : "new");
		model.addAttribute("activeMenu", "doctors");
		model.addAttribute("title", "Seleccionar Utilizador");
		model.addAttribute("pageTitle", "Seleccionar Utilizador");
		return "doctors/select-user";
	}

	@PostMapping("/save")
	public String save(@ModelAttribute("doctorDTO") DoctorCreateDtoAdmin dto, Model model,
			RedirectAttributes attributes) {
		try {
			service.create(dto);
			attributes.addFlashAttribute("success", "Médico criado com sucesso!");
			return "redirect:/doctors";
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
			if (dto.getUserId() != null) {
				model.addAttribute("selectedUser", userService.findById(dto.getUserId()));
			}
			model.addAttribute("activeMenu", "doctors");
			model.addAttribute("title", "Novo Médico");
			model.addAttribute("pageTitle", "Novo Médico");
			return "doctors/form";
		}
	}

	@GetMapping("/{id}/edit")
	public String editForm(@PathVariable Long id, Model model) {
		DoctorResponseDtoAdmin doctor = service.findById(id);

		DoctorUpdateDtoAdmin dto = new DoctorUpdateDtoAdmin();
		dto.setName(doctor.getName());
		dto.setCellPhone(doctor.getCellPhone());

		model.addAttribute("doctorDTO", dto);
		model.addAttribute("doctorId", id);
		model.addAttribute("doctor", doctor);
		model.addAttribute("activeMenu", "doctors");
		model.addAttribute("title", "Editar Médico");
		model.addAttribute("pageTitle", "Editar Médico");
		return "doctors/form";
	}

	@PostMapping("/{id}/update")
	public String update(@PathVariable Long id, @ModelAttribute("doctorDTO") DoctorUpdateDtoAdmin dto, Model model,
			RedirectAttributes attributes) {
		try {
			service.update(id, dto);
			attributes.addFlashAttribute("success", "Médico actualizado com sucesso!");
			return "redirect:/doctors";
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
			model.addAttribute("doctorId", id);
			model.addAttribute("activeMenu", "doctors");
			model.addAttribute("title", "Editar Médico");
			model.addAttribute("pageTitle", "Editar Médico");
			return "doctors/form";
		}
	}

	@GetMapping("/{id}/delete")
	public String delete(@PathVariable Long id, RedirectAttributes attributes) {
		try {
			service.delete(id);
			attributes.addFlashAttribute("success", "Médico apagado com sucesso!");
		} catch (Exception e) {
			attributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/doctors";
	}
}