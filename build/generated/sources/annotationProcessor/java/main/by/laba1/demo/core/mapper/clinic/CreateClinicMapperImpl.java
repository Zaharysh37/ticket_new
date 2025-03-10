package by.laba1.demo.core.mapper.clinic;

import by.laba1.demo.api.dto.clinic.CreateClinicDto;
import by.laba1.demo.core.entities.Clinic;
import by.laba1.demo.core.mapper.HelperDoctorMapper;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-10T11:21:42+0300",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.12.1.jar, environment: Java 17.0.14 (Amazon.com Inc.)"
)
@Component
public class CreateClinicMapperImpl implements CreateClinicMapper {

    @Autowired
    private HelperDoctorMapper helperDoctorMapper;

    @Override
    public List<CreateClinicDto> toDtos(Iterable<Clinic> list) {
        if ( list == null ) {
            return new ArrayList<CreateClinicDto>();
        }

        List<CreateClinicDto> list1 = new ArrayList<CreateClinicDto>();
        for ( Clinic clinic : list ) {
            list1.add( toDto( clinic ) );
        }

        return list1;
    }

    @Override
    public List<Clinic> toEntities(Iterable<CreateClinicDto> list) {
        if ( list == null ) {
            return new ArrayList<Clinic>();
        }

        List<Clinic> list1 = new ArrayList<Clinic>();
        for ( CreateClinicDto createClinicDto : list ) {
            list1.add( toEntity( createClinicDto ) );
        }

        return list1;
    }

    @Override
    public Clinic merge(Clinic entity, CreateClinicDto dto) {
        if ( dto == null ) {
            return entity;
        }

        if ( dto.getName() != null ) {
            entity.setName( dto.getName() );
        }
        if ( dto.getAddress() != null ) {
            entity.setAddress( dto.getAddress() );
        }

        return entity;
    }

    @Override
    public Clinic toEntity(CreateClinicDto dto) {
        if ( dto == null ) {
            return null;
        }

        Clinic clinic = new Clinic();

        clinic.setDoctors( helperDoctorMapper.mapDoctorIdsToDoctors( dto.getDoctorIds() ) );
        clinic.setName( dto.getName() );
        clinic.setAddress( dto.getAddress() );

        return clinic;
    }

    @Override
    public CreateClinicDto toDto(Clinic clinic) {
        if ( clinic == null ) {
            return null;
        }

        CreateClinicDto createClinicDto = new CreateClinicDto();

        createClinicDto.setDoctorIds( helperDoctorMapper.mapDoctorsToDoctorTds( clinic.getDoctors() ) );
        createClinicDto.setName( clinic.getName() );
        createClinicDto.setAddress( clinic.getAddress() );

        return createClinicDto;
    }
}
