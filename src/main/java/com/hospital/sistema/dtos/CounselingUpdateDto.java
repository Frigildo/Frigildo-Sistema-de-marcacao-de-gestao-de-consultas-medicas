package com.hospital.sistema.dtos;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CounselingUpdateDto {

	@NotBlank(message = "Descrição é obrigatória")
	@Size(max = 500, message = "Máximo 500 caracteres")
	private String description;

	@NotNull(message = "Data é obrigatória")
	private LocalDate date;

}