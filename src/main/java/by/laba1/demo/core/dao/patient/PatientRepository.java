package by.laba1.demo.core.dao.patient;

import by.laba1.demo.core.entities.Patient;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    @Query("SELECT p FROM Patient p WHERE " +
        "(:name IS NULL OR p.name = :name) AND" +
        " (:email IS NULL OR p.email = :email)")
    List<Patient> findByFilters(@Param("name") String name, @Param("email") String email);
}
