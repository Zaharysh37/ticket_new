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
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PatientService {
    private final PatientRepository patientRepository;
    private final CreatePatientMapper createPatientMapper;
    private final GetPatientMapper getPatientMapper;

    public List<GetPatientDto> getPatientsByFilter(String name, String email) {
        return getPatientMapper.toDtos(
            patientRepository.findByFilters(name, email)
        );
    }

    public GetPatientDto getPatientById(long id) {
        return getPatientMapper.toDto(
            patientRepository.findById(id).orElse(null)
        );
    }

    public GetPatientDto createPatient(CreatePatientDto dto) {
        Patient patient = createPatientMapper.toEntity(dto);
        Patient savedPatient = patientRepository.save(patient);
        return getPatientMapper.toDto(
            savedPatient
        );
    }

    public void deletePatient(long id) {
        patientRepository.deleteById(id);
    }
}
