package by.laba1.demo.core.mapper.clinic;

import by.laba1.demo.api.dto.clinic.CreateClinicDto;
import by.laba1.demo.core.entities.Clinic;
import by.laba1.demo.core.mapper.BaseMapper;
import by.laba1.demo.core.mapper.HelperDoctorMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = BaseMapper.class, uses = HelperDoctorMapper.class)
public interface CreateClinicMapper extends BaseMapper<Clinic, CreateClinicDto> {
    @Mapping(source = "doctorIds", target = "doctors")
    Clinic toEntity(CreateClinicDto dto);

    @Mapping(source = "doctors", target = "doctorIds")
    CreateClinicDto toDto(Clinic clinic);
}
