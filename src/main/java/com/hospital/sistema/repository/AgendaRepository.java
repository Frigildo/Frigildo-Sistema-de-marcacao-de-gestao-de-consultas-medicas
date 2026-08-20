package com.hospital.sistema.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository
        .JpaRepository;
import com.hospital.sistema.entities.Agenda;
import com.hospital.sistema.entities.Doctor;

public interface AgendaRepository
        extends JpaRepository<Agenda, Long> {

    // Todos os horários de um médico
    List<Agenda> findByDoctor(Doctor doctor);

    // Horários disponíveis de um médico
    List<Agenda> findByDoctorAndAvailableTrue(
        Doctor doctor);

    // Horários disponíveis por data
    List<Agenda> findByDateAndAvailableTrue(
        LocalDate date);

    // Todos os horários disponíveis
    List<Agenda> findByAvailableTrue();

    // Horários de um médico numa data
    List<Agenda> findByDoctorAndDate(
        Doctor doctor, LocalDate date);
}