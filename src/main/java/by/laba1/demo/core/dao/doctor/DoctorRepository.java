package by.laba1.demo.core.dao.doctor;

import by.laba1.demo.core.entities.Doctor;
import by.laba1.demo.core.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}
