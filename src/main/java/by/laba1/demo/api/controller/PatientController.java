package by.laba1.demo.api.controller;

import by.laba1.demo.api.dto.patient.CreatePatientDto;
import by.laba1.demo.api.dto.patient.GetPatientDto;
import by.laba1.demo.core.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Tag(name = "Patient API", description = "Управление пациентами")
public class PatientController {
    private final PatientService patientService;

    @Operation(summary = "Поиск пациентов")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "404", description = "Пациенты не найдены")
    })
    @GetMapping
    public List<GetPatientDto> getByFilter(
        @Parameter(description = "Имя пациента", example = "Иванов")
        @RequestParam(required = false) String name,

        @Parameter(description = "Номер телефона", example = "80251234567")
        @RequestParam(required = false) String phoneNumber
    ) {
        return patientService.getPatientsByFilter(name, phoneNumber);
    }

    @Operation(summary = "Получить пациента по ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "404", description = "Не найдено")
    })
    @GetMapping("/{id}")
    public GetPatientDto getById(
        @Parameter(description = "ID пациента", example = "1")
        @PathVariable long id) {
        return patientService.getPatientById(id);
    }

    @Operation(summary = "Добавить пациента")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Создано"),
        @ApiResponse(responseCode = "400", description = "Невалидные данные"),
        @ApiResponse(responseCode = "409", description = "Номер телефона уже существует")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GetPatientDto create(@RequestBody CreatePatientDto dto) {
        return patientService.createPatient(dto);
    }

    @Operation(summary = "Обновить пациента")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Обновлено"),
        @ApiResponse(responseCode = "400", description = "Невалидные данные"),
        @ApiResponse(responseCode = "404", description = "Не найдено"),
        @ApiResponse(responseCode = "409", description = "Номер телефона уже существует")
    })
    @PutMapping("/{id}")
    public GetPatientDto update(
        @Parameter(description = "ID пациента", example = "1")
        @PathVariable long id,
        @RequestBody CreatePatientDto dto) {
        return patientService.updatePatient(id, dto);
    }

    @Operation(summary = "Удалить пациента")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Удалено"),
        @ApiResponse(responseCode = "404", description = "Не найдено")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
        @Parameter(description = "ID пациента", example = "1")
        @PathVariable long id) {
        patientService.deletePatient(id);
    }
}