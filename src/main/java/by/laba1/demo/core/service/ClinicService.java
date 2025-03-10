package by.laba1.demo.core.service;

import by.laba1.demo.api.dto.clinic.CreateClinicDto;
import by.laba1.demo.api.dto.clinic.GetClinicDto;
import by.laba1.demo.core.dao.appointment.AppointmentRepository;
import by.laba1.demo.core.dao.clinic.ClinicRepository;
import by.laba1.demo.core.dao.doctor.DoctorRepository;
import by.laba1.demo.core.entities.Clinic;
import by.laba1.demo.core.entities.Doctor;
import by.laba1.demo.core.entities.Patient;
import by.laba1.demo.core.mapper.clinic.CreateClinicMapper;
import by.laba1.demo.core.mapper.clinic.GetClinicMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
@RequiredArgsConstructor
public class ClinicService {
    private final ClinicRepository clinicRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final CreateClinicMapper createClinicMapper;
    private final GetClinicMapper getClinicMapper;

    public GetClinicDto create(CreateClinicDto dto) {
        Clinic clinic = createClinicMapper.toEntity(dto);
        Clinic savedClinic = clinicRepository.save(clinic);
        return getClinicMapper.toDto(savedClinic);
    }

    public List<GetClinicDto> getAll() {
        List<Clinic> clinics = clinicRepository.findAll();
        if (clinics.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Patients not found");
        }
        return getClinicMapper.toDtos(clinics);
    }

    public GetClinicDto getById(Long id) {
        Clinic clinic = clinicRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Clinic not found"));
        return getClinicMapper.toDto(clinic);
    }

    @Transactional
    public GetClinicDto update(Long id, CreateClinicDto dto) {
        Clinic clinic = clinicRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Clinic not found"));

        createClinicMapper.merge(clinic, dto);

        Clinic savedClinic = clinicRepository.save(clinic);
        return getClinicMapper.toDto(savedClinic);
    }

    public void delete(Long id) {
        clinicRepository.deleteById(id);
    }
}