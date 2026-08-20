package com.hospital.sistema.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.hospital.sistema.service.exceptions.ResourceNotFoundException;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public String resourceNotFound(
            ResourceNotFoundException e,
            Model model) {

        model.addAttribute(
                "error",
                e.getMessage()
        );

        return "error/404";
    }

}