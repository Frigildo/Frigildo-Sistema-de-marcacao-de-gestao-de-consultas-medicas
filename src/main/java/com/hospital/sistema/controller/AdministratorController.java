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

import com.hospital.sistema.dtos.AdministratorCreateDtoAdmin;
import com.hospital.sistema.dtos.AdministratorResponseDtoAdmin;
import com.hospital.sistema.dtos.AdministratorUpdateDtoAdmin;
import com.hospital.sistema.service.AdministratorService;
import com.hospital.sistema.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/administrators")
@RequiredArgsConstructor
public class AdministratorController {

	private final AdministratorService service;
	private final UserService userService;

	@GetMapping
	public String list(@RequestParam(required = false) String search, Model model) {
		if (search != null && !search.isBlank()) {
			model.addAttribute("administrators", service.findByName(search));
			model.addAttribute("search", search);
		} else {
			model.addAttribute("administrators", service.findAll());
		}
		model.addAttribute("activeMenu", "administrators");
		model.addAttribute("title", "Administradores");
		model.addAttribute("pageTitle", "Lista de Administradores");
		return "administrators/list";
	}

	@GetMapping("/new")
	public String newForm(@RequestParam(required = false) Long userId, Model model) {

		AdministratorCreateDtoAdmin dto = new AdministratorCreateDtoAdmin();

		if (userId != null) {
			dto.setUserId(userId);
			model.addAttribute("selectedUser", userService.findById(userId));
		}

		model.addAttribute("administratorDTO", dto);
		model.addAttribute("activeMenu", "administrators");
		model.addAttribute("title", "Novo Administrador");
		model.addAttribute("pageTitle", "Novo Administrador");
		return "administrators/form";
	}

	@GetMapping("/select-user")
	public String selectUser(@RequestParam(required = false) String search, Model model) {

		if (search != null && !search.isBlank()) {
			model.addAttribute("users", userService.findByEmail(search));
			model.addAttribute("search", search);
		} else {
			model.addAttribute("users", userService.findAll());
		}

		model.addAttribute("activeMenu", "administrators");
		model.addAttribute("title", "Seleccionar Utilizador");
		model.addAttribute("pageTitle", "Seleccionar Utilizador");
		return "administrators/select-user";
	}

	@PostMapping("/save")
	public String save(@ModelAttribute("administratorDTO") AdministratorCreateDtoAdmin dto, Model model,
			RedirectAttributes attributes) {
		try {
			service.create(dto);
			attributes.addFlashAttribute("success", "Administrador criado com sucesso!");
			return "redirect:/administrators";
		} catch (Exception e) {
			model.addAttribute("administratorDTO", dto);
			if (dto.getUserId() != null) {
				model.addAttribute("selectedUser", userService.findById(dto.getUserId()));
			}
			model.addAttribute("errorMessage", e.getMessage());
			model.addAttribute("activeMenu", "administrators");
			model.addAttribute("title", "Novo Administrador");
			model.addAttribute("pageTitle", "Novo Administrador");
			return "administrators/form";
		}
	}

	@GetMapping("/{id}/edit")
	public String editForm(@PathVariable Long id, Model model) {
		AdministratorResponseDtoAdmin administrator = service.findById(id);

		AdministratorUpdateDtoAdmin dto = new AdministratorUpdateDtoAdmin();
		dto.setName(administrator.getName());

		model.addAttribute("administratorDTO", dto);
		model.addAttribute("administratorId", id);
		model.addAttribute("administrator", administrator);
		model.addAttribute("activeMenu", "administrators");
		model.addAttribute("title", "Editar Administrador");
		model.addAttribute("pageTitle", "Editar Administrador");
		return "administrators/form";
	}

	@PostMapping("/{id}/update")
	public String update(@PathVariable Long id, @ModelAttribute("administratorDTO") AdministratorUpdateDtoAdmin dto,
			Model model, RedirectAttributes attributes) {
		try {
			service.update(id, dto);
			attributes.addFlashAttribute("success", "Administrador actualizado com sucesso!");
			return "redirect:/administrators";
		} catch (Exception e) {
			model.addAttribute("administratorDTO", dto);
			model.addAttribute("administratorId", id);
			model.addAttribute("errorMessage", e.getMessage());
			model.addAttribute("activeMenu", "administrators");
			model.addAttribute("title", "Editar Administrador");
			model.addAttribute("pageTitle", "Editar Administrador");
			return "administrators/form";
		}
	}

	@GetMapping("/{id}/delete")
	public String delete(@PathVariable Long id, RedirectAttributes attributes) {
		try {
			service.delete(id);
			attributes.addFlashAttribute("success", "Administrador apagado com sucesso!");
		} catch (Exception e) {
			attributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/administrators";
	}
}