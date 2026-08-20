package com.hospital.sistema.dtos;

import java.time.LocalDate;

import com.hospital.sistema.enums.Gender;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PatientCreateDtoAdmin {

	@NotBlank(message = "Nome é obrigatório")
	private String name;

	@NotNull(message = "Data de nascimento é obrigatória")
	@Past(message = "Data de nascimento deve ser no passado")
	private LocalDate dateOfBirth;

	@NotNull(message = "Género é obrigatório")
	private Gender gender;

	@NotBlank(message = "Telefone é obrigatório")
	private String phone;

	private String address;

	@NotNull(message = "Utilizador é obrigatório")
	private Long userId;
}