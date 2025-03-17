package by.laba1.demo.core.service.clinic;

import by.laba1.demo.api.dto.clinic.CreateClinicDto;
import by.laba1.demo.api.dto.clinic.GetClinicDto;
import by.laba1.demo.core.dao.appointment.AppointmentRepository;
import by.laba1.demo.core.dao.chmem.MyCache;
import by.laba1.demo.core.dao.clinic.ClinicRepository;
import by.laba1.demo.core.dao.doctor.DoctorRepository;
import by.laba1.demo.core.entities.Clinic;
import by.laba1.demo.core.mapper.clinic.CreateClinicMapper;
import by.laba1.demo.core.mapper.clinic.GetClinicMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
@RequiredArgsConstructor
public class ClinicService {
    private final ClinicRepository clinicRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final CreateClinicMapper createClinicMapper;
    private final GetClinicMapper getClinicMapper;
    private final HelperClinicService helperClinicService;
    private final MyCache<Long, GetClinicDto> clinicMyCache = new MyCache<>(10 * 60 * 1000L);

    public GetClinicDto create(CreateClinicDto dto) {
        Clinic clinic = createClinicMapper.toEntity(dto);
        Clinic savedClinic = clinicRepository.save(clinic);
        return getClinicMapper.toDto(savedClinic);
    }

    public List<GetClinicDto> getAll() {
        List<Clinic> clinics = clinicRepository.findAll();
        if (clinics.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Patients not found");
        }
        return getClinicMapper.toDtos(clinics);
    }

    public GetClinicDto getById(Long id) {
        GetClinicDto dto = clinicMyCache.get(id);
        if (dto != null) {
            return dto;
        }

        Clinic clinic = clinicRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Clinic not found"));
        GetClinicDto getClinicDto = getClinicMapper.toDto(clinic);

        clinicMyCache.put(id, getClinicDto);

        return getClinicMapper.toDto(clinic);
    }

    public GetClinicDto update(Long id, CreateClinicDto dto) {
        Clinic clinic = clinicRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Clinic not found"));

        helperClinicService.updateDoctorsAndRemoveAppointments(clinic, dto.getDoctorIds());
        createClinicMapper.merge(clinic, dto);

        Clinic savedClinic = clinicRepository.save(clinic);
        return getClinicMapper.toDto(savedClinic);
    }

    public void delete(Long id) {
        clinicRepository.deleteById(id);
    }
}