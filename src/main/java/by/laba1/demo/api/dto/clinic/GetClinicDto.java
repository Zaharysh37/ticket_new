package by.laba1.demo.api.dto.clinic;

import by.laba1.demo.api.dto.doctor.GetDoctorDto;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;


@Data
public class GetClinicDto {
    private Long id;
    private String name;
    private String address;
    private Set<GetDoctorDto> doctorDtos = new HashSet<>();
}
