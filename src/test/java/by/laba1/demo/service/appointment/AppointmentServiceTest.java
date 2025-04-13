package by.laba1.demo.service.appointment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import by.laba1.demo.api.dto.appointment.CreateAppointmentDto;
import by.laba1.demo.api.dto.appointment.GetAppointmentDto;
import by.laba1.demo.api.dto.doctor.GetDoctorDto;
import by.laba1.demo.api.exception.throwble.BadRequestException;
import by.laba1.demo.api.exception.throwble.ResourceNotFoundException;
import by.laba1.demo.core.dao.appointment.AppointmentRepository;
import by.laba1.demo.core.dao.chmem.CacheFactory;
import by.laba1.demo.core.dao.chmem.MyCache;
import by.laba1.demo.core.entities.Appointment;
import by.laba1.demo.core.mapper.appointment.CreateAppointmentMapper;
import by.laba1.demo.core.mapper.appointment.GetAppointmentMapper;
import by.laba1.demo.core.service.appointment.AppointmentService;
import by.laba1.demo.core.service.appointment.HelperAppointmentService;
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
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private CreateAppointmentMapper createAppointmentMapper;

    @Mock
    private GetAppointmentMapper getAppointmentMapper;

    @Mock
    private HelperAppointmentService helperAppointmentService;

    @Mock
    private CacheFactory cacheFactory;

    @Mock
    private MyCache<String, List<GetAppointmentDto>> appointmentMyCache;

    @InjectMocks
    private AppointmentService appointmentService;

    private Appointment appointment;
    private CreateAppointmentDto createAppointmentDto;
    private GetAppointmentDto getAppointmentDto;
    private final Long appointmentId = 1L;
    private final LocalDateTime appointmentTime = LocalDateTime.now().plusDays(1);
    private final String patientName = "John Doe";

    @BeforeEach
    void setUp() {
        String cacheName = "appointmentCache";

        appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setAppointmentTime(appointmentTime);

        createAppointmentDto = new CreateAppointmentDto();
        createAppointmentDto.setPatientId(1L);
        createAppointmentDto.setDoctorId(1L);
        createAppointmentDto.setClinicId(1L);
        createAppointmentDto.setAppointmentTime(appointmentTime);

        getAppointmentDto = new GetAppointmentDto();
        getAppointmentDto.setId(appointmentId);
        getAppointmentDto.setAppointmentTime(appointmentTime);

        when(cacheFactory.createCache(eq(cacheName), anyInt(), anyLong()))
            .thenAnswer(invocation -> appointmentMyCache);

        appointmentService.init();

        verify(cacheFactory).createCache(eq(cacheName), anyInt(), anyLong());
    }

    @Test
    void create_ShouldSaveAppointmentAndClearCache() {
        when(createAppointmentMapper.toEntity(createAppointmentDto)).thenReturn(appointment);
        when(appointmentRepository.save(appointment)).thenReturn(appointment);
        when(getAppointmentMapper.toDto(appointment)).thenReturn(getAppointmentDto);

        GetAppointmentDto result = appointmentService.create(createAppointmentDto);

        assertEquals(getAppointmentDto, result);
        verify(helperAppointmentService).validateAppointment(appointment);
        verify(appointmentRepository).save(appointment);
        verify(appointmentMyCache).clear();
    }

    @Test
    void createBulk_ShouldSaveMultipleAppointments() {
        List<CreateAppointmentDto> dtos = List.of(createAppointmentDto);
        when(createAppointmentMapper.toEntity(createAppointmentDto)).thenReturn(appointment);
        when(appointmentRepository.saveAll(anyList())).thenReturn(List.of(appointment));
        when(getAppointmentMapper.toDtos(anyList())).thenReturn(List.of(getAppointmentDto));

        List<GetAppointmentDto> result = appointmentService.createBulk(dtos);

        assertEquals(1, result.size());
        verify(appointmentRepository).saveAll(anyList());
        verify(appointmentMyCache).clear();
    }

    @Test
    void createBulk_WhenEmptyList_ShouldThrowException() {
        assertThrows(BadRequestException.class,
            () -> appointmentService.createBulk(List.of()));
    }

    @Test
    void createBulk_WhenNull_ShouldThrowException() {
        assertThrows(BadRequestException.class,
            () -> appointmentService.createBulk(null));
    }

    @Test
    void getAll_ShouldReturnAllAppointments() {
        when(appointmentRepository.findAll()).thenReturn(List.of(appointment));
        when(getAppointmentMapper.toDtos(List.of(appointment))).thenReturn(List.of(getAppointmentDto));

        List<GetAppointmentDto> result = appointmentService.getAll();

        assertEquals(1, result.size());
        assertEquals(getAppointmentDto, result.get(0));
    }

    @Test
    void getById_ShouldReturnAppointment() {
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(getAppointmentMapper.toDto(appointment)).thenReturn(getAppointmentDto);

        GetAppointmentDto result = appointmentService.getById(appointmentId);

        assertEquals(getAppointmentDto, result);
    }

    @Test
    void getById_WhenNotFound_ShouldThrowException() {
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> appointmentService.getById(appointmentId));
    }

    @Test
    void delete_ShouldDeleteAppointmentAndClearCache() {
        when(appointmentRepository.existsById(appointmentId)).thenReturn(true);

        appointmentService.delete(appointmentId);

        verify(appointmentRepository).deleteById(appointmentId);
        verify(appointmentMyCache).clear();
    }

    @Test
    void delete_WhenNotFound_ShouldThrowException() {
        when(appointmentRepository.existsById(appointmentId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
            () -> appointmentService.delete(appointmentId));
    }

    @Test
    void findByPatientName_ShouldReturnAppointmentsFromRepository() {
        when(appointmentMyCache.get(eq(patientName), any())).thenAnswer(invocation -> {
            Supplier<List<GetAppointmentDto>> supplier = invocation.getArgument(1);
            return supplier.get();
        });
        when(appointmentRepository.findByPatientName(patientName)).thenReturn(List.of(appointment));
        when(getAppointmentMapper.toDtos(List.of(appointment))).thenReturn(List.of(getAppointmentDto));

        List<GetAppointmentDto> result = appointmentService.findByPatientName(patientName);

        assertEquals(1, result.size());
        assertEquals(getAppointmentDto, result.get(0));
    }

    @Test
    void findByPatientName_WhenDataInCache_ShouldNotCallRepository() {
        List<GetAppointmentDto> cachedAppointments = List.of(getAppointmentDto);

        when(appointmentMyCache.get(eq(patientName), any()))
            .thenReturn(cachedAppointments);

        List<GetAppointmentDto> result = appointmentService.findByPatientName(patientName);

        assertEquals(1, result.size());
        assertEquals(getAppointmentDto, result.get(0));
        verify(appointmentMyCache).get(eq(patientName), any());
        verify(appointmentRepository, never()).findByPatientName(any());
    }

    @Test
    void findByPatientName_WhenNameBlank_ShouldThrowException() {
        assertThrows(BadRequestException.class,
            () -> appointmentService.findByPatientName(" "));
    }

    @Test
    void findByPatientName_WhenNameIsNull_ShouldThrowException() {
        assertThrows(BadRequestException.class,
            () -> appointmentService.findByPatientName(null));
    }

    @Test
    void findByPatientName_WhenNotFound_ShouldThrowException() {
        when(appointmentRepository.findByPatientName(patientName))
            .thenReturn(List.of());
        when(appointmentMyCache.get(eq(patientName), any())).thenAnswer(invocation -> {
            Supplier<List<GetDoctorDto>> supplier = invocation.getArgument(1);
            return supplier.get();
        });

        assertThrows(ResourceNotFoundException.class,
            () -> appointmentService.findByPatientName(patientName));
    }
}