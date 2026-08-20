package com.hospital.sistema.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.sistema.entities.Consultation;
import com.hospital.sistema.entities.Presence;
import com.hospital.sistema.enums.PresenceStatus;

public interface PresenceRepository extends JpaRepository<Presence, Long> {

	List<Presence> findByStatus(PresenceStatus status);

	boolean existsByConsultation(Consultation consultation);
}