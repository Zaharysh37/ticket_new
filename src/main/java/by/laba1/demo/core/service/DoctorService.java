package by.laba1.demo.core.service;

import by.laba1.demo.api.dto.doctor.CreateDoctorDto;
import by.laba1.demo.api.dto.doctor.GetDoctorDto;
import by.laba1.demo.api.exception.throwble.BadRequestException;
import by.laba1.demo.api.exception.throwble.ConflictException;
import by.laba1.demo.api.exception.ExceptionMessage;
import by.laba1.demo.api.exception.throwble.ResourceNotFoundException;
import by.laba1.demo.core.dao.chmem.CacheFactory;
import by.laba1.demo.core.dao.chmem.MyCache;
import by.laba1.demo.core.dao.clinic.ClinicRepository;
import by.laba1.demo.core.dao.doctor.DoctorRepository;
import by.laba1.demo.core.entities.Doctor;
import by.laba1.demo.core.mapper.doctor.CreateDoctorMapper;
import by.laba1.demo.core.mapper.doctor.GetDoctorMapper;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Validated
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final ClinicRepository clinicRepository;
    private final CreateDoctorMapper createDoctorMapper;
    private final GetDoctorMapper getDoctorMapper;
    private final CacheFactory cacheFactory;
    private MyCache<String, List<GetDoctorDto>> doctorMyCache;

    @PostConstruct
    public void init() {
        this.doctorMyCache = cacheFactory.createCache(
            "doctorCache",
            10,
            60_000
        );
    }

    public GetDoctorDto create(@Valid CreateDoctorDto dto) {
        Doctor doctor = createDoctorMapper.toEntity(dto);
        Doctor savedDoctor = doctorRepository.save(doctor);
        doctorMyCache.clear();
        return getDoctorMapper.toDto(savedDoctor);
    }

    public List<GetDoctorDto> getAll() {
        return getDoctorMapper.toDtos(doctorRepository.findAll());
    }

    public GetDoctorDto getById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                ExceptionMessage.ENTITY_NOT_FOUND.format(id)
            ));
        return getDoctorMapper.toDto(doctor);
    }

    public List<GetDoctorDto> getByClinic(Long clinicId) { //если несуществующий
        String cacheKey = "clinic_" + clinicId;
        return doctorMyCache.get(cacheKey, () -> {
            List<Doctor> doctors = doctorRepository.findByClinicId(clinicId);
            if (doctors.isEmpty()) {
                throw new ResourceNotFoundException(
                    ExceptionMessage.ENTITY_WITH_CRITERIA_NOT_FOUND.getMessage()
                );
            }
            return getDoctorMapper.toDtos(doctors);
        });
    }

    public List<GetDoctorDto> findAvailable(LocalDateTime appointmentTime, String specialization) {
        if (specialization == null || specialization.isBlank()) {
            throw new BadRequestException(ExceptionMessage.FIELD_REQUIRED.getMessage());
        }

        String cacheKey = specialization + "_" + (appointmentTime != null ? appointmentTime.toString() : "null");

        return doctorMyCache.get(cacheKey, () -> {
            List<Doctor> doctors = doctorRepository.findAvailableDoctors(appointmentTime, specialization);
            if (doctors.isEmpty()) {
                throw new ResourceNotFoundException(ExceptionMessage.ENTITY_WITH_CRITERIA_NOT_FOUND.getMessage());
            }
            return getDoctorMapper.toDtos(doctors);
        });
    }

    public GetDoctorDto update(Long id, @Valid CreateDoctorDto dto) {
        Doctor doctor = doctorRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                ExceptionMessage.ENTITY_NOT_FOUND.format(id)
            ));

        createDoctorMapper.merge(doctor, dto);
        doctorMyCache.clear();
        return getDoctorMapper.toDto(doctorRepository.save(doctor));
    }

    @Transactional
    public void delete(Long id) {
        Doctor doctor = doctorRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                ExceptionMessage.ENTITY_NOT_FOUND.format(id)
            ));

        if (!doctor.getAppointments().isEmpty()) {
            throw new ConflictException(ExceptionMessage.ENTITY_HAS_NECESSARY_ENTITY.getMessage());
        }

        clinicRepository.findByDoctorsContaining(doctor)
            .forEach(clinic -> clinic.getDoctors().remove(doctor));

        doctorRepository.delete(doctor);
        doctorMyCache.clear();
    }
}
