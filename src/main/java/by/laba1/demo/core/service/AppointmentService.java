package by.laba1.demo.core.service;

import by.laba1.demo.api.dto.appointment.CreateAppointmentDto;
import by.laba1.demo.api.dto.appointment.GetAppointmentDto;
import by.laba1.demo.core.dao.appointment.AppointmentRepository;
import by.laba1.demo.core.dao.clinic.ClinicRepository;
import by.laba1.demo.core.dao.doctor.DoctorRepository;
import by.laba1.demo.core.dao.patient.PatientRepository;
import by.laba1.demo.core.entities.Appointment;
import by.laba1.demo.core.mapper.appointment.CreateAppointmentMapper;
import by.laba1.demo.core.mapper.appointment.GetAppointmentMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public GetAppointmentDto create(CreateAppointmentDto dto) {
        Appointment appointment = createAppointmentMapper.toEntity(dto);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        return getAppointmentMapper.toDto(savedAppointment);
    }

    public List<GetAppointmentDto> getAll() {
        return getAppointmentMapper.toDtos(appointmentRepository.findAll());
    }

    public GetAppointmentDto getById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));
        return getAppointmentMapper.toDto(appointment);
    }

    public void delete(Long id) {
        appointmentRepository.deleteById(id);
    }
}

