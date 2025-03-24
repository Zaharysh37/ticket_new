package by.laba1.demo.api.dto.appointment;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CreateAppointmentDto {
    @NotNull(message = "patientId is required")
    @Positive(message = "patientId must be positive")
    private Long patientId;

    @NotNull(message = "doctorId is required")
    @Positive(message = "doctorId must be positive")
    private Long doctorId;

    @NotNull(message = "clinicId is required")
    @Positive(message = "clinicId must be positive")
    private Long clinicId;

    @NotNull(message = "appointmentTime is required")
    @Future(message = "appointmentTime must be in the future")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime appointmentTime;
}
