package by.laba1.demo.core.mapper.appointment;

import by.laba1.demo.api.dto.appointment.CreateAppointmentDto;
import by.laba1.demo.core.entities.Appointment;
import by.laba1.demo.core.mapper.HelperClinicMapper;
import by.laba1.demo.core.mapper.HelperDoctorMapper;
import by.laba1.demo.core.mapper.HelperPatientMapper;
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
public class CreateAppointmentMapperImpl implements CreateAppointmentMapper {

    @Autowired
    private HelperDoctorMapper helperDoctorMapper;
    @Autowired
    private HelperPatientMapper helperPatientMapper;
    @Autowired
    private HelperClinicMapper helperClinicMapper;

    @Override
    public List<CreateAppointmentDto> toDtos(Iterable<Appointment> list) {
        if ( list == null ) {
            return new ArrayList<CreateAppointmentDto>();
        }

        List<CreateAppointmentDto> list1 = new ArrayList<CreateAppointmentDto>();
        for ( Appointment appointment : list ) {
            list1.add( toDto( appointment ) );
        }

        return list1;
    }

    @Override
    public List<Appointment> toEntities(Iterable<CreateAppointmentDto> list) {
        if ( list == null ) {
            return new ArrayList<Appointment>();
        }

        List<Appointment> list1 = new ArrayList<Appointment>();
        for ( CreateAppointmentDto createAppointmentDto : list ) {
            list1.add( toEntity( createAppointmentDto ) );
        }

        return list1;
    }

    @Override
    public Appointment merge(Appointment entity, CreateAppointmentDto dto) {
        if ( dto == null ) {
            return entity;
        }

        if ( dto.getAppointmentTime() != null ) {
            entity.setAppointmentTime( dto.getAppointmentTime() );
        }

        return entity;
    }

    @Override
    public Appointment toEntity(CreateAppointmentDto dto) {
        if ( dto == null ) {
            return null;
        }

        Appointment appointment = new Appointment();

        appointment.setDoctor( helperDoctorMapper.mapDoctorIdToDoctor( dto.getDoctorId() ) );
        appointment.setPatient( helperPatientMapper.mapPatientIdToPatient( dto.getPatientId() ) );
        appointment.setClinic( helperClinicMapper.mapClinicIdToClinic( dto.getClinicId() ) );
        appointment.setAppointmentTime( dto.getAppointmentTime() );

        return appointment;
    }

    @Override
    public CreateAppointmentDto toDto(Appointment appointment) {
        if ( appointment == null ) {
            return null;
        }

        CreateAppointmentDto createAppointmentDto = new CreateAppointmentDto();

        createAppointmentDto.setDoctorId( helperDoctorMapper.mapDoctorToDoctorId( appointment.getDoctor() ) );
        createAppointmentDto.setPatientId( helperPatientMapper.mapPatientToPatientId( appointment.getPatient() ) );
        createAppointmentDto.setAppointmentTime( appointment.getAppointmentTime() );

        return createAppointmentDto;
    }
}
