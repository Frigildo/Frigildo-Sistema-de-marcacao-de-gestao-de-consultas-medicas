package com.hospital.sistema.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdministratorCreateDtoAdmin {

	@NotBlank(message = "Nome é obrigatório")
	@Size(max = 100, message = "Máximo 100 caracteres")
	private String name;

	@NotNull(message = "Utilizador é obrigatório")
	private Long userId;
}