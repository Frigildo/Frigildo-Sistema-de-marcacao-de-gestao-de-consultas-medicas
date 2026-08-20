package com.hospital.sistema.dtos;

import com.hospital.sistema.enums.PresenceStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PresenceCreateDto {

	@NotNull(message = "Estado é obrigatório")
	private PresenceStatus status;

	@Size(max = 500, message = "Máximo 500 caracteres")
	private String justification;

	@NotNull(message = "Consulta é obrigatória")
	private Long consultationId;
}