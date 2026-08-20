package com.hospital.sistema.dtos;

import com.hospital.sistema.enums.Profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

	private Long id;
	private String email;
	private Profile profile;

}
