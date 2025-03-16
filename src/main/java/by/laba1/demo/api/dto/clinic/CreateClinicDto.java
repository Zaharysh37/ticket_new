package by.laba1.demo.api.dto.clinic;

import jakarta.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;

@Data
public class CreateClinicDto {
    @NotBlank
    private String name;

    @NotBlank
    private String address;

    private Set<Long> doctorIds = new HashSet<>();
}
