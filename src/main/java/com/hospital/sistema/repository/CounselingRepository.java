package com.hospital.sistema.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hospital.sistema.entities.Attendance;
import com.hospital.sistema.entities.Counseling;

public interface CounselingRepository extends JpaRepository<Counseling, Long> {

	Optional<Counseling> findByAttendance(Attendance attendance);

	boolean existsByAttendance(Attendance attendance);

	@Query("SELECT c FROM Counseling c " + "WHERE c.attendance.consultation" + ".patient.id = :patientId")
	List<Counseling> findByPatientId(@Param("patientId") Long patientId);
}