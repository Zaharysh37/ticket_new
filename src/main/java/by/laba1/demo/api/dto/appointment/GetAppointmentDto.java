package by.laba1.demo.api.dto.appointment;

import by.laba1.demo.api.dto.doctor.GetDoctorDto;
import by.laba1.demo.api.dto.patient.GetPatientDto;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class GetAppointmentDto {
    private Long id;
    private GetPatientDto patientDto;
    private GetDoctorDto doctorDto;
    private LocalDateTime appointmentTime;
    private String clinicId;
    private String clinicName;
    private String clinicAddress;
}
