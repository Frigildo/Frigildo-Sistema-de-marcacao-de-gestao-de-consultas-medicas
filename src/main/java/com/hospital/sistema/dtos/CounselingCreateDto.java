package com.hospital.sistema.dtos;

import java.time.LocalDate;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class CounselingCreateDto {

    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 500, message = "Máximo 500 caracteres")
    private String description;

    @NotNull(message = "Data é obrigatória")
    private LocalDate date;

    @NotNull(message = "Atendimento é obrigatório")
    private Long attendanceId;
}