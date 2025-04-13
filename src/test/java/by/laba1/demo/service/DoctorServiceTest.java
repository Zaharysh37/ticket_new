package by.laba1.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import by.laba1.demo.api.dto.doctor.CreateDoctorDto;
import by.laba1.demo.api.dto.doctor.GetDoctorDto;
import by.laba1.demo.api.exception.throwble.BadRequestException;
import by.laba1.demo.api.exception.throwble.ConflictException;
import by.laba1.demo.api.exception.throwble.ResourceNotFoundException;
import by.laba1.demo.core.dao.chmem.CacheFactory;
import by.laba1.demo.core.dao.chmem.MyCache;
import by.laba1.demo.core.dao.clinic.ClinicRepository;
import by.laba1.demo.core.dao.doctor.DoctorRepository;
import by.laba1.demo.core.entities.Appointment;
import by.laba1.demo.core.entities.Clinic;
import by.laba1.demo.core.entities.Doctor;
import by.laba1.demo.core.mapper.doctor.CreateDoctorMapper;
import by.laba1.demo.core.mapper.doctor.GetDoctorMapper;
import by.laba1.demo.core.service.DoctorService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private ClinicRepository clinicRepository;

    @Mock
    private CreateDoctorMapper createDoctorMapper;

    @Mock
    private GetDoctorMapper getDoctorMapper;

    @Mock
    private CacheFactory cacheFactory;

    @Mock
    private MyCache<String, List<GetDoctorDto>> doctorMyCache;

    @InjectMocks
    private DoctorService doctorService;

    private Doctor doctor;
    private CreateDoctorDto createDoctorDto;
    private GetDoctorDto getDoctorDto;

    private final Long doctorId = 1L;
    private final String specialization = "Cardiology";
    private final LocalDateTime time = LocalDateTime.now().plusHours(1);

    @BeforeEach
    void setUp() {
        String name = "Dr. House";
        String cacheName = "doctorCache";

        doctor = new Doctor();
        doctor.setId(doctorId);
        doctor.setName(name);
        doctor.setSpecialization(specialization);

        createDoctorDto = new CreateDoctorDto();
        createDoctorDto.setName(name);
        createDoctorDto.setSpecialization(specialization);

        getDoctorDto = new GetDoctorDto();
        getDoctorDto.setId(doctorId);
        getDoctorDto.setName(name);
        getDoctorDto.setSpecialization(specialization);

        when(cacheFactory.createCache(eq(cacheName), anyInt(), anyLong()))
            .thenAnswer(invocation -> doctorMyCache);

        doctorService.init();

        verify(cacheFactory).createCache(eq(cacheName), anyInt(), anyLong());
    }

    @Test
    void create_ShouldSaveDoctorAndClearCache() {
        when(createDoctorMapper.toEntity(createDoctorDto)).thenReturn(doctor);
        when(doctorRepository.save(doctor)).thenReturn(doctor);
        when(getDoctorMapper.toDto(doctor)).thenReturn(getDoctorDto);

        GetDoctorDto result = doctorService.create(createDoctorDto);

        assertEquals(getDoctorDto, result);
        verify(doctorRepository).save(doctor);
        verify(doctorMyCache).clear();
    }

    @Test
    void getAll_ShouldReturnAllDoctors() {
        when(doctorRepository.findAll()).thenReturn(List.of(doctor));
        when(getDoctorMapper.toDtos(List.of(doctor))).thenReturn(List.of(getDoctorDto));

        List<GetDoctorDto> result = doctorService.getAll();

        assertEquals(1, result.size());
        assertEquals(getDoctorDto, result.get(0));
        verify(doctorRepository).findAll();
    }

    @Test
    void getById_WhenDoctorExists_ShouldReturnDoctor() {
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(getDoctorMapper.toDto(doctor)).thenReturn(getDoctorDto);

        GetDoctorDto result = doctorService.getById(doctorId);

        assertEquals(getDoctorDto, result);
        verify(doctorRepository).findById(doctorId);
    }

    @Test
    void getById_WhenDoctorNotExists_ShouldThrowException() {
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> doctorService.getById(1L));
        verify(doctorRepository).findById(doctorId);
    }

    @Test
    void findAvailable_ShouldReturnAvailableDoctors() {
        String cacheKey = specialization + "_" + time.toString();

        when(doctorRepository.findAvailableDoctors(time, specialization))
            .thenReturn(List.of(doctor));
        when(getDoctorMapper.toDtos(List.of(doctor))).thenReturn(List.of(getDoctorDto));
        when(doctorMyCache.get(eq(cacheKey), any())).thenAnswer(invocation -> {
            Supplier<List<GetDoctorDto>> supplier = invocation.getArgument(1);
            return supplier.get();
        });

        List<GetDoctorDto> result = doctorService.findAvailable(time, specialization);

        assertEquals(1, result.size());
        assertEquals(getDoctorDto, result.get(0));
        verify(doctorMyCache).get(eq(cacheKey), any());
    }

    @Test
    void findAvailable_WhenAppointmentTimeIsNull_ShouldReturnAvailableDoctors() {
        LocalDateTime nullTime = null;
        String cacheKey = specialization + "_" + "null";

        when(doctorRepository.findAvailableDoctors(nullTime, specialization))
            .thenReturn(List.of(doctor));
        when(getDoctorMapper.toDtos(List.of(doctor))).thenReturn(List.of(getDoctorDto));
        when(doctorMyCache.get(eq(cacheKey), any())).thenAnswer(invocation -> {
            Supplier<List<GetDoctorDto>> supplier = invocation.getArgument(1);
            return supplier.get();
        });

        List<GetDoctorDto> result = doctorService.findAvailable(nullTime, specialization);

        assertEquals(1, result.size());
        assertEquals(getDoctorDto, result.get(0));
        verify(doctorMyCache).get(eq(cacheKey), any());
    }

    @Test
    void findAvailable_WhenDataInCache_ShouldNotCallRepository() {
        String cacheKey = specialization + "_" + time.toString();
        List<GetDoctorDto> cachedDoctors = List.of(getDoctorDto);

        when(doctorMyCache.get(eq(cacheKey), any()))
            .thenReturn(cachedDoctors);

        List<GetDoctorDto> result = doctorService.findAvailable(time, specialization);

        assertEquals(1, result.size());
        assertEquals(getDoctorDto, result.get(0));
        verify(doctorMyCache).get(eq(cacheKey), any());
        verify(doctorRepository, never()).findAvailableDoctors(any(), any());
    }

    @Test
    void findAvailable_WhenSpecializationIsNull_ShouldThrowException() {
        assertThrows(BadRequestException.class,
            () -> doctorService.findAvailable(time, null));
    }

    @Test
    void findAvailable_WhenSpecializationBlank_ShouldThrowException() {
        assertThrows(BadRequestException.class,
            () -> doctorService.findAvailable(time, ""));
    }

    @Test
    void findAvailable_WhenNoDoctorsFound_ShouldThrowException() {
        String cacheKey = specialization + "_" + time.toString();

        when(doctorRepository.findAvailableDoctors(time, specialization))
            .thenReturn(List.of());
        when(doctorMyCache.get(eq(cacheKey), any())).thenAnswer(invocation -> {
            Supplier<List<GetDoctorDto>> supplier = invocation.getArgument(1);
            return supplier.get();
        });

        assertThrows(ResourceNotFoundException.class,
            () -> doctorService.findAvailable(time, specialization));
    }

    @Test
    void update_ShouldUpdateDoctorAndClearCache() {
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(doctorRepository.save(doctor)).thenReturn(doctor);
        when(getDoctorMapper.toDto(doctor)).thenReturn(getDoctorDto);

        GetDoctorDto result = doctorService.update(doctorId, createDoctorDto);

        assertEquals(getDoctorDto, result);
        verify(createDoctorMapper).merge(doctor, createDoctorDto);
        verify(doctorMyCache).clear();
    }

    @Test
    void update_WhenDoctorNotExists_ShouldThrowException() {
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> doctorService.update(doctorId, createDoctorDto));
    }

    @Test
    void delete_ShouldDeleteDoctorAndClearCache() {
        Clinic clinic = new Clinic();
        clinic.getDoctors().add(doctor);

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(clinicRepository.findByDoctorsContaining(doctor)).thenReturn(List.of(clinic));

        doctorService.delete(doctorId);

        verify(doctorRepository).delete(doctor);
        verify(doctorMyCache).clear();
        assertTrue(clinic.getDoctors().isEmpty());
    }

    @Test
    void delete_WhenDoctorHasAppointments_ShouldThrowException() {
        doctor.setAppointments(List.of(new Appointment()));

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));

        assertThrows(ConflictException.class, () -> doctorService.delete(doctorId));
        verify(doctorRepository, never()).delete(any());
    }

    @Test
    void delete_WhenDoctorNotExists_ShouldThrowException() {
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> doctorService.delete(doctorId));
        verify(doctorRepository, never()).delete(any());
    }
}