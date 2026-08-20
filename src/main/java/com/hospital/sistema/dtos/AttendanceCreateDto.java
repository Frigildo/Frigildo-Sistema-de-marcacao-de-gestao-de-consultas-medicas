package com.hospital.sistema.dtos;

import java.time.LocalDate;

import com.hospital.sistema.enums.ExamType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AttendanceCreateDto {

	@Size(max = 500, message = "Máximo 500 caracteres")
	private String observations;

	@NotNull(message = "Data é obrigatória")
	private LocalDate attendanceDate;

	@NotNull(message = "Consulta é obrigatória")
	private Long consultationId;

	@NotNull(message = "Tipo de exame é obrigatório")
	private ExamType examType;

	@NotNull(message = "Data é obrigatória")
	private LocalDate examDate;

	private String diagnosis;
	private String medication;

	private boolean needsExam = false;
}