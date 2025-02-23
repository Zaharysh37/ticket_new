package by.laba1.demo.core.dao.patient;

import by.laba1.demo.core.entities.Patient;
import java.util.List;

public interface PatientRepositoryCustom {
    List<Patient> findByFilters(String name, String email);
}
