package by.laba1.demo.controller;

import by.laba1.demo.entity.Ticket;
import by.laba1.demo.service.TicketService;
import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ticket")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    // GET-запрос с Query Parameters (например: /tickets?specialization=Терапевт)
    @GetMapping
    public List<Ticket> getTicketsByFilter(
        @RequestParam(required = false) String specialization,
        @RequestParam(required = false) String medicalInstitution
    ) {
        return ticketService.getTicketsByFilter(specialization, medicalInstitution);
    }

    // GET с Path Parameters (например: /patients/1)
    @GetMapping("/{id}")
    public Optional<Ticket> getTicketById(@PathVariable Long id) {
        return ticketService.getTicketById(id);
    }
}
