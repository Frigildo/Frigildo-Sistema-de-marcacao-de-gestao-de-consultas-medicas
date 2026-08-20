package com.hospital.sistema.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hospital.sistema.dtos.UserCreateDtoAdmin;
import com.hospital.sistema.dtos.UserResponseDto;
import com.hospital.sistema.dtos.UserUpdateDtoAdmin;
import com.hospital.sistema.enums.Profile;
import com.hospital.sistema.service.UserService;

@Controller
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService service;

	@GetMapping
	public String list(@RequestParam(required = false) String search, Model model) {

		if (search != null && !search.isBlank()) {
			model.addAttribute("users", service.findByEmail(search));
			model.addAttribute("search", search);
		} else {
			model.addAttribute("users", service.findAll());
		}

		model.addAttribute("activeMenu", "users");
		model.addAttribute("title", "Utilizadores");
		model.addAttribute("pageTitle", "Lista de Utilizadores");
		return "users/list";
	}

	@GetMapping("/new")
	public String newForm(Model model) {
		model.addAttribute("userDTO", new UserCreateDtoAdmin());
		model.addAttribute("profiles", Profile.values());
		model.addAttribute("activeMenu", "users");
		model.addAttribute("title", "Novo Utilizador");
		model.addAttribute("pageTitle", "Novo Utilizador");
		return "users/form";
	}

	@PostMapping("/save")
	public String save(@ModelAttribute("userDTO") UserCreateDtoAdmin dto, RedirectAttributes attributes) {
		try {
			service.save(dto);
			attributes.addFlashAttribute("success", "Utilizador criado com sucesso!");
		} catch (Exception e) {
			attributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/users";
	}

	@GetMapping("/{id}/edit")
	public String editForm(@PathVariable Long id, Model model) {
		UserResponseDto user = service.findById(id);

		UserUpdateDtoAdmin dto = new UserUpdateDtoAdmin();
		dto.setEmail(user.getEmail());
		dto.setProfile(user.getProfile());

		model.addAttribute("userDTO", dto);
		model.addAttribute("userId", id);
		model.addAttribute("profiles", Profile.values());
		model.addAttribute("activeMenu", "users");
		model.addAttribute("title", "Editar Utilizador");
		model.addAttribute("pageTitle", "Editar Utilizador");
		return "users/form";
	}

	@PostMapping("/{id}/update")
	public String update(@PathVariable Long id, @ModelAttribute("userDTO") UserUpdateDtoAdmin dto,
			RedirectAttributes attributes) {
		try {
			service.update(id, dto);
			attributes.addFlashAttribute("success", "Utilizador actualizado com sucesso!");
		} catch (Exception e) {
			attributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/users";
	}

	@GetMapping("/{id}/delete")
	public String delete(@PathVariable Long id, RedirectAttributes attributes) {
		try {
			service.delete(id);
			attributes.addFlashAttribute("success", "Utilizador apagado com sucesso!");
		} catch (Exception e) {
			attributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/users";
	}
}