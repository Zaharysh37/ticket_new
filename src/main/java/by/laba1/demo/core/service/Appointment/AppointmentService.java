package by.laba1.demo.core.service.Appointment;

import by.laba1.demo.api.dto.appointment.CreateAppointmentDto;
import by.laba1.demo.api.dto.appointment.GetAppointmentDto;
import by.laba1.demo.api.exception.throwble.BadRequestException;
import by.laba1.demo.api.exception.ExceptionMessage;
import by.laba1.demo.api.exception.throwble.ResourceNotFoundException;
import by.laba1.demo.core.dao.appointment.AppointmentRepository;
import by.laba1.demo.core.dao.chmem.MyCache;
import by.laba1.demo.core.dao.clinic.ClinicRepository;
import by.laba1.demo.core.dao.doctor.DoctorRepository;
import by.laba1.demo.core.dao.patient.PatientRepository;
import by.laba1.demo.core.entities.Appointment;
import by.laba1.demo.core.mapper.appointment.CreateAppointmentMapper;
import by.laba1.demo.core.mapper.appointment.GetAppointmentMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final CreateAppointmentMapper createAppointmentMapper;
    private final GetAppointmentMapper getAppointmentMapper;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final ClinicRepository clinicRepository;
    private final HelperAppointmentService helperAppointmentService;
    private final MyCache<String, List<GetAppointmentDto>> appointmentMyCache = new MyCache<>(100, 120_000);

    public GetAppointmentDto create(CreateAppointmentDto dto) {
        Appointment appointment = createAppointmentMapper.toEntity(dto);
        helperAppointmentService.validateAppointment(appointment);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        appointmentMyCache.clear();
        return getAppointmentMapper.toDto(savedAppointment);
    }

    @Transactional(readOnly = true)
    public List<GetAppointmentDto> getAll() {
        return getAppointmentMapper.toDtos(appointmentRepository.findAll());
    }

    public GetAppointmentDto getById(Long id) {
        return getAppointmentMapper.toDto(
            appointmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                ExceptionMessage.ENTITY_NOT_FOUND.format(id)
            ))
        );
    }

    public void delete(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new ResourceNotFoundException(ExceptionMessage.ENTITY_NOT_FOUND.format(id));
        }
        appointmentRepository.deleteById(id);
        appointmentMyCache.clear();
    }

    @Transactional(readOnly = true)
    public List<GetAppointmentDto> findByPatientName(String patientName) {
        if (patientName == null || patientName.isBlank()) {
            throw new BadRequestException(ExceptionMessage.FIELD_REQUIRED.getMessage());
        }

        return appointmentMyCache.get(patientName, () -> {
                List<Appointment> appointments = appointmentRepository.findByPatientName(patientName);
                if (appointments.isEmpty()) {
                    throw new ResourceNotFoundException(ExceptionMessage.ENTITY_WITH_CRITERIA_NOT_FOUND.getMessage());
                }
                return getAppointmentMapper.toDtos(appointments);
            }
        );
    }
}