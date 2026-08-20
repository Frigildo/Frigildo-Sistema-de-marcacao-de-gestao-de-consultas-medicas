package com.hospital.sistema.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hospital.sistema.entities.Attendance;
import com.hospital.sistema.entities.Exam;
import com.hospital.sistema.enums.ExamType;

public interface ExamRepository extends JpaRepository<Exam, Long> {

	List<Exam> findByAttendance(Attendance attendance);

	List<Exam> findByType(ExamType type);

	@Query("SELECT e FROM Exam e " + "WHERE e.attendance.consultation" + ".patient.id = :patientId")
	List<Exam> findByPatientId(@Param("patientId") Long patientId);
}