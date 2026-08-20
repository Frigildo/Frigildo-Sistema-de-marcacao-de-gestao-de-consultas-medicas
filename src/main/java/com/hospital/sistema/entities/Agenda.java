package com.hospital.sistema.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "tb_agenda")
@Getter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Agenda implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull
    @Column(nullable = false)
    private LocalDate date;

    @NotNull
    @Column(nullable = false)
    private LocalTime startTime;

    @NotNull
    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private boolean available = true;
    // ← true = disponível
    // ← false = já marcado

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id",
                nullable = false)
    private Doctor doctor;

    public Agenda(LocalDate date,
                  LocalTime startTime,
                  LocalTime endTime,
                  Doctor doctor) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.doctor = doctor;
        this.available = true;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }
}