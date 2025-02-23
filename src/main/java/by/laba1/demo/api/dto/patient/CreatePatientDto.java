package by.laba1.demo.api.dto.patient;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatePatientDto {
    @NotBlank
    private String name;

    @NotBlank
    private String email;
}
