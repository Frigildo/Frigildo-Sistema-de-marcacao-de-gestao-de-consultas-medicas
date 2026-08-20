package com.hospital.sistema.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support
        .RedirectAttributes;
import com.hospital.sistema.dtos.*;
import com.hospital.sistema.service.SpecialtyService;

@Controller
@RequestMapping("/specialties")
public class SpecialtyController {

    @Autowired
    private SpecialtyService service;

    @GetMapping
    public String list(
            @RequestParam(required = false) String search,
            Model model) {
        if (search != null && !search.isBlank()) {
            model.addAttribute("specialties",
                service.findByName(search));
            model.addAttribute("search", search);
        } else {
            model.addAttribute("specialties",
                service.findAll());
        }
        model.addAttribute("activeMenu", "specialties");
        model.addAttribute("title", "Especialidades");
        model.addAttribute("pageTitle",
            "Lista de Especialidades");
        return "specialties/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("specialtyDTO",
            new SpecialtyCreateDto());
        model.addAttribute("activeMenu", "specialties");
        model.addAttribute("title", "Nova Especialidade");
        model.addAttribute("pageTitle",
            "Nova Especialidade");
        return "specialties/form";
    }

    @PostMapping("/save")
    public String save(
            @ModelAttribute("specialtyDTO")
            SpecialtyCreateDto dto,
            Model model,
            RedirectAttributes attributes) {
        try {
            service.create(dto);
            attributes.addFlashAttribute("success",
                "Especialidade criada com sucesso!");
            return "redirect:/specialties";
        } catch (Exception e) {
            model.addAttribute("errorMessage",
                e.getMessage());
            model.addAttribute("activeMenu", "specialties");
            model.addAttribute("title",
                "Nova Especialidade");
            model.addAttribute("pageTitle",
                "Nova Especialidade");
            return "specialties/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id,
                           Model model) {
        SpecialtyResponseDto specialty =
            service.findById(id);
        SpecialtyUpdateDto dto = new SpecialtyUpdateDto();
        dto.setName(specialty.getName());
        dto.setDescription(specialty.getDescription());
        model.addAttribute("specialtyDTO", dto);
        model.addAttribute("specialtyId", id);
        model.addAttribute("activeMenu", "specialties");
        model.addAttribute("title", "Editar Especialidade");
        model.addAttribute("pageTitle",
            "Editar Especialidade");
        return "specialties/form";
    }

    @PostMapping("/{id}/update")
    public String update(
            @PathVariable Long id,
            @ModelAttribute("specialtyDTO")
            SpecialtyUpdateDto dto,
            Model model,
            RedirectAttributes attributes) {
        try {
            service.update(id, dto);
            attributes.addFlashAttribute("success",
                "Especialidade actualizada com sucesso!");
            return "redirect:/specialties";
        } catch (Exception e) {
            model.addAttribute("errorMessage",
                e.getMessage());
            model.addAttribute("specialtyId", id);
            model.addAttribute("activeMenu", "specialties");
            model.addAttribute("title",
                "Editar Especialidade");
            model.addAttribute("pageTitle",
                "Editar Especialidade");
            return "specialties/form";
        }
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
            RedirectAttributes attributes) {
        try {
            service.delete(id);
            attributes.addFlashAttribute("success",
                "Especialidade apagada com sucesso!");
        } catch (Exception e) {
            attributes.addFlashAttribute("error",
                e.getMessage());
        }
        return "redirect:/specialties";
    }
}