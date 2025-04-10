package by.laba1.demo.api.controller;

import by.laba1.demo.api.dto.doctor.CreateDoctorDto;
import by.laba1.demo.api.dto.doctor.GetDoctorDto;
import by.laba1.demo.core.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctor API", description = "Управление врачами")
public class DoctorController {
    private final DoctorService doctorService;

    @Operation(summary = "Добавить врача")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Врач создан"),
        @ApiResponse(responseCode = "400", description = "Невалидные данные")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GetDoctorDto create(@RequestBody CreateDoctorDto dto) {
        return doctorService.create(dto);
    }

    @Operation(summary = "Список врачей")
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping
    public List<GetDoctorDto> getAll() {
        return doctorService.getAll();
    }

    @Operation(summary = "Найти врача по ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "404", description = "Не найдено")
    })
    @GetMapping("/{id}")
    public GetDoctorDto getById(
        @Parameter(description = "ID врача", example = "1")
        @PathVariable long id) {
        return doctorService.getById(id);
    }

    @Operation(summary = "Поиск доступных врачей")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Не указана специализация"),
        @ApiResponse(responseCode = "404", description = "Врачи не найдены")
    })
    @GetMapping("/available")
    public List<GetDoctorDto> findAvailable(
        @Parameter(description = "Дата и время приёма")
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime appointmentTime,
        @Parameter(description = "Специализация", required = true, example = "Кардиолог")
        @RequestParam String specialization) {
        return doctorService.findAvailable(appointmentTime, specialization);
    }

    @Operation(summary = "Обновить данные врача")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Обновлено"),
        @ApiResponse(responseCode = "400", description = "Невалидные данные"),
        @ApiResponse(responseCode = "404", description = "Не найдено")
    })
    @PutMapping("/{id}")
    public GetDoctorDto update(
        @Parameter(description = "ID врача", example = "1")
        @PathVariable long id,
        @RequestBody CreateDoctorDto dto) {
        return doctorService.update(id, dto);
    }

    @Operation(summary = "Удалить врача")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Удалено"),
        @ApiResponse(responseCode = "404", description = "Не найдено"),
        @ApiResponse(responseCode = "409", description = "Есть активные записи")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
        @Parameter(description = "ID врача", example = "1")
        @PathVariable long id) {
        doctorService.delete(id);
    }
}

