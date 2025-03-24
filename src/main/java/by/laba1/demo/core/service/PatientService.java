package by.laba1.demo.core.service;

import by.laba1.demo.api.dto.patient.CreatePatientDto;
import by.laba1.demo.api.dto.patient.GetPatientDto;
import by.laba1.demo.api.error.MessageException;
import by.laba1.demo.api.error.ResourceNotFoundException;
import by.laba1.demo.core.dao.chmem.MyCache;
import by.laba1.demo.core.dao.patient.PatientRepository;
import by.laba1.demo.core.entities.Patient;
import by.laba1.demo.core.mapper.patient.CreatePatientMapper;
import by.laba1.demo.core.mapper.patient.GetPatientMapper;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PatientService {
    private final PatientRepository patientRepository;
    private final CreatePatientMapper createPatientMapper;
    private final GetPatientMapper getPatientMapper;
    private final MyCache<Long, GetPatientDto> patientMyCache = new MyCache<>(10, 120_000);

    public List<GetPatientDto> getPatientsByFilter(String name, String phoneNumber) {
        List<Patient> patients = patientRepository.findByFilters(name, phoneNumber);

        if (patients.isEmpty()) {
            throw new EntityNotFoundException(MessageException.ENTITY_WITH_CRITERIA_NOT_FOUND);
        }

        return getPatientMapper.toDtos(patients);
    }

    public GetPatientDto getPatientById(long id) {
        GetPatientDto cachedPatient = patientMyCache.get(id);
        if (cachedPatient != null) {
            return cachedPatient;
        }

        Patient patientFound = patientRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException(
                String.format(MessageException.ENTITY_WITH_ID_NOT_FOUND, id)
        ));
        GetPatientDto dto = getPatientMapper.toDto(patientFound);
        patientMyCache.put(id, dto);

        return getPatientMapper.toDto(patientFound);
    }

    public GetPatientDto createPatient(CreatePatientDto dto) {
        if (patientRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new DataIntegrityViolationException(MessageException.PHONE_NUMBER_UNIQUE + dto.getPhoneNumber());
        }

        Patient patient = createPatientMapper.toEntity(dto);
        Patient savedPatient = patientRepository.save(patient);
        return getPatientMapper.toDto(savedPatient);
    }

    public GetPatientDto updatePatient(long id, CreatePatientDto dto) {
        Patient patient = patientRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Patient not found with ID " + id));

        if (!patient.getPhoneNumber().equals(dto.getPhoneNumber()) &&
            patientRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new DataIntegrityViolationException(MessageException.PHONE_NUMBER_UNIQUE + dto.getPhoneNumber());
        }
        
        createPatientMapper.merge(patient, dto);

        Patient updatedPatient = patientRepository.save(patient);
        return getPatientMapper.toDto(updatedPatient);
    }

    public void deletePatient(long id) {
        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException(String.format(MessageException.ENTITY_WITH_ID_NOT_FOUND, id));
        }
        patientRepository.deleteById(id);
        patientMyCache.clear();
    }
}
