package by.laba1.demo.service.appointment;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import by.laba1.demo.api.exception.throwble.ConflictException;
import by.laba1.demo.core.dao.appointment.AppointmentRepository;
import by.laba1.demo.core.entities.Appointment;
import by.laba1.demo.core.entities.Clinic;
import by.laba1.demo.core.entities.Doctor;
import by.laba1.demo.core.entities.Patient;
import by.laba1.demo.core.service.appointment.HelperAppointmentService;
import java.time.LocalDateTime;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HelperAppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private HelperAppointmentService helperAppointmentService;

    private Appointment appointment;
    private Doctor doctor;
    private Patient patient;
    private Clinic clinic;
    private final LocalDateTime appointmentTime = LocalDateTime.now().plusHours(1);

    @BeforeEach
    void setUp() {
        doctor = new Doctor();
        doctor.setId(1L);
        doctor.setName("Dr. Smith");

        patient = new Patient();
        patient.setId(1L);
        patient.setName("John Doe");

        clinic = new Clinic();
        clinic.setId(1L);
        clinic.setName("City Clinic");
        clinic.setDoctors(Set.of(doctor));

        appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setClinic(clinic);
        appointment.setAppointmentTime(appointmentTime);
    }

    @Test
    void validateAppointment_ShouldPassValidation() {
        when(appointmentRepository.existsByDoctorAndAppointmentTime(doctor, appointmentTime))
            .thenReturn(false);
        when(appointmentRepository.existsByPatientAndAppointmentTime(patient, appointmentTime))
            .thenReturn(false);

        assertDoesNotThrow(() -> helperAppointmentService.validateAppointment(appointment));
    }

    @Test
    void validateAppointment_WhenDoctorBusy_ShouldThrowException() {
        when(appointmentRepository.existsByDoctorAndAppointmentTime(doctor, appointmentTime))
            .thenReturn(true);

        assertThrows(ConflictException.class,
            () -> helperAppointmentService.validateAppointment(appointment));
    }

    @Test
    void validateAppointment_WhenPatientHasAppointment_ShouldThrowException() {
        when(appointmentRepository.existsByPatientAndAppointmentTime(patient, appointmentTime))
            .thenReturn(true);

        assertThrows(ConflictException.class,
            () -> helperAppointmentService.validateAppointment(appointment));
    }

    @Test
    void validateAppointment_WhenDoctorNotInClinic_ShouldThrowException() {
        clinic.setDoctors(Set.of()); // Убираем врача из клиники

        assertThrows(ConflictException.class,
            () -> helperAppointmentService.validateAppointment(appointment));
    }
}