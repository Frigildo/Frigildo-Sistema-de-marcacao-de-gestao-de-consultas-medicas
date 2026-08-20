package com.hospital.sistema.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.sistema.entities.Doctor;
import com.hospital.sistema.entities.User;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

	Optional<Doctor> findByUser(User user);

	List<Doctor> findByNameContainingIgnoreCase(String name);

	boolean existsByEmployeeNumber(String employeeNumber);

	boolean existsByUser(User user);
}
