package by.laba1.demo.serviceLayer;

import by.laba1.demo.dataAccessLayer.TicketsDataAccessObject;
import by.laba1.demo.entityLayer.Ticket;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class TicketService {
    private final TicketsDataAccessObject ticketsDataAccessObject;

    public TicketService(TicketsDataAccessObject ticketsDataAccessObject) {
        this.ticketsDataAccessObject = ticketsDataAccessObject;
    }

    public List<Ticket> getAllTickets() {
        return ticketsDataAccessObject.findAll();
    }

    public List<Ticket> getAllTickets(String specialization, String medicalInstitution) {
        return ticketsDataAccessObject.getAllTickets(specialization, medicalInstitution);
    }

    public Optional<Ticket> getTicketById(Long id) {
        return ticketsDataAccessObject.findById(id);
    }
}
