package by.laba1.demo.core.mapper.appointment;

import by.laba1.demo.api.dto.appointment.CreateAppointmentDto;
import by.laba1.demo.core.entities.Appointment;
import by.laba1.demo.core.mapper.BaseMapper;
import by.laba1.demo.core.mapper.HelperClinicMapper;
import by.laba1.demo.core.mapper.HelperDoctorMapper;
import by.laba1.demo.core.mapper.HelperPatientMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = BaseMapper.class, uses = {HelperDoctorMapper.class, HelperPatientMapper.class, HelperClinicMapper.class})
public interface CreateAppointmentMapper extends BaseMapper<Appointment, CreateAppointmentDto> {
    @Mapping(source = "doctorId", target = "doctor")
    @Mapping(source = "patientId", target = "patient")
    @Mapping(source = "clinicId", target = "clinic")
    Appointment toEntity(CreateAppointmentDto dto);

    @Mapping(source = "doctor", target = "doctorId")
    @Mapping(source = "patient", target = "patientId")
    CreateAppointmentDto toDto(Appointment appointment);
}
