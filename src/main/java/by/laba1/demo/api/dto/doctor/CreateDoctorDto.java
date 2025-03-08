package by.laba1.demo.api.dto.doctor;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateDoctorDto {
    @NotBlank
    private String name;

    @NotBlank
    private String specialization;
}
