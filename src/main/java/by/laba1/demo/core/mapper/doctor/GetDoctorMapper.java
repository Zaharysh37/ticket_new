package by.laba1.demo.core.mapper.doctor;

import by.laba1.demo.api.dto.doctor.GetDoctorDto;
import by.laba1.demo.core.entities.Doctor;
import by.laba1.demo.core.mapper.BaseMapper;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapper.class)
public interface GetDoctorMapper extends BaseMapper<Doctor, GetDoctorDto> {
}
