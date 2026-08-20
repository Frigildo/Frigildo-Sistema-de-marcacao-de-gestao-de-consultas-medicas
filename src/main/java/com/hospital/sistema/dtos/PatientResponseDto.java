package com.hospital.sistema.dtos;

import java.time.LocalDate;

import com.hospital.sistema.enums.Gender;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PatientResponseDto {

	private Long id;
	private String name;
	private String patientNumber;
	private LocalDate dateOfBirth;
	private Gender gender;
	private String phone;
	private String address;
	private String userEmail;
}