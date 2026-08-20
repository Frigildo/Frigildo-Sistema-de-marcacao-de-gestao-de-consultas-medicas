package com.hospital.sistema.entities;

import java.io.Serializable;
import java.time.LocalDate;

import com.hospital.sistema.enums.ExamType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_exam")
@Getter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Exam implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;

	@NotNull
	@Column(nullable = false)
	private ExamType type;

	@Column(length = 500)
	private String result;

	@NotNull
	@Column(nullable = false)
	private LocalDate date;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "attendance_id", nullable = false)
	private Attendance attendance;

	@Column
	private String fileName;

	@Column
	private String filePath;

	@Column
	private String fileType;

	public Exam(ExamType type, String result, LocalDate date, Attendance attendance) {
		this.type = type;
		this.result = result;
		this.date = date;
		this.attendance = attendance;
	}

	public void setType(ExamType type) {
		this.type = type;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public void setAttendance(Attendance attendance) {
		this.attendance = attendance;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

}