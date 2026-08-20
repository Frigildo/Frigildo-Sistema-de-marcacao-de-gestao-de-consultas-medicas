package com.hospital.sistema.dtos;

import java.time.LocalDate;

import com.hospital.sistema.enums.AttendanceStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AttendanceUpdateDto {

	@Size(max = 500, message = "Máximo 500 caracteres")
	private String observations;

	@NotNull(message = "Data é obrigatória")
	private LocalDate attendanceDate;
	private String diagnosis;
	private String medication;
	private AttendanceStatus status;
}