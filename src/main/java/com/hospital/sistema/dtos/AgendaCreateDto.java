package com.hospital.sistema.dtos;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AgendaCreateDto {

	@NotNull(message = "Data é obrigatória")
	@FutureOrPresent(message = "Data não pode ser no passado")
	private LocalDate date;

	@NotNull(message = "Hora de início é obrigatória")
	private LocalTime startTime;

	@NotNull(message = "Hora de fim é obrigatória")
	private LocalTime endTime;

}