package by.laba1.demo.api.controller;

import by.laba1.demo.api.dto.clinic.CreateClinicDto;
import by.laba1.demo.api.dto.clinic.GetClinicDto;
import by.laba1.demo.core.service.clinic.ClinicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clinics")
@RequiredArgsConstructor
@Tag(name = "Clinic API", description = "Операции с медицинскими клиниками")
public class ClinicController {
    private final ClinicService clinicService;

    @Operation(summary = "Создать клинику")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Клиника создана"),
        @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
        @ApiResponse(responseCode = "409", description = "Конфликт данных")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GetClinicDto create(@RequestBody @Valid CreateClinicDto dto) {
        return clinicService.create(dto);
    }

    @Operation(summary = "Список клиник")
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping
    public List<GetClinicDto> getAll() {
        return clinicService.getAll();
    }

    @Operation(summary = "Получить клинику")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "404", description = "Не найдено")
    })
    @GetMapping("/{id}")
    public GetClinicDto getById(
        @Parameter(description = "ID клиники", example = "1")
        @PathVariable long id) {
        return clinicService.getById(id);
    }

    @Operation(summary = "Обновить клинику")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Обновлено"),
        @ApiResponse(responseCode = "400", description = "Невалидные данные"),
        @ApiResponse(responseCode = "404", description = "Не найдено")
    })
    @PutMapping("/{id}")
    public GetClinicDto update(
        @Parameter(description = "ID клиники", example = "1")
        @PathVariable long id,
        @RequestBody @Valid CreateClinicDto dto) {
        return clinicService.update(id, dto);
    }

    @Operation(summary = "Удалить клинику")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Удалено"),
        @ApiResponse(responseCode = "404", description = "Не найдено"),
        @ApiResponse(responseCode = "409", description = "Есть связанные записи")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
        @Parameter(description = "ID клиники", example = "1")
        @PathVariable long id) {
        clinicService.delete(id);
    }
}