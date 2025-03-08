package by.laba1.demo.api.dto.appointment;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CreateAppointmentDto {
    @NotBlank
    private Long patientId;

    @NotBlank
    private Long doctorId;

    private LocalDateTime appointmentTime;
}
