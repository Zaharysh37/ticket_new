package by.laba1.demo.core.mapper.clinic;

import by.laba1.demo.api.dto.clinic.GetClinicDto;
import by.laba1.demo.api.dto.doctor.GetDoctorDto;
import by.laba1.demo.core.entities.Clinic;
import by.laba1.demo.core.entities.Doctor;
import by.laba1.demo.core.mapper.doctor.GetDoctorMapper;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-10T11:21:42+0300",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.12.1.jar, environment: Java 17.0.14 (Amazon.com Inc.)"
)
@Component
public class GetClinicMapperImpl implements GetClinicMapper {

    @Autowired
    private GetDoctorMapper getDoctorMapper;

    @Override
    public List<GetClinicDto> toDtos(Iterable<Clinic> list) {
        if ( list == null ) {
            return new ArrayList<GetClinicDto>();
        }

        List<GetClinicDto> list1 = new ArrayList<GetClinicDto>();
        for ( Clinic clinic : list ) {
            list1.add( toDto( clinic ) );
        }

        return list1;
    }

    @Override
    public List<Clinic> toEntities(Iterable<GetClinicDto> list) {
        if ( list == null ) {
            return new ArrayList<Clinic>();
        }

        List<Clinic> list1 = new ArrayList<Clinic>();
        for ( GetClinicDto getClinicDto : list ) {
            list1.add( toEntity( getClinicDto ) );
        }

        return list1;
    }

    @Override
    public Clinic merge(Clinic entity, GetClinicDto dto) {
        if ( dto == null ) {
            return entity;
        }

        if ( dto.getId() != null ) {
            entity.setId( dto.getId() );
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
    public GetClinicDto toDto(Clinic clinic) {
        if ( clinic == null ) {
            return null;
        }

        GetClinicDto getClinicDto = new GetClinicDto();

        getClinicDto.setDoctorDtos( doctorSetToGetDoctorDtoSet( clinic.getDoctors() ) );
        getClinicDto.setId( clinic.getId() );
        getClinicDto.setName( clinic.getName() );
        getClinicDto.setAddress( clinic.getAddress() );

        return getClinicDto;
    }

    @Override
    public Clinic toEntity(GetClinicDto dto) {
        if ( dto == null ) {
            return null;
        }

        Clinic clinic = new Clinic();

        clinic.setDoctors( getDoctorDtoSetToDoctorSet( dto.getDoctorDtos() ) );
        clinic.setId( dto.getId() );
        clinic.setName( dto.getName() );
        clinic.setAddress( dto.getAddress() );

        return clinic;
    }

    protected Set<GetDoctorDto> doctorSetToGetDoctorDtoSet(Set<Doctor> set) {
        if ( set == null ) {
            return new LinkedHashSet<GetDoctorDto>();
        }

        Set<GetDoctorDto> set1 = new LinkedHashSet<GetDoctorDto>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( Doctor doctor : set ) {
            set1.add( getDoctorMapper.toDto( doctor ) );
        }

        return set1;
    }

    protected Set<Doctor> getDoctorDtoSetToDoctorSet(Set<GetDoctorDto> set) {
        if ( set == null ) {
            return new LinkedHashSet<Doctor>();
        }

        Set<Doctor> set1 = new LinkedHashSet<Doctor>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( GetDoctorDto getDoctorDto : set ) {
            set1.add( getDoctorMapper.toEntity( getDoctorDto ) );
        }

        return set1;
    }
}
