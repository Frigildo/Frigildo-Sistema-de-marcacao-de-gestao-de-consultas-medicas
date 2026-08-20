package com.hospital.sistema.service;

import org.springframework.security.crypto.password
        .PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation
        .Transactional;
import com.hospital.sistema.dtos.PatientRegisterDto;
import com.hospital.sistema.entities.*;
import com.hospital.sistema.enums.Profile;
import com.hospital.sistema.repository.*;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void register(PatientRegisterDto dto) {

        // Verifica se passwords coincidem
        if (!dto.getPassword()
                .equals(dto.getConfirmPassword())) {
            throw new RuntimeException(
                "As passwords não coincidem!");
        }

        // Verifica se email já existe
        if (userRepository
                .existsByEmail(dto.getEmail())) {
            throw new RuntimeException(
                "Este email já está registado!");
        }

        // Cria o User com perfil PACIENTE
        User user = new User(
            dto.getEmail(),
            passwordEncoder.encode(dto.getPassword()),
            Profile.PACIENTE
        );
        userRepository.save(user);

        // Cria o Patient automaticamente
        Patient patient = new Patient(
            generatePatientNumber(),
            dto.getName(),
            dto.getDateOfBirth(),
            dto.getGender(),
            dto.getPhone(),
            dto.getAddress() != null
                ? dto.getAddress() : "",
            user
        );
        patientRepository.save(patient);
    }

    private String generatePatientNumber() {
        int year = java.time.Year.now().getValue();
        long count = patientRepository.count() + 1;
        return String.format(
            "PAC-%d-%03d", year, count);
    }
}