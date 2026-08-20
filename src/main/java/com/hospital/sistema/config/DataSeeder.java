package com.hospital.sistema.config;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hospital.sistema.entities.Administrator;
import com.hospital.sistema.entities.Agenda;
import com.hospital.sistema.entities.Attendance;
import com.hospital.sistema.entities.Consultation;
import com.hospital.sistema.entities.Counseling;
import com.hospital.sistema.entities.Doctor;
import com.hospital.sistema.entities.Exam;
import com.hospital.sistema.entities.Patient;
import com.hospital.sistema.entities.Presence;
import com.hospital.sistema.entities.Specialty;
import com.hospital.sistema.entities.User;
import com.hospital.sistema.enums.ConsultationStatus;
import com.hospital.sistema.enums.ExamType;
import com.hospital.sistema.enums.Gender;
import com.hospital.sistema.enums.PresenceStatus;
import com.hospital.sistema.enums.Profile;
import com.hospital.sistema.repository.AdministratorRepository;
import com.hospital.sistema.repository.AgendaRepository;
import com.hospital.sistema.repository.AttendanceRepository;
import com.hospital.sistema.repository.ConsultationRepository;
import com.hospital.sistema.repository.CounselingRepository;
import com.hospital.sistema.repository.DoctorRepository;
import com.hospital.sistema.repository.ExamRepository;
import com.hospital.sistema.repository.PatientRepository;
import com.hospital.sistema.repository.PresenceRepository;
import com.hospital.sistema.repository.SpecialtyRepository;
import com.hospital.sistema.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {

	private final PasswordEncoder passwordEncoder;

	@Bean
	@org.springframework.context.annotation.Profile("test")
	public CommandLineRunner seedData(UserRepository userRepository, AdministratorRepository administratorRepository,
			DoctorRepository doctorRepository, PatientRepository patientRepository,
			SpecialtyRepository specialtyRepository, ConsultationRepository consultationRepository,
			AttendanceRepository attendanceRepository, ExamRepository examRepository,
			CounselingRepository counselingRepository, PresenceRepository presenceRepository,
			AgendaRepository agendaRepository) {

		return args -> {
			// ─────────────────────────────────────
			// USERS
			// ─────────────────────────────────────
			User userAdmin = new User("admin@hospital.com", passwordEncoder.encode("111111"), Profile.ADMINISTRADOR);
			User userDoctor1 = new User("dr.silva@hospital.com", passwordEncoder.encode("111111"), Profile.MEDICO);
			User userDoctor2 = new User("dr.costa@hospital.com", passwordEncoder.encode("111111"), Profile.MEDICO);
			User userPatient1 = new User("joao@gmail.com", passwordEncoder.encode("111111"), Profile.PACIENTE);
			User userPatient2 = new User("maria@gmail.com", passwordEncoder.encode("111111"), Profile.PACIENTE);
			User userPatient3 = new User("pedro@gmail.com", passwordEncoder.encode("111111"), Profile.PACIENTE);

			userRepository.save(userAdmin);
			userRepository.save(userDoctor1);
			userRepository.save(userDoctor2);
			userRepository.save(userPatient1);
			userRepository.save(userPatient2);
			userRepository.save(userPatient3);

			// ─────────────────────────────────────
			// SPECIALTIES
			// ─────────────────────────────────────
			Specialty generalMedicine = new Specialty("Clínica Geral", "Medicina geral e familiar");
			Specialty pediatrics = new Specialty("Pediatria", "Cuidados de saúde infantil");
			Specialty gynecology = new Specialty("Ginecologia", "Saúde da mulher");

			specialtyRepository.save(generalMedicine);
			specialtyRepository.save(pediatrics);
			specialtyRepository.save(gynecology);

			// ─────────────────────────────────────
			// ADMINISTRATORS
			// ─────────────────────────────────────
			Administrator admin = new Administrator("António Silva", userAdmin);
			admin.setEmployeeNumber("ADM-2024-001");
			administratorRepository.save(admin);

			// ─────────────────────────────────────
			// DOCTORS
			// ─────────────────────────────────────
			Doctor doctor1 = new Doctor("Dr. Carlos Silva", "841234567", userDoctor1);
			doctor1.setEmployeeNumber("MED-2024-001");
			doctor1.setSpecialty(generalMedicine);
			doctorRepository.save(doctor1);

			Doctor doctor2 = new Doctor("Dra. Ana Costa", "842345678", userDoctor2);
			doctor2.setEmployeeNumber("MED-2024-002");
			doctor2.setSpecialty(pediatrics);
			doctorRepository.save(doctor2);

			// ─────────────────────────────────────
			// PATIENTS — com patientNumber!
			// ─────────────────────────────────────
			Patient patient1 = new Patient("PAC-2024-001", // ← patientNumber
					"João Manuel", LocalDate.of(1990, 5, 15), Gender.MASCULINO, "843456789", "Rua da Paz, 123, Maputo",
					userPatient1);
			patientRepository.save(patient1);

			Patient patient2 = new Patient("PAC-2024-002", // ← patientNumber
					"Maria José", LocalDate.of(1985, 8, 22), Gender.FEMININO, "844567890",
					"Av. Julius Nyerere, 456, Maputo", userPatient2);
			patientRepository.save(patient2);

			Patient patient3 = new Patient("PAC-2024-003", // ← patientNumber
					"Pedro António", LocalDate.of(2000, 3, 10), Gender.MASCULINO, "845678901",
					"Rua dos Desportistas, 789, Maputo", userPatient3);
			patientRepository.save(patient3);

			// ─────────────────────────────────────
			// AGENDA
			// ─────────────────────────────────────
			Agenda slot1 = new Agenda(LocalDate.now().plusDays(1), LocalTime.of(9, 0), LocalTime.of(9, 30), doctor1);
			Agenda slot2 = new Agenda(LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(10, 30), doctor1);
			Agenda slot3 = new Agenda(LocalDate.now().plusDays(2), LocalTime.of(14, 0), LocalTime.of(14, 30), doctor2);
			Agenda slot4 = new Agenda(LocalDate.now().plusDays(2), LocalTime.of(15, 0), LocalTime.of(15, 30), doctor2);

			agendaRepository.save(slot1);
			agendaRepository.save(slot2);
			agendaRepository.save(slot3);
			agendaRepository.save(slot4);

			// ─────────────────────────────────────
			// CONSULTATIONS
			// ─────────────────────────────────────
			Consultation c1 = new Consultation(LocalDate.now().plusDays(1), LocalTime.of(9, 0),
					ConsultationStatus.AGENDADA, patient1, doctor1);
			Consultation c2 = new Consultation(LocalDate.now().plusDays(2), LocalTime.of(10, 30),
					ConsultationStatus.AGENDADA, patient2, doctor2);
			Consultation c3 = new Consultation(LocalDate.now().minusDays(5), LocalTime.of(14, 0),
					ConsultationStatus.CONCLUIDA, patient3, doctor1);
			Consultation c4 = new Consultation(LocalDate.now().minusDays(10), LocalTime.of(11, 0),
					ConsultationStatus.CANCELADA, patient1, doctor2);

			consultationRepository.save(c1);
			consultationRepository.save(c2);
			consultationRepository.save(c3);
			consultationRepository.save(c4);

			// ─────────────────────────────────────
			// ATTENDANCES
			// ─────────────────────────────────────
			Attendance attendance1 = new Attendance("Paciente com febre e tosse persistente",
					LocalDate.now().minusDays(5), c3);
			attendanceRepository.save(attendance1);

			// ─────────────────────────────────────
			// EXAMS
			// ─────────────────────────────────────
			Exam exam1 = new Exam(ExamType.RAIO_X, "Resultados dentro dos valores normais",
					LocalDate.now().minusDays(4), attendance1);
			Exam exam2 = new Exam(ExamType.ECOGRAFIA, "Sem alterações significativas", LocalDate.now().minusDays(4),
					attendance1);
			examRepository.save(exam1);
			examRepository.save(exam2);

			// ─────────────────────────────────────
			// COUNSELINGS
			// ─────────────────────────────────────
			Counseling counseling1 = new Counseling("Paciente aconselhado a manter dieta "
					+ "saudável, praticar exercício físico " + "regular e evitar ambientes com fumo.",
					LocalDate.now().minusDays(5), attendance1);
			counselingRepository.save(counseling1);

			// ─────────────────────────────────────
			// PRESENCES
			// ─────────────────────────────────────
			Presence presence1 = new Presence(PresenceStatus.PRESENTE, null, c1);
			presenceRepository.save(presence1);

			// ─────────────────────────────────────
			// LOG
			// ─────────────────────────────────────
			System.out.println("\n");
			System.out.println("═══════════════════════════════════════");
			System.out.println("✅  DADOS DE TESTE CARREGADOS!");
			System.out.println("═══════════════════════════════════════");
			System.out.println("👤 Admin:      admin@hospital.com / 123456");
			System.out.println("👨‍⚕️ Médico 1:   dr.silva@hospital.com / 123456");
			System.out.println("👨‍⚕️ Médico 2:   dr.costa@hospital.com / 123456");
			System.out.println("🏥 Paciente 1: joao@gmail.com / 123456");
			System.out.println("🏥 Paciente 2: maria@gmail.com / 123456");
			System.out.println("🏥 Paciente 3: pedro@gmail.com / 123456");
			System.out.println("═══════════════════════════════════════\n");
		};
	}
}