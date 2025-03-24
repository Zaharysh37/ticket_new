package by.laba1.demo.api.dto.appointment;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CreateAppointmentDto {
    @NotBlank(message = "patientId is required")
    private Long patientId;

    @NotBlank(message = "doctorId is required")
    private Long doctorId;

    @NotBlank(message = "clinicId is required")
    private Long clinicId;

    @NotBlank(message = "appointmentTime is required")
    private LocalDateTime appointmentTime;
}
