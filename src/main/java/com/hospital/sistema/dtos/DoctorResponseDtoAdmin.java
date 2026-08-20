package com.hospital.sistema.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DoctorResponseDtoAdmin {

	private Long id;
	private String name;
	private String employeeNumber;
	private String cellPhone;
	private Long userId;
	private String userEmail;
	private String specialtyName;
}