package com.hospital.sistema.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorUpdateDtoAdmin {

	@NotBlank(message = "Nome é obrigatório")
	@Size(max = 100, message = "Máximo 100 caracteres")
	private String name;

	@NotBlank(message = "Número de celular é obrigatório")
	@Pattern(regexp = "^[0-9]{9}$", message = "Número inválido — 9 dígitos")
	private String cellPhone;

}