package by.laba1.demo.core.mapper;

import by.laba1.demo.core.dao.doctor.DoctorRepository;
import by.laba1.demo.core.entities.Doctor;
import jakarta.persistence.EntityNotFoundException;
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
        Set<Doctor> doctors = new HashSet<>(doctorRepository.findAllById(doctorIds));
        System.out.println("Найденные доктора: " + doctors);
        return doctors;
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
        if (doctorId == null) {
            return null;
        }
        return doctorRepository.findById(doctorId).orElseThrow(() -> new EntityNotFoundException("Doctor with id " + doctorId + " not found"));
    }

    public Long mapDoctorToDoctorId(Doctor doctor) {
        return (doctor == null) ? null : doctor.getId();
    }
}
