package by.laba1.demo.dataAccessLayer;

import by.laba1.demo.entityLayer.Ticket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class TicketsDataAccessObject {
    private final List<Ticket> tickets = new ArrayList<>();

    public TicketsDataAccessObject() {
        long nextId = 0L;
        tickets.add(
            new Ticket(++nextId,
                "Иван Иванов Львович",
                "Врач-хирург",
                "1-я городская детская поликлиника г. Минска",
                LocalDateTime.of(2025, 2, 20, 14, 30))
        );
        tickets.add(
            new Ticket(++nextId,
                "Мария Петрова Попова",
                "Врач-невролог",
                "5-я городская клиническая больница г. Минска",
                LocalDateTime.of(2025, 2, 20, 15, 45))
        );
    }

    public List<Ticket> findAll() {
        return new ArrayList<>(tickets);
    }

    public Optional<Ticket> findById(Long id) {
        return tickets.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    public List<Ticket> getAllTickets(String specialization, String medicalInstitution) {
        return tickets.stream()
            .filter(t -> specialization == null
                || t.getSpecialization().equalsIgnoreCase(specialization))
            .filter(t -> medicalInstitution == null
                || t.getMedicalInstitution().equalsIgnoreCase(medicalInstitution))
            .collect(Collectors.toList());
    }
}