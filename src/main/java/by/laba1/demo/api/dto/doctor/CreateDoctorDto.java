package by.laba1.demo.api.dto.doctor;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateDoctorDto {
    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "specialization is required")
    private String specialization;
}
