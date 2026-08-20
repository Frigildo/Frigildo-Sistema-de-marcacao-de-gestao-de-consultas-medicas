package com.hospital.sistema.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hospital.sistema.dtos.*;
import com.hospital.sistema.entities.Specialty;
import com.hospital.sistema.repository.SpecialtyRepository;
import com.hospital.sistema.service.exceptions
        .ResourceNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SpecialtyService {

    private final SpecialtyRepository repository;

    @Transactional(readOnly = true)
    public List<SpecialtyResponseDto> findAll() {
        return repository.findAll()
                .stream()
                .map(this::entityToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public SpecialtyResponseDto findById(Long id) {
        return entityToDto(repository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Especialidade não encontrada: "
                            + id)));
    }

    @Transactional(readOnly = true)
    public Specialty findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Especialidade não encontrada: "
                            + id));
    }

    @Transactional(readOnly = true)
    public List<SpecialtyResponseDto> findByName(
            String name) {
        return repository
                .findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::entityToDto)
                .toList();
    }

    @Transactional
    public void create(SpecialtyCreateDto dto) {
        if (repository.existsByName(dto.getName())) {
            throw new RuntimeException(
                "Especialidade já existe!");
        }
        Specialty specialty = new Specialty(
                dto.getName(),
                dto.getDescription()
        );
        repository.save(specialty);
    }

    @Transactional
    public void update(Long id, SpecialtyUpdateDto dto) {
        Specialty specialty = repository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Especialidade não encontrada: "
                            + id));
        specialty.setName(dto.getName());
        specialty.setDescription(dto.getDescription());
        repository.save(specialty);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(
                "Especialidade não encontrada: " + id);
        }
        repository.deleteById(id);
    }

    private SpecialtyResponseDto entityToDto(
            Specialty entity) {
        SpecialtyResponseDto dto =
            new SpecialtyResponseDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        return dto;
    }
}