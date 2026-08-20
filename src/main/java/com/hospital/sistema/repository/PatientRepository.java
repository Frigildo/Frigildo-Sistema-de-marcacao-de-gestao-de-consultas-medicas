package com.hospital.sistema.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.sistema.entities.Patient;
import com.hospital.sistema.entities.User;

public interface PatientRepository extends JpaRepository<Patient, Long> {

	Optional<Patient> findByUser(User user);

	List<Patient> findByNameContainingIgnoreCase(String name);

	boolean existsByUser(User user);
}