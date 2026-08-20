package com.hospital.sistema.dtos;

import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.validation.constraints.*;
import lombok.*;
import com.hospital.sistema.enums.ConsultationStatus;

@Getter
@Setter
@NoArgsConstructor
public class ConsultationUpdateDto {

    @NotNull(message = "Data é obrigatória")
    private LocalDate date;

    @NotNull(message = "Hora é obrigatória")
    private LocalTime time;

    @NotNull(message = "Estado é obrigatório")
    private ConsultationStatus status;

    // ← patientId e doctorId não mudam no update
}