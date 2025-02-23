package by.laba1.demo.api.dto.patient;

import lombok.Data;

@Data
public class GetPatientDto {
    private Long id;
    private String name;
    private String email;
}
