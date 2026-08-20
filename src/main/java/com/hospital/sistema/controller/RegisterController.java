package com.hospital.sistema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support
        .RedirectAttributes;
import com.hospital.sistema.dtos.PatientRegisterDto;
import com.hospital.sistema.enums.Gender;
import com.hospital.sistema.service.RegisterService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RegisterController {

    private final RegisterService registerService;

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registerDTO",
            new PatientRegisterDto());
        model.addAttribute("genders",
            Gender.values());
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @ModelAttribute("registerDTO")
            PatientRegisterDto dto,
            Model model,
            RedirectAttributes attributes) {
        try {
            registerService.register(dto);
            attributes.addFlashAttribute("success",
                "Conta criada com sucesso! " +
                "Faça login para continuar.");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("errorMessage",
                e.getMessage());
            model.addAttribute("genders",
                Gender.values());
            model.addAttribute("registerDTO", dto);
            return "register";
        }
    }
}