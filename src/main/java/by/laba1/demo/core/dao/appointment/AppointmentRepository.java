package by.laba1.demo.core.dao.appointment;

import by.laba1.demo.core.entities.Appointment;
import by.laba1.demo.core.entities.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
}
