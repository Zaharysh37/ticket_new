package by.laba1.demo.core.mapper.doctor;

import by.laba1.demo.api.dto.clinic.GetClinicDto;
import by.laba1.demo.api.dto.doctor.GetDoctorDto;
import by.laba1.demo.core.entities.Clinic;
import by.laba1.demo.core.entities.Doctor;
import by.laba1.demo.core.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(config = BaseMapper.class)
public interface GetDoctorMapper extends BaseMapper<Doctor, GetDoctorDto> {
}
