package by.laba1.demo.service.clinic;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import by.laba1.demo.api.exception.throwble.ResourceNotFoundException;
import by.laba1.demo.core.dao.appointment.AppointmentRepository;
import by.laba1.demo.core.dao.doctor.DoctorRepository;
import by.laba1.demo.core.entities.Clinic;
import by.laba1.demo.core.entities.Doctor;
import by.laba1.demo.core.service.clinic.HelperClinicService;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HelperClinicServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private HelperClinicService helperClinicService;

    private Clinic clinic;
    private Set<Doctor> doctors;
    private final Set<Long> doctorIds = Set.of(1L, 2L);

    @BeforeEach
    void setUp() {
        clinic = new Clinic();
        Doctor doctor1 = new Doctor();
        doctor1.setId(1L);
        Doctor doctor2 = new Doctor();
        doctor2.setId(2L);
        doctors = Set.of(doctor1, doctor2);
        clinic.setDoctors(doctors);
    }

    @Test
    void updateDoctorsAndRemoveAppointments_ShouldUpdateDoctors() {
        when(doctorRepository.findAllById(doctorIds)).thenReturn(new ArrayList<>(doctors));

        helperClinicService.updateDoctorsAndRemoveAppointments(clinic, doctorIds);

        verify(doctorRepository).findAllById(doctorIds);
        verify(appointmentRepository, never()).deleteByClinicAndDoctorIn(any(), any());
    }

    @Test
    void updateDoctorsAndRemoveAppointments_WhenDoctorsNotFound_ShouldThrowException() {
        when(doctorRepository.findAllById(doctorIds)).thenReturn(List.of(new Doctor()));

        assertThrows(ResourceNotFoundException.class,
            () -> helperClinicService.updateDoctorsAndRemoveAppointments(clinic, doctorIds));
    }

    @Test
    void updateDoctorsAndRemoveAppointments_ShouldRemoveAppointmentsForRemovedDoctors() {
        Set<Long> newDoctorIds = Set.of(1L);

        when(doctorRepository.findAllById(newDoctorIds))
            .thenReturn(List.of(doctors.stream().filter(d -> d.getId() == 1L).findFirst().get()));

        helperClinicService.updateDoctorsAndRemoveAppointments(clinic, newDoctorIds);

        verify(appointmentRepository).deleteByClinicAndDoctorIn(clinic, Set.of(doctors.stream().filter(d -> d.getId() == 2L).findFirst().get()));
    }
}

