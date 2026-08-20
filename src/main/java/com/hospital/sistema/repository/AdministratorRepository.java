package com.hospital.sistema.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.sistema.entities.Administrator;
import com.hospital.sistema.entities.User;

public interface AdministratorRepository extends JpaRepository<Administrator, Long> {

	Optional<Administrator> findByUser(User user);

	boolean existsByEmployeeNumber(String employeeNumber);

	List<Administrator> findByNameContainingIgnoreCase(String name);
}