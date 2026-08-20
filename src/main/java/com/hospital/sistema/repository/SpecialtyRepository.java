package com.hospital.sistema.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.sistema.entities.Specialty;

public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {

	List<Specialty> findByNameContainingIgnoreCase(String name);

	boolean existsByName(String name);
}