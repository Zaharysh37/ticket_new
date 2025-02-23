package by.laba1.demo.core.entities;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Ticket {
    private Long id;
    private String name;
    private String specialization;
    private String medicalInstitution;
    private LocalDateTime appointmentTime;
}
