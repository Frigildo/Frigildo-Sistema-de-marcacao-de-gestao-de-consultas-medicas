package com.hospital.sistema.dtos;

import com.hospital.sistema.enums.Profile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class UserCreateDtoAdmin {

	@NotBlank(message = "Email é obrigatório")
	@Email(message = "Email inválido")
	private String email;

	@NotBlank(message = "Password é obrigatória")
	@Size(min = 6, message = "Mínimo 6 caracteres")
	private String password;

	@NotBlank(message = "Confirmação é obrigatória")
	private String confirmPassword;

	@NotNull(message = "Perfil é obrigatório")
	private Profile profile;
}
