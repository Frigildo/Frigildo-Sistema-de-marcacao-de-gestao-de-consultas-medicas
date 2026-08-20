package com.hospital.sistema.dtos;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class SpecialtyCreateDto {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Máximo 100 caracteres")
    private String name;

    @Size(max = 500, message = "Máximo 500 caracteres")
    private String description;
}