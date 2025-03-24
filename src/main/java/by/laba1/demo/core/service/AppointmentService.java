package by.laba1.demo.core.service;

import by.laba1.demo.api.dto.appointment.CreateAppointmentDto;
import by.laba1.demo.api.dto.appointment.GetAppointmentDto;
import by.laba1.demo.api.error.BadRequestException;
import by.laba1.demo.api.error.MessageException;
import by.laba1.demo.api.error.ResourceNotFoundException;
import by.laba1.demo.api.error.ValidationException;
import by.laba1.demo.core.dao.appointment.AppointmentRepository;
import by.laba1.demo.core.dao.chmem.MyCache;
import by.laba1.demo.core.dao.clinic.ClinicRepository;
import by.laba1.demo.core.dao.doctor.DoctorRepository;
import by.laba1.demo.core.dao.patient.PatientRepository;
import by.laba1.demo.core.entities.Appointment;
import by.laba1.demo.core.mapper.appointment.CreateAppointmentMapper;
import by.laba1.demo.core.mapper.appointment.GetAppointmentMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;

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
    private final MyCache<String, List<GetAppointmentDto>> appointmentMyCache = new MyCache<>(100, 120_000);

    public GetAppointmentDto create(CreateAppointmentDto dto) {
        Appointment appointment;
        try {
            appointment = createAppointmentMapper.toEntity(dto);
        } catch(Exception e) {
            throw new ValidationException(MessageException.BAD_MAPPING);
        }

        if (appointmentRepository.existsByDoctorAndAppointmentTime(appointment.getDoctor(), appointment.getAppointmentTime())) {
            throw new ValidationException(MessageException.CONFLICT);
        }

        if (appointment.getAppointmentTime().isBefore(LocalDate.now().atStartOfDay())) {
            throw new ValidationException(MessageException.CONFLICT);
        }

        Appointment savedAppointment = appointmentRepository.save(appointment);

        appointmentMyCache.clear();

        return getAppointmentMapper.toDto(savedAppointment);
    }

    public List<GetAppointmentDto> getAll() {
        return getAppointmentMapper.toDtos(appointmentRepository.findAll());
    }

    public GetAppointmentDto getById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                String.format(MessageException.ENTITY_WITH_ID_NOT_FOUND, id)
            ));
        return getAppointmentMapper.toDto(appointment);
    }

    public void delete(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new ResourceNotFoundException(String.format(MessageException.ENTITY_WITH_ID_NOT_FOUND, id));
        }
        appointmentRepository.deleteById(id);
        appointmentMyCache.clear();
    }

    public List<GetAppointmentDto> findByPatientName(String patientName) {
        if (patientName == null || patientName.isBlank()) {
            throw new BadRequestException(MessageException.SPECIALIZATION_REQUIRED);
        }

        String cacheKey = "appointments_" + patientName;

        List<GetAppointmentDto> cachedAppointments = appointmentMyCache.get(cacheKey);
        if (cachedAppointments != null) {
            return cachedAppointments;
        }

        List<Appointment> appointments = appointmentRepository.findByPatientName(patientName);
        if (appointments.isEmpty()) {
            throw new EntityNotFoundException(MessageException.ENTITY_WITH_CRITERIA_NOT_FOUND);
        }

        List<GetAppointmentDto> appointmentDtos = getAppointmentMapper.toDtos(appointments);
        appointmentMyCache.put(cacheKey, appointmentDtos);

        return appointmentDtos;
    }
}