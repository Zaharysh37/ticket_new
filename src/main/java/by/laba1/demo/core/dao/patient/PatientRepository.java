package by.laba1.demo.core.dao.patient;

import by.laba1.demo.core.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends
    JpaRepository<Patient, Long>, PatientRepositoryCustom {
}
