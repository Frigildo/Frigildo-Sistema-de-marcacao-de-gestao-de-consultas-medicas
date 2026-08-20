package com.hospital.sistema.dtos;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.*;
import com.hospital.sistema.enums.ConsultationStatus;

@Getter
@Setter
@NoArgsConstructor
public class ConsultationResponseDto {

    private Long id;
    private LocalDate date;
    private LocalTime time;
    private ConsultationStatus status;

    // Dados do Paciente
    private Long patientId;
    private String patientName;

    // Dados do Médico
    private Long doctorId;
    private String doctorName;
}