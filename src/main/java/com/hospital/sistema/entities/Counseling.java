package com.hospital.sistema.entities;

import java.io.Serializable;
import java.time.LocalDate;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "tb_counseling")
@Getter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Counseling implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 500)
    private String description;

    @NotNull
    @Column(nullable = false)
    private LocalDate date;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_id",
                nullable = false,
                unique = true)
    private Attendance attendance;

    public Counseling(String description,
                      LocalDate date,
                      Attendance attendance) {
        this.description = description;
        this.date = date;
        this.attendance = attendance;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setAttendance(Attendance attendance) {
        this.attendance = attendance;
    }
}