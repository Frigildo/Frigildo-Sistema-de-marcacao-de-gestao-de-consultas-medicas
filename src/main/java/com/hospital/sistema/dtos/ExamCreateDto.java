package com.hospital.sistema.dtos;

import java.time.LocalDate;

import org.springframework.web.multipart.MultipartFile;

import com.hospital.sistema.enums.ExamType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExamCreateDto {

	@NotBlank(message = "Tipo é obrigatório")
	@Size(max = 100, message = "Máximo 100 caracteres")
	private ExamType type;

	@Size(max = 500, message = "Máximo 500 caracteres")
	private String result;

	@NotNull(message = "Data é obrigatória")
	private LocalDate date;

	@NotNull(message = "Atendimento é obrigatório")
	private Long attendanceId;

	@NotNull(message = "Ficheiro é obrigatório")
	private MultipartFile file;
}