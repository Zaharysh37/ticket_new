package by.laba1.demo.api.dto.patient;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreatePatientDto {
    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "phoneNumber is required")
    @Pattern(regexp = "^(802[5-9]\\d{6})$",
        message = "Телефон должен быть в формате 8025xxxxxx-8029xxxxxx")
    private String phoneNumber;
}
