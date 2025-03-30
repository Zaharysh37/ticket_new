package by.laba1.demo.core.service;

import by.laba1.demo.api.dto.patient.CreatePatientDto;
import by.laba1.demo.api.dto.patient.GetPatientDto;
import by.laba1.demo.api.exception.ExceptionMessage;
import by.laba1.demo.api.exception.throwble.ResourceNotFoundException;
import by.laba1.demo.core.dao.chmem.CacheFactory;
import by.laba1.demo.core.dao.chmem.MyCache;
import by.laba1.demo.core.dao.patient.PatientRepository;
import by.laba1.demo.core.entities.Patient;
import by.laba1.demo.core.mapper.patient.CreatePatientMapper;
import by.laba1.demo.core.mapper.patient.GetPatientMapper;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PatientService {
    private final PatientRepository patientRepository;
    private final CreatePatientMapper createPatientMapper;
    private final GetPatientMapper getPatientMapper;
    private final CacheFactory cacheFactory;
    private MyCache<Long, GetPatientDto> patientMyCache;

    @PostConstruct
    public void init() {
        this.patientMyCache = cacheFactory.createCache(
            "patientCache",
            10,
            120_000
        );
    }

    public List<GetPatientDto> getPatientsByFilter(String name, String phoneNumber) {
        List<Patient> patients = patientRepository.findByFilters(name, phoneNumber);

        if (patients.isEmpty()) {
            throw new ResourceNotFoundException(ExceptionMessage.ENTITY_WITH_CRITERIA_NOT_FOUND.getMessage());
        }

        return getPatientMapper.toDtos(patients);
    }

    public GetPatientDto getPatientById(long id) {
        return patientMyCache.get(id, () -> {
            Patient patientFound = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    ExceptionMessage.ENTITY_NOT_FOUND.format(id)));
            return getPatientMapper.toDto(patientFound);
        });
    }

    public GetPatientDto createPatient(CreatePatientDto dto) {
        Patient patient = createPatientMapper.toEntity(dto);
        Patient savedPatient = patientRepository.save(patient);
        return getPatientMapper.toDto(savedPatient);
    }

    public GetPatientDto updatePatient(long id, CreatePatientDto dto) {
        Patient patient = patientRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                ExceptionMessage.ENTITY_NOT_FOUND.format(id)));

        createPatientMapper.merge(patient, dto);
        Patient updatedPatient = patientRepository.save(patient);
        patientMyCache.clear();
        return getPatientMapper.toDto(updatedPatient);
    }

    public void deletePatient(long id) {
        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException(ExceptionMessage.ENTITY_NOT_FOUND.format(id));
        }
        patientRepository.deleteById(id);
        patientMyCache.clear();
    }
}