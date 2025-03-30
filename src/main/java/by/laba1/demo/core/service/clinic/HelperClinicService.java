package by.laba1.demo.core.service.clinic;

import by.laba1.demo.api.exception.ExceptionMessage;
import by.laba1.demo.api.exception.throwble.ResourceNotFoundException;
import by.laba1.demo.core.dao.appointment.AppointmentRepository;
import by.laba1.demo.core.dao.doctor.DoctorRepository;
import by.laba1.demo.core.entities.Clinic;
import by.laba1.demo.core.entities.Doctor;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class HelperClinicService {
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;

    @Transactional
    void updateDoctorsAndRemoveAppointments(Clinic clinic, Set<Long> doctorIds) {
        Set<Doctor> newDoctors = new HashSet<>(doctorRepository.findAllById(doctorIds));

        if (newDoctors.size() != doctorIds.size()) {
            throw new ResourceNotFoundException(ExceptionMessage.ENTITY_WITH_CRITERIA_NOT_FOUND.getMessage());
        }

        Set<Doctor> removedDoctors = new HashSet<>(clinic.getDoctors());
        removedDoctors.removeAll(newDoctors);

        if (!removedDoctors.isEmpty()) {
            appointmentRepository.deleteByClinicAndDoctorIn(clinic, removedDoctors);
        }
    }
}

