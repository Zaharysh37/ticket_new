package by.laba1.demo.core.service.Appointment;

import by.laba1.demo.api.exception.throwble.ConflictException;
import by.laba1.demo.api.exception.ExceptionMessage;
import by.laba1.demo.core.dao.appointment.AppointmentRepository;
import by.laba1.demo.core.entities.Appointment;
import by.laba1.demo.core.entities.Clinic;
import by.laba1.demo.core.entities.Doctor;
import by.laba1.demo.core.entities.Patient;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HelperAppointmentService {
    private final AppointmentRepository appointmentRepository;

    public void validateAppointment(Appointment appointment) {
        LocalDateTime time = appointment.getAppointmentTime();
        Doctor doctor = appointment.getDoctor();
        Patient patient = appointment.getPatient();
        Clinic clinic = appointment.getClinic();

        if (appointmentRepository.existsByDoctorAndAppointmentTime(doctor, time)) {
            throw new ConflictException(ExceptionMessage.UNIQUE_CONSTRAINT_VIOLATION.format(
                doctor.getName(),
                time.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
            ));
        }

        if (appointmentRepository.existsByPatientAndAppointmentTime(patient, time)) {
            throw new ConflictException(ExceptionMessage.UNIQUE_CONSTRAINT_VIOLATION.format(
                patient.getName(),
                time.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
            ));
        }

        if (!clinic.getDoctors().contains(doctor)) {
            throw new ConflictException(ExceptionMessage.NO_CONNECTION_BETWEEN.format(
                doctor.getName(),
                clinic.getName())
            );
        }
    }
}
