package by.laba1.demo.core.mapper.appointment;

import by.laba1.demo.api.dto.appointment.GetAppointmentDto;
import by.laba1.demo.core.entities.Appointment;
import by.laba1.demo.core.mapper.doctor.GetDoctorMapper;
import by.laba1.demo.core.mapper.patient.GetPatientMapper;
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
public class GetAppointmentMapperImpl implements GetAppointmentMapper {

    @Autowired
    private GetDoctorMapper getDoctorMapper;
    @Autowired
    private GetPatientMapper getPatientMapper;

    @Override
    public List<GetAppointmentDto> toDtos(Iterable<Appointment> list) {
        if ( list == null ) {
            return new ArrayList<GetAppointmentDto>();
        }

        List<GetAppointmentDto> list1 = new ArrayList<GetAppointmentDto>();
        for ( Appointment appointment : list ) {
            list1.add( toDto( appointment ) );
        }

        return list1;
    }

    @Override
    public List<Appointment> toEntities(Iterable<GetAppointmentDto> list) {
        if ( list == null ) {
            return new ArrayList<Appointment>();
        }

        List<Appointment> list1 = new ArrayList<Appointment>();
        for ( GetAppointmentDto getAppointmentDto : list ) {
            list1.add( toEntity( getAppointmentDto ) );
        }

        return list1;
    }

    @Override
    public Appointment merge(Appointment entity, GetAppointmentDto dto) {
        if ( dto == null ) {
            return entity;
        }

        if ( dto.getId() != null ) {
            entity.setId( dto.getId() );
        }
        if ( dto.getAppointmentTime() != null ) {
            entity.setAppointmentTime( dto.getAppointmentTime() );
        }

        return entity;
    }

    @Override
    public GetAppointmentDto toDto(Appointment appointment) {
        if ( appointment == null ) {
            return null;
        }

        GetAppointmentDto getAppointmentDto = new GetAppointmentDto();

        getAppointmentDto.setDoctorDto( getDoctorMapper.toDto( appointment.getDoctor() ) );
        getAppointmentDto.setPatientDto( getPatientMapper.toDto( appointment.getPatient() ) );
        getAppointmentDto.setId( appointment.getId() );
        getAppointmentDto.setAppointmentTime( appointment.getAppointmentTime() );

        return getAppointmentDto;
    }

    @Override
    public Appointment toEntity(GetAppointmentDto dto) {
        if ( dto == null ) {
            return null;
        }

        Appointment appointment = new Appointment();

        appointment.setDoctor( getDoctorMapper.toEntity( dto.getDoctorDto() ) );
        appointment.setPatient( getPatientMapper.toEntity( dto.getPatientDto() ) );
        appointment.setId( dto.getId() );
        appointment.setAppointmentTime( dto.getAppointmentTime() );

        return appointment;
    }
}
