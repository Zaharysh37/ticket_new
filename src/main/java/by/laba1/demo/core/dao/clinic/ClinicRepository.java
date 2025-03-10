package by.laba1.demo.core.dao.clinic;

import by.laba1.demo.core.entities.Clinic;
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
}
