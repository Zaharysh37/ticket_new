package by.laba1.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock private DoctorRepository doctorRepository;
    @Mock private ClinicRepository clinicRepository;
    @Mock private CreateDoctorMapper createDoctorMapper;
    @Mock private GetDoctorMapper getDoctorMapper;
    @Mock private CacheFactory cacheFactory;
    @Mock private MyCache<String, List<GetDoctorDto>> doctorMyCache;

    @InjectMocks
    private DoctorService doctorService;

    private final Long TEST_ID = 1L;
    private final String TEST_SPECIALIZATION = "Cardiology";
    private final LocalDateTime TEST_TIME = LocalDateTime.now().plusHours(1);

    @BeforeEach
    void setUp() {
        when(cacheFactory.createCache("doctorCache", anyInt(), anyLong()))
            .thenReturn(doctorMyCache);
        doctorService.init();
    }

    // Тест для метода create
    @Test
    void create_ShouldSaveDoctorAndClearCache() {
        CreateDoctorDto dto = new CreateDoctorDto();
        Doctor doctor = new Doctor();
        Doctor savedDoctor = new Doctor();
        savedDoctor.setId(TEST_ID);
        GetDoctorDto expectedDto = new GetDoctorDto();

        when(createDoctorMapper.toEntity(dto)).thenReturn(doctor);
        when(doctorRepository.save(doctor)).thenReturn(savedDoctor);
        when(getDoctorMapper.toDto(savedDoctor)).thenReturn(expectedDto);

        GetDoctorDto result = doctorService.create(dto);

        assertEquals(expectedDto, result);
        verify(doctorRepository).save(doctor);
        verify(doctorMyCache).clear();
    }

    // Тест для метода getAll
    @Test
    void getAll_ShouldReturnAllDoctors() {
        List<Doctor> doctors = List.of(new Doctor(), new Doctor());
        List<GetDoctorDto> expectedDtos = List.of(new GetDoctorDto(), new GetDoctorDto());

        when(doctorRepository.findAll()).thenReturn(doctors);
        when(getDoctorMapper.toDtos(doctors)).thenReturn(expectedDtos);

        List<GetDoctorDto> result = doctorService.getAll();

        assertEquals(2, result.size());
        verify(doctorRepository).findAll();
    }

    // Тест для метода getById (успешный случай)
    @Test
    void getById_WhenDoctorExists_ShouldReturnDoctor() {
        Doctor doctor = new Doctor();
        doctor.setId(TEST_ID);
        GetDoctorDto expectedDto = new GetDoctorDto();

        when(doctorRepository.findById(TEST_ID)).thenReturn(Optional.of(doctor));
        when(getDoctorMapper.toDto(doctor)).thenReturn(expectedDto);

        GetDoctorDto result = doctorService.getById(TEST_ID);

        assertEquals(expectedDto, result);
    }

    // Тест для метода getById (когда врач не найден)
    @Test
    void getById_WhenDoctorNotExists_ShouldThrowException() {
        when(doctorRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> doctorService.getById(TEST_ID));
    }

    // Тест для метода findAvailable (успешный случай)
    @Test
    void findAvailable_ShouldReturnAvailableDoctors() {
        String cacheKey = TEST_SPECIALIZATION + "_" + TEST_TIME.toString();
        List<Doctor> doctors = List.of(new Doctor(), new Doctor());
        List<GetDoctorDto> expectedDtos = List.of(new GetDoctorDto(), new GetDoctorDto());

        when(doctorRepository.findAvailableDoctors(TEST_TIME, TEST_SPECIALIZATION))
            .thenReturn(doctors);
        when(getDoctorMapper.toDtos(doctors)).thenReturn(expectedDtos);

        List<GetDoctorDto> result = doctorService.findAvailable(TEST_TIME, TEST_SPECIALIZATION);

        assertEquals(2, result.size());
        verify(doctorMyCache).get(cacheKey, any());
    }

    // Тест для метода findAvailable (когда специализация не указана)
    @Test
    void findAvailable_WhenSpecializationBlank_ShouldThrowException() {
        assertThrows(BadRequestException.class,
            () -> doctorService.findAvailable(TEST_TIME, " "));
    }

    // Тест для метода findAvailable (когда врачи не найдены)
    @Test
    void findAvailable_WhenNoDoctorsFound_ShouldThrowException() {
        when(doctorRepository.findAvailableDoctors(any(), any()))
            .thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class,
            () -> doctorService.findAvailable(TEST_TIME, TEST_SPECIALIZATION));
    }

    // Тест для метода update (успешный случай)
    @Test
    void update_ShouldUpdateDoctorAndClearCache() {
        CreateDoctorDto dto = new CreateDoctorDto();
        Doctor existingDoctor = new Doctor();
        existingDoctor.setId(TEST_ID);
        Doctor updatedDoctor = new Doctor();
        updatedDoctor.setId(TEST_ID);
        GetDoctorDto expectedDto = new GetDoctorDto();

        when(doctorRepository.findById(TEST_ID)).thenReturn(Optional.of(existingDoctor));
        when(doctorRepository.save(existingDoctor)).thenReturn(updatedDoctor);
        when(getDoctorMapper.toDto(updatedDoctor)).thenReturn(expectedDto);

        GetDoctorDto result = doctorService.update(TEST_ID, dto);

        assertEquals(expectedDto, result);
        verify(createDoctorMapper).merge(existingDoctor, dto);
        verify(doctorMyCache).clear();
    }

    // Тест для метода delete (успешный случай)
    @Test
    void delete_ShouldDeleteDoctorAndClearCache() {
        Doctor doctor = new Doctor();
        doctor.setId(TEST_ID);
        Clinic clinic = new Clinic();
        clinic.getDoctors().add(doctor);

        when(doctorRepository.findById(TEST_ID)).thenReturn(Optional.of(doctor));
        when(clinicRepository.findByDoctorsContaining(doctor)).thenReturn(List.of(clinic));

        doctorService.delete(TEST_ID);

        verify(doctorRepository).delete(doctor);
        assertTrue(clinic.getDoctors().isEmpty());
        verify(doctorMyCache).clear();
    }

    // Тест для метода delete (когда есть записи на прием)
    @Test
    void delete_WhenHasAppointments_ShouldThrowException() {
        Doctor doctor = new Doctor();
        doctor.setId(TEST_ID);
        doctor.setAppointments(List.of(new Appointment()));

        when(doctorRepository.findById(TEST_ID)).thenReturn(Optional.of(doctor));

        assertThrows(ConflictException.class, () -> doctorService.delete(TEST_ID));
    }

    // Тест для generateCacheKey
    @Test
    void generateCacheKey_ShouldGenerateValidKey() {
        String result = doctorService.generateCacheKey(TEST_SPECIALIZATION, TEST_TIME);
        assertTrue(result.contains(TEST_SPECIALIZATION));
        assertTrue(result.contains(TEST_TIME.toString()));
    }

    // Тест для generateCacheKey с null временем
    @Test
    void generateCacheKey_WithNullTime_ShouldGenerateValidKey() {
        String result = doctorService.generateCacheKey(TEST_SPECIALIZATION, null);
        assertTrue(result.contains(TEST_SPECIALIZATION));
        assertTrue(result.contains("null"));
    }
}
