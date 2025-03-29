package by.laba1.demo.core.mapper;

import by.laba1.demo.api.exception.ExceptionMessage;
import by.laba1.demo.api.exception.throwble.ResourceNotFoundException;
import by.laba1.demo.core.dao.doctor.DoctorRepository;
import by.laba1.demo.core.entities.Doctor;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class HelperDoctorMapper {
    private final DoctorRepository doctorRepository;

    public HelperDoctorMapper(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public Set<Doctor> mapDoctorIdsToDoctors(Set<Long> doctorIds) {
        if (doctorIds == null) {
            return Collections.emptySet();
        }
        return new HashSet<>(doctorRepository.findAllById(doctorIds));
    }

    public Set<Long> mapDoctorsToDoctorTds(Set<Doctor> doctors) {
        if(doctors == null) {
            return Collections.emptySet();
        }
        return doctors.stream()
            .map(Doctor::getId)
            .collect(Collectors.toSet());
    }

    public Doctor mapDoctorIdToDoctor(Long doctorId) {
        return doctorRepository.findById(doctorId)
            .orElseThrow(() -> new ResourceNotFoundException(
                ExceptionMessage.ENTITY_NOT_FOUND.format(doctorId)
            ));
    }

    public Long mapDoctorToDoctorId(Doctor doctor) {
        return (doctor == null) ? null : doctor.getId();
    }
}
