package com.hospital.sistema.dtos;

import java.time.LocalDate;
import java.time.LocalTime;

import com.hospital.sistema.enums.ConsultationStatus;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ConsultationCreateDto {

	@NotNull(message = "Data é obrigatória")
	@FutureOrPresent(message = "Data não pode ser no passado")
	private LocalDate date;

	@NotNull(message = "Hora é obrigatória")
	private LocalTime time;

	@NotNull(message = "Estado é obrigatório")
	private ConsultationStatus status;

	@NotNull(message = "Paciente é obrigatório")
	private Long patientId;

	@NotNull(message = "Médico é obrigatório")
	private Long doctorId;

	@NotNull(message = "Agenda é obrigatória")
	private Long agendaId;
}