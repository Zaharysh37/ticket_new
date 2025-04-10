package by.laba1.demo.core.service.clinic;

import by.laba1.demo.api.dto.clinic.CreateClinicDto;
import by.laba1.demo.api.dto.clinic.GetClinicDto;
import by.laba1.demo.api.exception.throwble.ConflictException;
import by.laba1.demo.api.exception.ExceptionMessage;
import by.laba1.demo.api.exception.throwble.ResourceNotFoundException;
import by.laba1.demo.core.dao.chmem.CacheFactory;
import by.laba1.demo.core.dao.chmem.MyCache;
import by.laba1.demo.core.dao.clinic.ClinicRepository;
import by.laba1.demo.core.dao.doctor.DoctorRepository;
import by.laba1.demo.core.entities.Clinic;
import by.laba1.demo.core.mapper.clinic.CreateClinicMapper;
import by.laba1.demo.core.mapper.clinic.GetClinicMapper;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Transactional
@RequiredArgsConstructor
@Validated
public class ClinicService {
    private final ClinicRepository clinicRepository;
    private final DoctorRepository doctorRepository;
    private final CreateClinicMapper createClinicMapper;
    private final GetClinicMapper getClinicMapper;
    private final HelperClinicService helperClinicService;
    private final CacheFactory cacheFactory;
    private MyCache<Long, GetClinicDto> clinicMyCache;

    @PostConstruct
    public void init() {
        this.clinicMyCache = cacheFactory.createCache(
            "clinicCache",
            10,
            120_000
        );
    }

    public GetClinicDto create(@Valid CreateClinicDto dto) {
        Clinic clinic = createClinicMapper.toEntity(dto);
        Clinic savedClinic = clinicRepository.save(clinic);
        clinicMyCache.clear();
        return getClinicMapper.toDto(savedClinic);
    }

    @Transactional(readOnly = true)
    public List<GetClinicDto> getAll() {
        return getClinicMapper.toDtos(clinicRepository.findAll());
    }

    @Transactional(readOnly = true)
    public GetClinicDto getById(Long id) {
        return clinicMyCache.get(id, () -> {
            Clinic clinic = clinicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    ExceptionMessage.ENTITY_NOT_FOUND.format(id)
                ));
            return getClinicMapper.toDto(clinic);
        });
    }

    public GetClinicDto update(Long id, @Valid CreateClinicDto dto) {
        Clinic clinic = clinicRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                ExceptionMessage.ENTITY_NOT_FOUND.format(id)
            ));

        createClinicMapper.merge(clinic, dto);
        helperClinicService.updateDoctorsAndRemoveAppointments(clinic, dto.getDoctorIds());

        clinicMyCache.clear();
        return getClinicMapper.toDto(clinicRepository.save(clinic));
    }

    public void delete(Long id) {
        Clinic clinic = clinicRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                ExceptionMessage.ENTITY_NOT_FOUND.format(id)
            ));

        if (!clinic.getAppointments().isEmpty()) {
            throw new ConflictException(ExceptionMessage.ENTITY_HAS_NECESSARY_ENTITY.getMessage());
        }

        clinicRepository.delete(clinic);
        clinicMyCache.clear();
    }
}