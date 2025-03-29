package by.laba1.demo.api.controller;

import by.laba1.demo.api.dto.appointment.CreateAppointmentDto;
import by.laba1.demo.api.dto.appointment.GetAppointmentDto;
import by.laba1.demo.core.service.Appointment.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Appointment API", description = "Управление записями на приём")
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;

    @Operation(summary = "Создать запись")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Успешно создана"),
        @ApiResponse(responseCode = "400", description = "Невалидные данные"),
        @ApiResponse(responseCode = "404", description = "Врач/пациент/клиника не найдены"),
        @ApiResponse(responseCode = "409", description = "Конфликт времени записи")
    })
    @PostMapping
    public GetAppointmentDto create(@RequestBody @Valid CreateAppointmentDto dto) {
        return appointmentService.create(dto);
    }

    @Operation(summary = "Получить все записи")
    @GetMapping
    public List<GetAppointmentDto> getAll() {
        return appointmentService.getAll();
    }

    @Operation(summary = "Получить запись по ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Запись найдена"),
        @ApiResponse(responseCode = "404", description = "Запись не найдена")
    })
    @GetMapping("/{id}")
    public GetAppointmentDto getById(@PathVariable long id) {
        return appointmentService.getById(id);
    }

    @Operation(summary = "Найти записи по пациенту")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Успешно"),
        @ApiResponse(responseCode = "400", description = "Не указано имя"),
        @ApiResponse(responseCode = "404", description = "Записи не найдены")
    })
    @GetMapping("/filter")
    public List<GetAppointmentDto> getByPatientName(
        @RequestParam String patientName) {
        return appointmentService.findByPatientName(patientName);
    }

    @Operation(summary = "Удалить запись")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Успешно удалено"),
        @ApiResponse(responseCode = "404", description = "Запись не найдена")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        appointmentService.delete(id);
    }
}