package by.laba1.demo.core.dao.doctor;

import by.laba1.demo.core.entities.Doctor;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    @Query("""
        SELECT d FROM Doctor AS d
        WHERE d.specialization = :specialization
        AND  NOT EXISTS (
        SELECT a
        FROM Appointment AS a
        WHERE a.doctor = d
        AND a.appointmentTime = :appointmentTime 
        )
    """)
    List<Doctor> findAvailableDoctors(
        @Param("appointmentTime") LocalDateTime appointmentTime,
        @Param("specialization") String specialization
    );

    @Query("SELECT d FROM Doctor d JOIN d.clinics c WHERE c.id = :clinicId")
    List<Doctor> findByClinicId(@Param("clinicId") Long clinicId);
}

/*
@Query(value = """
    SELECT d.* FROM doctors d
    WHERE d.specialization = :specialization
    WHERE d.id NOT IN (
    SELECT a.doctor_id FROM appointments a
    WHERE a.appointment_time = :appointmentTime
    )
""", nativeQuery = true)
 */

/*
SELECT d.*
FROM doctors d
LEFT JOIN appointments a ON a.doctor_id = d.id
    AND a.appointment_time = :appointmentTime
WHERE d.specialization = :specialization
AND a.id IS NULL
 */