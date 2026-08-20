package com.hospital.sistema.dtos;

import java.time.LocalDate;

import com.hospital.sistema.enums.AttendanceStatus;
import com.hospital.sistema.enums.ConsultationStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AttendanceResponseDto {

	private Long id;
	private String observations;
	private LocalDate attendanceDate;
	private String diagnosis;
	private String medication;
	private AttendanceStatus status;
	private Long consultationId;
	private String patientName;
	private String doctorName;
	private LocalDate consultationDate;
	private ConsultationStatus consultationStatus;
}