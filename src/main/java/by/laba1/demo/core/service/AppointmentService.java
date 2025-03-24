package by.laba1.demo.core.service;

import by.laba1.demo.api.dto.appointment.CreateAppointmentDto;
import by.laba1.demo.api.dto.appointment.GetAppointmentDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
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
        Appointment appointment = createAppointmentMapper.toEntity(dto);

        if (appointmentRepository.existsByDoctorAndAppointmentTime(appointment.getDoctor(), appointment.getAppointmentTime())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The doctor is busy at this time.");
        }

        if (appointment.getAppointmentTime().isBefore(LocalDate.now().atStartOfDay())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Дата записи не может быть в прошлом");
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
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));
        return getAppointmentMapper.toDto(appointment);
    }

    public void delete(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found");
        }

        appointmentRepository.deleteById(id);
        appointmentMyCache.clear();
    }

    public List<GetAppointmentDto> findByPatientName(String patientName) {
        String cacheKey = "appointments_" + patientName;

        List<GetAppointmentDto> cachedAppointments = appointmentMyCache.get(cacheKey);
        if (cachedAppointments != null) {
            return cachedAppointments;
        }

        List<Appointment> appointments = appointmentRepository.findByPatientName(patientName);
        if (appointments.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No appointments found for patient: " + patientName);
        }

        List<GetAppointmentDto> appointmentDtos = getAppointmentMapper.toDtos(appointments);
        appointmentMyCache.put(cacheKey, appointmentDtos);

        return appointmentDtos;
    }
}


