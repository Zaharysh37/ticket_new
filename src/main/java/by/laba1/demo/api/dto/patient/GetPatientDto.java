package by.laba1.demo.api.dto.patient;

import by.laba1.demo.api.dto.appointment.GetAppointmentDto;
import by.laba1.demo.core.entities.Appointment;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class GetPatientDto {
    private Long id;
    private String name;
    private String PhoneNumber;
}
