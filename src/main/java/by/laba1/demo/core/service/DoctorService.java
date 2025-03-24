package by.laba1.demo.core.service;

import by.laba1.demo.api.dto.doctor.CreateDoctorDto;
import by.laba1.demo.api.dto.doctor.GetDoctorDto;
import by.laba1.demo.api.error.BadRequestException;
import by.laba1.demo.api.error.MessageException;
import by.laba1.demo.api.error.ResourceNotFoundException;
import by.laba1.demo.core.dao.chmem.MyCache;
import by.laba1.demo.core.dao.doctor.DoctorRepository;
import by.laba1.demo.core.entities.Doctor;
import by.laba1.demo.core.mapper.doctor.CreateDoctorMapper;
import by.laba1.demo.core.mapper.doctor.GetDoctorMapper;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final CreateDoctorMapper createDoctorMapper;
    private final GetDoctorMapper getDoctorMapper;
    private final MyCache<String, List<GetDoctorDto>> doctorMyCache = new MyCache<>(2, 60_000);

    public GetDoctorDto create(CreateDoctorDto dto) {
        try {
            Doctor doctor = createDoctorMapper.toEntity(dto);
            Doctor savedDoctor = doctorRepository.save(doctor);

            doctorMyCache.clear();

            return getDoctorMapper.toDto(savedDoctor);
        } catch (Exception e) {
            throw new BadRequestException(MessageException.UNEXPECTED_ERROR);
        }
    }

    public List<GetDoctorDto> getAll() {
        return getDoctorMapper.toDtos(doctorRepository.findAll());
    }

    public GetDoctorDto getById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                String.format(MessageException.ENTITY_WITH_ID_NOT_FOUND, id)
            ));
        return getDoctorMapper.toDto(doctor);
    }

    public List<GetDoctorDto> findAvailable(LocalDateTime appointmentTime, String specialization) {
        if (specialization == null || specialization.isBlank()) {
            throw new BadRequestException(MessageException.SPECIALIZATION_REQUIRED);
        }

        String cacheKey = specialization + "_" + (appointmentTime != null ? appointmentTime.toString() : "null");

        List<GetDoctorDto> cachedDoctors = doctorMyCache.get(cacheKey);
        if (cachedDoctors != null) {
            return cachedDoctors;
        }

        List<Doctor> doctors = doctorRepository.findAvailableDoctors(appointmentTime, specialization);
        List<GetDoctorDto> doctorDtos = getDoctorMapper.toDtos(doctors);

        doctorMyCache.put(cacheKey, doctorDtos);

        return doctorDtos;
    }

    public GetDoctorDto update(Long id, CreateDoctorDto dto) {
        try
        {
            Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() ->  new EntityNotFoundException("Doctor not found with ID " + id));

            createDoctorMapper.merge(doctor, dto);
            doctor = doctorRepository.save(doctor);

            doctorMyCache.clear();

            return getDoctorMapper.toDto(doctor);
        } catch (Exception e) {
            throw new BadRequestException(MessageException.UNEXPECTED_ERROR);
        }
    }

    public void delete(Long id) {
        if (!doctorRepository.existsById(id)) {
            throw new ResourceNotFoundException(String.format(MessageException.ENTITY_WITH_ID_NOT_FOUND, id));
        }
        doctorRepository.deleteById(id);
        doctorMyCache.clear();
    }
}
