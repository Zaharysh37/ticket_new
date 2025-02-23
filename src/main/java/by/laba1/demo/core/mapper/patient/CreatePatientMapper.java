package by.laba1.demo.core.mapper.patient;

import by.laba1.demo.api.dto.patient.CreatePatientDto;
import by.laba1.demo.core.entities.Patient;
import by.laba1.demo.core.mapper.BaseMapper;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapper.class)
public interface CreatePatientMapper extends BaseMapper<Patient, CreatePatientDto> {
}
