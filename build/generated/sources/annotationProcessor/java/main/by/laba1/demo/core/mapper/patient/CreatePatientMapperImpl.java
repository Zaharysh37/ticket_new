package by.laba1.demo.core.mapper.patient;

import by.laba1.demo.api.dto.patient.CreatePatientDto;
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
public class CreatePatientMapperImpl implements CreatePatientMapper {

    @Override
    public CreatePatientDto toDto(Patient e) {
        if ( e == null ) {
            return null;
        }

        CreatePatientDto createPatientDto = new CreatePatientDto();

        createPatientDto.setName( e.getName() );
        createPatientDto.setPhoneNumber( e.getPhoneNumber() );

        return createPatientDto;
    }

    @Override
    public Patient toEntity(CreatePatientDto d) {
        if ( d == null ) {
            return null;
        }

        Patient patient = new Patient();

        patient.setName( d.getName() );
        patient.setPhoneNumber( d.getPhoneNumber() );

        return patient;
    }

    @Override
    public List<CreatePatientDto> toDtos(Iterable<Patient> list) {
        if ( list == null ) {
            return new ArrayList<CreatePatientDto>();
        }

        List<CreatePatientDto> list1 = new ArrayList<CreatePatientDto>();
        for ( Patient patient : list ) {
            list1.add( toDto( patient ) );
        }

        return list1;
    }

    @Override
    public List<Patient> toEntities(Iterable<CreatePatientDto> list) {
        if ( list == null ) {
            return new ArrayList<Patient>();
        }

        List<Patient> list1 = new ArrayList<Patient>();
        for ( CreatePatientDto createPatientDto : list ) {
            list1.add( toEntity( createPatientDto ) );
        }

        return list1;
    }

    @Override
    public Patient merge(Patient entity, CreatePatientDto dto) {
        if ( dto == null ) {
            return entity;
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
