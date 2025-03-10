package by.laba1.demo.core.mapper.doctor;

import by.laba1.demo.api.dto.doctor.CreateDoctorDto;
import by.laba1.demo.core.entities.Doctor;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-10T11:21:42+0300",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.12.1.jar, environment: Java 17.0.14 (Amazon.com Inc.)"
)
@Component
public class CreateDoctorMapperImpl implements CreateDoctorMapper {

    @Override
    public CreateDoctorDto toDto(Doctor e) {
        if ( e == null ) {
            return null;
        }

        CreateDoctorDto createDoctorDto = new CreateDoctorDto();

        createDoctorDto.setName( e.getName() );
        createDoctorDto.setSpecialization( e.getSpecialization() );

        return createDoctorDto;
    }

    @Override
    public Doctor toEntity(CreateDoctorDto d) {
        if ( d == null ) {
            return null;
        }

        Doctor doctor = new Doctor();

        doctor.setName( d.getName() );
        doctor.setSpecialization( d.getSpecialization() );

        return doctor;
    }

    @Override
    public List<CreateDoctorDto> toDtos(Iterable<Doctor> list) {
        if ( list == null ) {
            return new ArrayList<CreateDoctorDto>();
        }

        List<CreateDoctorDto> list1 = new ArrayList<CreateDoctorDto>();
        for ( Doctor doctor : list ) {
            list1.add( toDto( doctor ) );
        }

        return list1;
    }

    @Override
    public List<Doctor> toEntities(Iterable<CreateDoctorDto> list) {
        if ( list == null ) {
            return new ArrayList<Doctor>();
        }

        List<Doctor> list1 = new ArrayList<Doctor>();
        for ( CreateDoctorDto createDoctorDto : list ) {
            list1.add( toEntity( createDoctorDto ) );
        }

        return list1;
    }

    @Override
    public Doctor merge(Doctor entity, CreateDoctorDto dto) {
        if ( dto == null ) {
            return entity;
        }

        if ( dto.getName() != null ) {
            entity.setName( dto.getName() );
        }
        if ( dto.getSpecialization() != null ) {
            entity.setSpecialization( dto.getSpecialization() );
        }

        return entity;
    }
}
