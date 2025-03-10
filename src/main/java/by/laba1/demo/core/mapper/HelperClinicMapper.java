package by.laba1.demo.core.mapper;

import by.laba1.demo.core.dao.clinic.ClinicRepository;
import by.laba1.demo.core.entities.Clinic;
import org.springframework.stereotype.Component;

@Component
public class HelperClinicMapper {
    private final ClinicRepository clinicRepository;

    public HelperClinicMapper(ClinicRepository clinicRepository) {
        this.clinicRepository = clinicRepository;
    }

    public Clinic mapClinicIdToClinic(Long clinicId) {
        if (clinicId == null) {
            return null;
        }
        return clinicRepository.findById(clinicId).orElse(null);
    }

    public Long mapClinicToClinicId(Clinic clinic) {
        return (clinic == null) ? null : clinic.getId();
    }
}