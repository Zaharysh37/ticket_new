package by.laba1.demo.core.dao.clinic;

import by.laba1.demo.core.entities.Clinic;
import by.laba1.demo.core.entities.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClinicRepository extends JpaRepository<Clinic, Long> {
}
