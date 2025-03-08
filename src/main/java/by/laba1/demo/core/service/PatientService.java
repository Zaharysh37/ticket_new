package by.laba1.demo.core.service;

import by.laba1.demo.api.dto.patient.CreatePatientDto;
import by.laba1.demo.api.dto.patient.GetPatientDto;
import by.laba1.demo.core.dao.patient.PatientRepository;
import by.laba1.demo.core.entities.Patient;
import by.laba1.demo.core.mapper.patient.CreatePatientMapper;
import by.laba1.demo.core.mapper.patient.GetPatientMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PatientService {
    private final PatientRepository patientRepository;
    private final CreatePatientMapper createPatientMapper;
    private final GetPatientMapper getPatientMapper;

    public List<GetPatientDto> getPatientsByFilter(String name, String phoneNumber) {
        List<Patient> patients = patientRepository.findByFilters(name, phoneNumber);

        if (patients.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Patients not found");
        }

        return getPatientMapper.toDtos(patients);
    }

    public GetPatientDto getPatientById(long id) {
        return getPatientMapper.toDto(
            patientRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patients not found")
            )
        );
    }

    public GetPatientDto createPatient(CreatePatientDto dto) {
        if (patientRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Patient with this email already exist: " + dto.getPhoneNumber());
        }

        Patient patient = createPatientMapper.toEntity(dto);
        Patient savedPatient = patientRepository.save(patient);
        return getPatientMapper.toDto(savedPatient);
    }

    public GetPatientDto updatePatient(long id, CreatePatientDto dto) {
        Patient patient = patientRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Patient with this id " + id + " not found"));

        if (!patient.getPhoneNumber().equals(dto.getPhoneNumber()) &&
            patientRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Patient with this phone number already exist: " + dto.getPhoneNumber());
        }
        
        createPatientMapper.merge(patient, dto);

        Patient updatedPatient = patientRepository.save(patient);
        return getPatientMapper.toDto(updatedPatient);
    }

    public void deletePatient(long id) {
        patientRepository.deleteById(id);
    }
}
