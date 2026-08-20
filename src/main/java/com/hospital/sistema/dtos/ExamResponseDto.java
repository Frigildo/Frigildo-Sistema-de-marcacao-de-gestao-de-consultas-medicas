package com.hospital.sistema.dtos;

import java.time.LocalDate;

import com.hospital.sistema.enums.ExamType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExamResponseDto {

	private Long id;
	private ExamType type;
	private String result;
	private LocalDate date;
	private String fileName;
	private String filePath;
	private String fileType;

	// Dados do Atendimento
	private Long attendanceId;
	private String patientName;
	private String doctorName;
}