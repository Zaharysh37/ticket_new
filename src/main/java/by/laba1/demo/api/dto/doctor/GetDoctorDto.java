package by.laba1.demo.api.dto.doctor;

import lombok.Data;

@Data
public class GetDoctorDto {
    private Long id;
    private String name;
    private String specialization;
}
