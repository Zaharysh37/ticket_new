package by.laba1.demo.core.mapper.doctor;

import by.laba1.demo.api.dto.doctor.GetDoctorDto;
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
public class GetDoctorMapperImpl implements GetDoctorMapper {

    @Override
    public GetDoctorDto toDto(Doctor e) {
        if ( e == null ) {
            return null;
        }

        GetDoctorDto getDoctorDto = new GetDoctorDto();

        getDoctorDto.setId( e.getId() );
        getDoctorDto.setName( e.getName() );
        getDoctorDto.setSpecialization( e.getSpecialization() );

        return getDoctorDto;
    }

    @Override
    public Doctor toEntity(GetDoctorDto d) {
        if ( d == null ) {
            return null;
        }

        Doctor doctor = new Doctor();

        doctor.setId( d.getId() );
        doctor.setName( d.getName() );
        doctor.setSpecialization( d.getSpecialization() );

        return doctor;
    }

    @Override
    public List<GetDoctorDto> toDtos(Iterable<Doctor> list) {
        if ( list == null ) {
            return new ArrayList<GetDoctorDto>();
        }

        List<GetDoctorDto> list1 = new ArrayList<GetDoctorDto>();
        for ( Doctor doctor : list ) {
            list1.add( toDto( doctor ) );
        }

        return list1;
    }

    @Override
    public List<Doctor> toEntities(Iterable<GetDoctorDto> list) {
        if ( list == null ) {
            return new ArrayList<Doctor>();
        }

        List<Doctor> list1 = new ArrayList<Doctor>();
        for ( GetDoctorDto getDoctorDto : list ) {
            list1.add( toEntity( getDoctorDto ) );
        }

        return list1;
    }

    @Override
    public Doctor merge(Doctor entity, GetDoctorDto dto) {
        if ( dto == null ) {
            return entity;
        }

        if ( dto.getId() != null ) {
            entity.setId( dto.getId() );
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
