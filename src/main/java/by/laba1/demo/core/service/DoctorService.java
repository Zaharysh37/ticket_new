package by.laba1.demo.core.service;

import by.laba1.demo.api.dto.doctor.CreateDoctorDto;
import by.laba1.demo.api.dto.doctor.GetDoctorDto;
import by.laba1.demo.core.dao.doctor.DoctorRepository;
import by.laba1.demo.core.entities.Doctor;
import by.laba1.demo.core.mapper.doctor.CreateDoctorMapper;
import by.laba1.demo.core.mapper.doctor.GetDoctorMapper;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final CreateDoctorMapper createDoctorMapper;
    private final GetDoctorMapper getDoctorMapper;

    public GetDoctorDto create(CreateDoctorDto dto) {
        Doctor doctor = createDoctorMapper.toEntity(dto);
        Doctor savedDoctor = doctorRepository.save(doctor);
        return getDoctorMapper.toDto(savedDoctor);
    }

    public List<GetDoctorDto> getAll() {
        return getDoctorMapper.toDtos(doctorRepository.findAll());
    }

    public GetDoctorDto getById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Doctor not found"));
        return getDoctorMapper.toDto(doctor);
    }

    public GetDoctorDto update(Long id, CreateDoctorDto dto) {
        Doctor doctor = doctorRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Doctor not found"));

        createDoctorMapper.merge(doctor, dto);

        doctor = doctorRepository.save(doctor);
        return getDoctorMapper.toDto(doctor);
    }

    public void delete(Long id) {
        doctorRepository.deleteById(id);
    }
}
