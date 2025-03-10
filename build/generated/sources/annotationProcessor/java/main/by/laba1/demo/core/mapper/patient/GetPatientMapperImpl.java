package by.laba1.demo.core.mapper.patient;

import by.laba1.demo.api.dto.patient.GetPatientDto;
import by.laba1.demo.core.entities.Patient;
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
public class GetPatientMapperImpl implements GetPatientMapper {

    @Override
    public GetPatientDto toDto(Patient e) {
        if ( e == null ) {
            return null;
        }

        GetPatientDto getPatientDto = new GetPatientDto();

        getPatientDto.setId( e.getId() );
        getPatientDto.setName( e.getName() );
        getPatientDto.setPhoneNumber( e.getPhoneNumber() );

        return getPatientDto;
    }

    @Override
    public Patient toEntity(GetPatientDto d) {
        if ( d == null ) {
            return null;
        }

        Patient patient = new Patient();

        patient.setId( d.getId() );
        patient.setName( d.getName() );
        patient.setPhoneNumber( d.getPhoneNumber() );

        return patient;
    }

    @Override
    public List<GetPatientDto> toDtos(Iterable<Patient> list) {
        if ( list == null ) {
            return new ArrayList<GetPatientDto>();
        }

        List<GetPatientDto> list1 = new ArrayList<GetPatientDto>();
        for ( Patient patient : list ) {
            list1.add( toDto( patient ) );
        }

        return list1;
    }

    @Override
    public List<Patient> toEntities(Iterable<GetPatientDto> list) {
        if ( list == null ) {
            return new ArrayList<Patient>();
        }

        List<Patient> list1 = new ArrayList<Patient>();
        for ( GetPatientDto getPatientDto : list ) {
            list1.add( toEntity( getPatientDto ) );
        }

        return list1;
    }

    @Override
    public Patient merge(Patient entity, GetPatientDto dto) {
        if ( dto == null ) {
            return entity;
        }

        if ( dto.getId() != null ) {
            entity.setId( dto.getId() );
        }
        if ( dto.getName() != null ) {
            entity.setName( dto.getName() );
        }
        if ( dto.getPhoneNumber() != null ) {
            entity.setPhoneNumber( dto.getPhoneNumber() );
        }

        return entity;
    }
}
