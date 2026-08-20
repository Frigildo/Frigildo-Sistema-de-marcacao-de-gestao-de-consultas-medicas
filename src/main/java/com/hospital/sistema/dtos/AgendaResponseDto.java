package com.hospital.sistema.dtos;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class AgendaResponseDto {

    private Long id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean available;
    private Long doctorId;
    private String doctorName;
    private String doctorSpecialty;
}