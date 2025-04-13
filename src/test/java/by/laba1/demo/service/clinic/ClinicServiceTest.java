package by.laba1.demo.service.clinic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import by.laba1.demo.api.dto.clinic.CreateClinicDto;
import by.laba1.demo.api.dto.clinic.GetClinicDto;
import by.laba1.demo.api.exception.throwble.ConflictException;
import by.laba1.demo.api.exception.throwble.ResourceNotFoundException;
import by.laba1.demo.core.dao.chmem.CacheFactory;
import by.laba1.demo.core.dao.chmem.MyCache;
import by.laba1.demo.core.dao.clinic.ClinicRepository;
import by.laba1.demo.core.dao.doctor.DoctorRepository;
import by.laba1.demo.core.entities.Appointment;
import by.laba1.demo.core.entities.Clinic;
import by.laba1.demo.core.entities.Doctor;
import by.laba1.demo.core.mapper.clinic.CreateClinicMapper;
import by.laba1.demo.core.mapper.clinic.GetClinicMapper;
import by.laba1.demo.core.service.clinic.ClinicService;
import by.laba1.demo.core.service.clinic.HelperClinicService;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClinicServiceTest {

    @Mock
    private ClinicRepository clinicRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private CreateClinicMapper createClinicMapper;

    @Mock
    private GetClinicMapper getClinicMapper;

    @Mock
    private HelperClinicService helperClinicService;

    @Mock
    private CacheFactory cacheFactory;

    @Mock
    private MyCache<Long, GetClinicDto> clinicMyCache;

    @InjectMocks
    private ClinicService clinicService;

    private Clinic clinic;
    private CreateClinicDto createClinicDto;
    private GetClinicDto getClinicDto;
    private Set<Doctor> doctors;
    private final Long clinicId = 1L;
    private final String clinicName = "City Clinic";
    private final String clinicAddress = "123 Main St";
    private final Set<Long> doctorIds = Set.of(1L, 2L);

    @BeforeEach
    void setUp() {
        String cacheName = "clinicCache";

        clinic = new Clinic();
        clinic.setId(clinicId);
        clinic.setName(clinicName);
        clinic.setAddress(clinicAddress);

        Doctor doctor1 = new Doctor();
        doctor1.setId(1L);
        Doctor doctor2 = new Doctor();
        doctor2.setId(2L);
        doctors = Set.of(doctor1, doctor2);
        clinic.setDoctors(doctors);

        createClinicDto = new CreateClinicDto();
        createClinicDto.setName(clinicName);
        createClinicDto.setAddress(clinicAddress);
        createClinicDto.setDoctorIds(doctorIds);

        getClinicDto = new GetClinicDto();
        getClinicDto.setId(clinicId);
        getClinicDto.setName(clinicName);
        getClinicDto.setAddress(clinicAddress);

        when(cacheFactory.createCache(eq(cacheName), anyInt(), anyLong()))
            .thenAnswer(invocation -> clinicMyCache);

        clinicService.init();

        verify(cacheFactory).createCache(eq(cacheName), anyInt(), anyLong());
    }

    @Test
    void create_ShouldSaveClinic() {
        when(createClinicMapper.toEntity(createClinicDto)).thenReturn(clinic);
        when(clinicRepository.save(clinic)).thenReturn(clinic);
        when(getClinicMapper.toDto(clinic)).thenReturn(getClinicDto);

        GetClinicDto result = clinicService.create(createClinicDto);

        assertEquals(getClinicDto, result);
        verify(clinicRepository).save(clinic);
        verify(clinicMyCache).clear();
    }

    @Test
    void getAll_ShouldReturnAllClinics() {
        when(clinicRepository.findAll()).thenReturn(List.of(clinic));
        when(getClinicMapper.toDtos(List.of(clinic))).thenReturn(List.of(getClinicDto));

        List<GetClinicDto> result = clinicService.getAll();

        assertEquals(1, result.size());
        assertEquals(getClinicDto, result.get(0));
        verify(clinicRepository).findAll();
    }

    @Test
    void getById_ShouldReturnClinicFromRepository() {
        when(clinicMyCache.get(eq(clinicId), any())).thenAnswer(invocation -> {
            Supplier<GetClinicDto> supplier = invocation.getArgument(1);
            return supplier.get();
        });
        when(clinicRepository.findById(clinicId)).thenReturn(Optional.of(clinic));
        when(getClinicMapper.toDto(clinic)).thenReturn(getClinicDto);

        GetClinicDto result = clinicService.getById(clinicId);

        assertEquals(getClinicDto, result);
        verify(clinicMyCache).get(eq(clinicId), any());
    }

    @Test
    void getById_WhenDataInCache_ShouldNotCallRepository() {
        when(clinicMyCache.get(eq(clinicId), any())).thenReturn(getClinicDto);

        GetClinicDto result = clinicService.getById(clinicId);

        assertEquals(getClinicDto, result);
        verify(clinicMyCache).get(eq(clinicId), any());
        verify(clinicRepository, never()).findById(any());
    }

    @Test
    void getById_WhenClinicNotExists_ShouldThrowException() {
        when(clinicMyCache.get(eq(clinicId), any())).thenAnswer(invocation -> {
            Supplier<GetClinicDto> supplier = invocation.getArgument(1);
            return supplier.get();
        });
        when(clinicRepository.findById(clinicId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> clinicService.getById(clinicId));
    }

    @Test
    void update_ShouldUpdateClinicAndClearCache() {
        when(clinicRepository.findById(clinicId)).thenReturn(Optional.of(clinic));
        when(clinicRepository.save(clinic)).thenReturn(clinic);
        when(getClinicMapper.toDto(clinic)).thenReturn(getClinicDto);

        GetClinicDto result = clinicService.update(clinicId, createClinicDto);

        assertEquals(getClinicDto, result);
        verify(createClinicMapper).merge(clinic, createClinicDto);
        verify(helperClinicService).updateDoctorsAndRemoveAppointments(clinic, doctorIds);
        verify(clinicMyCache).clear();
    }

    @Test
    void update_WhenClinicNotExists_ShouldThrowException() {
        when(clinicRepository.findById(clinicId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> clinicService.update(clinicId, createClinicDto));
    }

    @Test
    void delete_ShouldDeleteClinicAndClearCache() {
        when(clinicRepository.findById(clinicId)).thenReturn(Optional.of(clinic));

        clinicService.delete(clinicId);

        verify(clinicRepository).delete(clinic);
        verify(clinicMyCache).clear();
    }

    @Test
    void delete_WhenClinicHasAppointments_ShouldThrowException() {
        clinic.setAppointments(List.of(new Appointment()));
        when(clinicRepository.findById(clinicId)).thenReturn(Optional.of(clinic));

        assertThrows(ConflictException.class,
            () -> clinicService.delete(clinicId));
        verify(clinicRepository, never()).delete(any());
    }

    @Test
    void delete_WhenClinicNotExists_ShouldThrowException() {
        when(clinicRepository.findById(clinicId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> clinicService.delete(clinicId));
        verify(clinicRepository, never()).delete(any());
    }
}