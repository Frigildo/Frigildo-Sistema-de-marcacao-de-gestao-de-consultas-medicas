package com.hospital.sistema.dtos;

import java.time.LocalDate;

import com.hospital.sistema.enums.Gender;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PatientRegisterDto {

	@NotBlank(message = "Nome é obrigatório")
	private String name;

	@NotBlank(message = "Email é obrigatório")
	@Email(message = "Email inválido")
	private String email;

	@NotBlank(message = "Password é obrigatória")
	@Size(min = 6, message = "Mínimo 6 caracteres")
	private String password;

	@NotBlank(message = "Confirmação é obrigatória")
	private String confirmPassword;

	@NotNull(message = "Data de nascimento é obrigatória")
	@Past(message = "Data de nascimento deve ser no passado")
	private LocalDate dateOfBirth;

	@NotNull(message = "Género é obrigatório")
	private Gender gender;

	@NotBlank(message = "Telefone é obrigatório")
	private String phone;

	private String address;
}