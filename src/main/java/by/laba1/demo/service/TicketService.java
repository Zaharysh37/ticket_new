package by.laba1.demo.service;

import by.laba1.demo.data.TicketsDataAccessObject;
import by.laba1.demo.entity.Ticket;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class TicketService {
    private final TicketsDataAccessObject ticketsDataAccessObject;

    public TicketService(TicketsDataAccessObject ticketsDataAccessObject) {
        this.ticketsDataAccessObject = ticketsDataAccessObject;
    }

    public List<Ticket> getTicketsByFilter(String specialization, String medicalInstitution) {
        return ticketsDataAccessObject.getTicketsByFilter(specialization, medicalInstitution);
    }

    public Optional<Ticket> getTicketById(Long id) {
        return ticketsDataAccessObject.findById(id);
    }
}
