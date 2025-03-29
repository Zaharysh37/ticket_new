package by.laba1.demo.core.dao.clinic;

import by.laba1.demo.core.entities.Clinic;
import by.laba1.demo.core.entities.Doctor;
import io.micrometer.common.lang.NonNull;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClinicRepository extends JpaRepository<Clinic, Long> {
    @EntityGraph(attributePaths = {"doctors"})
    @NonNull
    @Override
    List<Clinic> findAll();

    @EntityGraph(attributePaths = {"doctors"})
    @NonNull
    @Override
    Optional<Clinic> findById(Long id);

    @EntityGraph(attributePaths = {"doctors"})
    List<Clinic> findByDoctorsContaining(Doctor doctor);
}

/*
@Query("""
    SELECT d FROM Doctor d
    JOIN d.clinics c
    WHERE d.specialization = :specialization
    AND c.address LIKE CONCAT('%', :clinicAddress, '%')
    AND (:appointmentTime IS NULL OR NOT EXISTS (
        SELECT a FROM Appointment a
        WHERE a.doctor = d
        AND a.appointmentTime = :appointmentTime
    ))
""")
* */

/*
@Query(value = """
    SELECT d.* FROM doctors d
    JOIN clinic_doctors cd ON cd.doctor_id = d.id
    JOIN clinics c ON cd.clinic_id = c.id
    WHERE d.specialization = :specialization
    AND c.address LIKE CONCAT('%', :clinicAddress, '%')
    AND (:appointmentTime IS NULL OR NOT EXISTS (
        SELECT 1 FROM appointments a
        WHERE a.doctor_id = d.id
        AND a.appointment_time = :appointmentTime
    ))
""", nativeQuery = true)
 */