package com.hospital.sistema.controller;

import java.security.Principal;
import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.hospital.sistema.dtos.AgendaResponseDto;
import com.hospital.sistema.entities.Doctor;
import com.hospital.sistema.entities.Patient;
import com.hospital.sistema.entities.User;
import com.hospital.sistema.enums.ConsultationStatus;
import com.hospital.sistema.repository.DoctorRepository;
import com.hospital.sistema.repository.PatientRepository;
import com.hospital.sistema.repository.UserRepository;
import com.hospital.sistema.service.AgendaService;
import com.hospital.sistema.service.AttendanceService;
import com.hospital.sistema.service.ConsultationService;
import com.hospital.sistema.service.CounselingService;
import com.hospital.sistema.service.DoctorService;
import com.hospital.sistema.service.ExamService;
import com.hospital.sistema.service.PatientService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class DashboardController {

	private final PatientService patientService;
	private final DoctorService doctorService;
	private final ConsultationService consultationService;
	private final AttendanceService attendanceService;
	private final ExamService examService;
	private final CounselingService counselingService;
	private final AgendaService agendaService;
	private final UserRepository userRepository;
	private final DoctorRepository doctorRepository;
	private final PatientRepository patientRepository;

	@GetMapping("/dashboard")
	public String dashboard(Principal principal, Model model) {
		if (principal == null) {
			return "redirect:/login";
		}

		User user = userRepository.findByEmail(principal.getName()).orElse(null);

		if (user == null) {
			return "redirect:/login";
		}

		model.addAttribute("userRole", user.getProfile().name());

		switch (user.getProfile()) {
		case ADMINISTRADOR:
			return adminDashboard(model);
		case MEDICO:
			return doctorDashboard(user, model);
		case PACIENTE:
			return patientDashboard(user, model);
		default:
			return "redirect:/login";
		}
	}

	@GetMapping("/")
	public String index() {
		return "redirect:/dashboard";
	}

	// ─────────────────────────────────────
	// ADMIN
	// ─────────────────────────────────────
	private String adminDashboard(Model model) {
		model.addAttribute("totalPatients", patientService.findAll().size());
		model.addAttribute("totalDoctors", doctorService.findAll().size());
		model.addAttribute("totalAttendances", attendanceService.findAll().size());
		model.addAttribute("consultationsToday", consultationService.findByDate(LocalDate.now()).size());
		model.addAttribute("activeMenu", "dashboard");
		model.addAttribute("title", "Dashboard");
		model.addAttribute("pageTitle", "Dashboard");
		return "dashboard/admin";
	}

	// ─────────────────────────────────────
	// MÉDICO
	// ─────────────────────────────────────
	private String doctorDashboard(User user, Model model) {
		Doctor doctor = doctorRepository.findByUser(user).orElse(null);

		if (doctor == null) {
			model.addAttribute("doctorName", user.getEmail());
			model.addAttribute("myConsultations", 0);
			model.addAttribute("myConsultationsAgendadas", 0L);
			model.addAttribute("myAttendances", 0);
			model.addAttribute("myAvailableSlots", 0L);
			model.addAttribute("activeMenu", "dashboard");
			model.addAttribute("title", "Dashboard");
			model.addAttribute("pageTitle", "Dashboard");
			return "dashboard/doctor";
		}

		long agendadas = consultationService.findByDoctor(doctor.getId()).stream()
				.filter(c -> c.getStatus() == ConsultationStatus.AGENDADA).count();

		long availableSlots = agendaService.findByDoctor(doctor.getId()).stream().filter(AgendaResponseDto::isAvailable)
				.count();

		model.addAttribute("doctorName", doctor.getName());
		model.addAttribute("myConsultations", consultationService.findByDoctor(doctor.getId()).size());
		model.addAttribute("myConsultationsAgendadas", agendadas);
		model.addAttribute("myAttendances", attendanceService.findByDoctor(doctor.getId()).size());
		model.addAttribute("myAvailableSlots", availableSlots);
		model.addAttribute("activeMenu", "dashboard");
		model.addAttribute("title", "Dashboard");
		model.addAttribute("pageTitle", "Dashboard");
		return "dashboard/doctor";
	}

	// ─────────────────────────────────────
	// PACIENTE
	// ─────────────────────────────────────
	private String patientDashboard(User user, Model model) {
		Patient patient = patientRepository.findByUser(user).orElse(null);

		if (patient == null) {
			model.addAttribute("patientName", user.getEmail());
			model.addAttribute("myConsultations", 0);
			model.addAttribute("myConsultationsAgendadas", 0L);
			model.addAttribute("myExams", 0);
			model.addAttribute("availableSlots", 0);
			model.addAttribute("activeMenu", "dashboard");
			model.addAttribute("title", "Dashboard");
			model.addAttribute("pageTitle", "Dashboard");
			return "dashboard/patient";
		}

		long agendadas = consultationService.findByPatient(patient.getId()).stream()
				.filter(c -> c.getStatus() == ConsultationStatus.AGENDADA).count();

		model.addAttribute("patientName", patient.getName());
		model.addAttribute("myConsultations", consultationService.findByPatient(patient.getId()).size());
		model.addAttribute("myConsultationsAgendadas", agendadas);
		model.addAttribute("myExams", examService.findByPatient(patient.getId()).size());
		model.addAttribute("availableSlots", agendaService.findAvailable().size());
		model.addAttribute("activeMenu", "dashboard");
		model.addAttribute("title", "Dashboard");
		model.addAttribute("pageTitle", "Dashboard");
		return "dashboard/patient";
	}
}