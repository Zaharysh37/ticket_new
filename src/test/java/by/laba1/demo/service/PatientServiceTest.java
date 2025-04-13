package by.laba1.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import by.laba1.demo.api.dto.patient.CreatePatientDto;
import by.laba1.demo.api.dto.patient.GetPatientDto;
import by.laba1.demo.api.exception.throwble.ResourceNotFoundException;
import by.laba1.demo.core.dao.chmem.CacheFactory;
import by.laba1.demo.core.dao.chmem.MyCache;
import by.laba1.demo.core.dao.patient.PatientRepository;
import by.laba1.demo.core.entities.Patient;
import by.laba1.demo.core.mapper.patient.CreatePatientMapper;
import by.laba1.demo.core.mapper.patient.GetPatientMapper;
import by.laba1.demo.core.service.PatientService;
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
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private CreatePatientMapper createPatientMapper;

    @Mock
    private GetPatientMapper getPatientMapper;

    @Mock
    private CacheFactory cacheFactory;

    @Mock
    private MyCache<Long, GetPatientDto> patientMyCache;

    @InjectMocks
    private PatientService patientService;

    private Patient patient;
    private CreatePatientDto createPatientDto;
    private GetPatientDto getPatientDto;

    private final Long patientId = 1L;
    private final String patientName = "John Doe";
    private final String phoneNumber = "8025123456";

    @BeforeEach
    void setUp() {
        String cacheName = "patientCache";

        patient = new Patient();
        patient.setId(patientId);
        patient.setName(patientName);
        patient.setPhoneNumber(phoneNumber);

        createPatientDto = new CreatePatientDto();
        createPatientDto.setName(patientName);
        createPatientDto.setPhoneNumber(phoneNumber);

        getPatientDto = new GetPatientDto();
        getPatientDto.setId(patientId);
        getPatientDto.setName(patientName);
        getPatientDto.setPhoneNumber(phoneNumber);

        when(cacheFactory.createCache(eq(cacheName), anyInt(), anyLong()))
            .thenAnswer(invocation -> patientMyCache);

        patientService.init();

        verify(cacheFactory).createCache(eq(cacheName), anyInt(), anyLong());
    }

    @Test
    void getPatientsByFilter_ShouldReturnFilteredPatients() {
        when(patientRepository.findByFilters(patientName, phoneNumber))
            .thenReturn(List.of(patient));
        when(getPatientMapper.toDtos(List.of(patient)))
            .thenReturn(List.of(getPatientDto));

        List<GetPatientDto> result = patientService.getPatientsByFilter(patientName, phoneNumber);

        assertEquals(1, result.size());
        assertEquals(getPatientDto, result.get(0));
        verify(patientRepository).findByFilters(patientName, phoneNumber);
    }

    @Test
    void getPatientsByFilter_WhenNoPatientsFound_ShouldThrowException() {
        when(patientRepository.findByFilters(anyString(), anyString()))
            .thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class,
            () -> patientService.getPatientsByFilter("NonExisting", "000000000"));
    }

    @Test
    void getPatientById_ShouldReturnPatientFromRepository() {
        when(patientMyCache.get(eq(patientId), any())).thenAnswer(invocation -> {
            Supplier<GetPatientDto> supplier = invocation.getArgument(1);
            return supplier.get();
        });
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(getPatientMapper.toDto(patient)).thenReturn(getPatientDto);

        GetPatientDto result = patientService.getPatientById(patientId);

        assertEquals(getPatientDto, result);
        verify(patientMyCache).get(eq(patientId), any());
    }

    @Test
    void getPatientById_WhenDataInCache_ShouldNotCallRepository() {
        when(patientMyCache.get(eq(patientId), any())).thenReturn(getPatientDto);

        GetPatientDto result = patientService.getPatientById(patientId);

        assertEquals(getPatientDto, result);
        verify(patientMyCache).get(eq(patientId), any());
        verify(patientRepository, never()).findById(any());
    }

    @Test
    void getPatientById_WhenPatientNotExists_ShouldThrowException() {
        when(patientMyCache.get(eq(patientId), any())).thenAnswer(invocation -> {
            Supplier<GetPatientDto> supplier = invocation.getArgument(1);
            return supplier.get();
        });
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> patientService.getPatientById(patientId));
    }

    @Test
    void createPatient_ShouldSavePatient() {
        when(createPatientMapper.toEntity(createPatientDto)).thenReturn(patient);
        when(patientRepository.save(patient)).thenReturn(patient);
        when(getPatientMapper.toDto(patient)).thenReturn(getPatientDto);

        GetPatientDto result = patientService.createPatient(createPatientDto);

        assertEquals(getPatientDto, result);
        verify(patientRepository).save(patient);
    }

    @Test
    void updatePatient_ShouldUpdatePatientAndClearCache() {
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(patientRepository.save(patient)).thenReturn(patient);
        when(getPatientMapper.toDto(patient)).thenReturn(getPatientDto);

        GetPatientDto result = patientService.updatePatient(patientId, createPatientDto);

        assertEquals(getPatientDto, result);
        verify(createPatientMapper).merge(patient, createPatientDto);
        verify(patientMyCache).clear();
    }

    @Test
    void updatePatient_WhenPatientNotExists_ShouldThrowException() {
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> patientService.updatePatient(patientId, createPatientDto));
    }

    @Test
    void deletePatient_ShouldDeletePatientAndClearCache() {
        when(patientRepository.existsById(patientId)).thenReturn(true);

        patientService.deletePatient(patientId);

        verify(patientRepository).deleteById(patientId);
        verify(patientMyCache).clear();
    }

    @Test
    void deletePatient_WhenPatientNotExists_ShouldThrowException() {
        when(patientRepository.existsById(patientId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
            () -> patientService.deletePatient(patientId));
        verify(patientRepository, never()).deleteById(any());
    }
}