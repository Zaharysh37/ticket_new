package by.laba1.demo.core.mapper;

import by.laba1.demo.api.exception.ExceptionMessage;
import by.laba1.demo.api.exception.throwble.ResourceNotFoundException;
import by.laba1.demo.core.dao.patient.PatientRepository;
import by.laba1.demo.core.entities.Patient;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class HelperPatientMapper {
    private final PatientRepository patientRepository;

    public HelperPatientMapper(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public Set<Patient> mapPatientIdsToDPatients(Set<Long> patientIds) {
        if (patientIds == null) {
            return Collections.emptySet();
        }
        return new HashSet<>(patientRepository.findAllById(patientIds));
    }

    public Set<Long> mapPatientsToPatientTds(Set<Patient> patients) {
        if(patients == null) {
            return Collections.emptySet();
        }
        return patients.stream().map(Patient::getId).collect(Collectors.toSet());
    }

    public Patient mapPatientIdToPatient(Long patientId) {
        return patientRepository.findById(patientId)
            .orElseThrow(() -> new ResourceNotFoundException(
                ExceptionMessage.ENTITY_NOT_FOUND.format(patientId)
            ));
    }

    public Long mapPatientToPatientId(Patient patient) {
        return (patient == null) ? null : patient.getId();
    }
}
