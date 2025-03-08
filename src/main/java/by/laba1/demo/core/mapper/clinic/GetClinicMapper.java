package by.laba1.demo.core.mapper.clinic;

import by.laba1.demo.api.dto.clinic.GetClinicDto;
import by.laba1.demo.core.entities.Clinic;
import by.laba1.demo.core.mapper.BaseMapper;
import by.laba1.demo.core.mapper.HelperDoctorMapper;
import by.laba1.demo.core.mapper.doctor.GetDoctorMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = BaseMapper.class, uses = {HelperDoctorMapper.class, GetDoctorMapper.class})
public interface GetClinicMapper extends BaseMapper<Clinic, GetClinicDto> {
    @Mapping(source = "doctors", target = "doctorDtos")
    GetClinicDto toDto(Clinic clinic);

    @Mapping(source = "doctorDtos", target = "doctors")
    Clinic toEntity(GetClinicDto dto);
}
