package com.hospital.sistema.dtos;

import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class CounselingResponseDto {

    private Long id;
    private String description;
    private LocalDate date;

    // Dados do Atendimento
    private Long attendanceId;
    private String patientName;
    private String doctorName;
}