package by.laba1.demo.api.dto.patient;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class CreatePatientDto {
    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "phoneNumber is required")
    private String phoneNumber;
}
