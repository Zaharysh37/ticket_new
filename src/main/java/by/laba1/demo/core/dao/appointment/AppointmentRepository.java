package by.laba1.demo.core.dao.appointment;

import by.laba1.demo.core.entities.Appointment;
import by.laba1.demo.core.entities.Clinic;
import by.laba1.demo.core.entities.Doctor;
import by.laba1.demo.core.entities.Patient;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    void deleteByClinicAndDoctorIn(Clinic clinic, Set<Doctor> removedDoctors);
    boolean existsByDoctorAndAppointmentTime(Doctor doctor, LocalDateTime appointmentTime);
    boolean existsByPatientAndAppointmentTime(Patient patient, LocalDateTime appointmentTime);

    @Query("SELECT a FROM Appointment a WHERE a.patient.name = :name")
    List<Appointment> findByPatientName(@Param("name") String name);
}
