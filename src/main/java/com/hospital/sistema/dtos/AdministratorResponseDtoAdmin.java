package com.hospital.sistema.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class AdministratorResponseDtoAdmin {

    private Long id;
    private String name;
    private String employeeNumber;
    private String userEmail;
}