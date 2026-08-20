package com.hospital.sistema.dtos;

import com.hospital.sistema.enums.PresenceStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PresenceResponseDto {

	private Long id;
	private PresenceStatus status;
	private String justification;
	private Long consultationId;
	private String patientName;
	private String doctorName;
}