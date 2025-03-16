package by.laba1.demo.core.mapper.appointment;

import by.laba1.demo.api.dto.appointment.GetAppointmentDto;
import by.laba1.demo.core.entities.Appointment;
import by.laba1.demo.core.mapper.BaseMapper;
import by.laba1.demo.core.mapper.doctor.GetDoctorMapper;
import by.laba1.demo.core.mapper.patient.GetPatientMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = BaseMapper.class, uses = {GetDoctorMapper.class, GetPatientMapper.class})
public interface GetAppointmentMapper extends BaseMapper<Appointment, GetAppointmentDto> {
    @Mapping(source = "doctor", target = "doctorDto")
    @Mapping(source = "patient", target = "patientDto")
    @Mapping(source = "clinic.name", target = "clinicName")
    @Mapping(source = "clinic.address", target = "clinicAddress")
    GetAppointmentDto toDto(Appointment appointment);

    @Mapping(source = "doctorDto", target = "doctor")
    @Mapping(source = "patientDto", target = "patient")
    Appointment toEntity(GetAppointmentDto dto);
}
