package by.laba1.demo.api.controller;

import by.laba1.demo.api.dto.clinic.CreateClinicDto;
import by.laba1.demo.api.dto.clinic.GetClinicDto;
import by.laba1.demo.core.service.clinic.ClinicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clinics")
@RequiredArgsConstructor
@Tag(name = "Clinics", description = "Управление клиниками")
public class ClinicController {
    private final ClinicService clinicService;

    @Operation(summary = "Создать клинику", description = "Добавляет новую клинику в систему")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Клиника успешно создана"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные"),
        @ApiResponse(responseCode = "409", description = "Конфликт данных")
    })
    @PostMapping
    public GetClinicDto create(@Valid @RequestBody CreateClinicDto dto) {
        return clinicService.create(dto);
    }

    @Operation(summary = "Получить все клиники", description = "Возвращает список всех клиник")
    @ApiResponse(responseCode = "200", description = "Список успешно получен")
    @GetMapping
    public List<GetClinicDto> getAll() {
        return clinicService.getAll();
    }

    @Operation(summary = "Получить клинику по ID", description = "Возвращает клинику по идентификатору")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Клиника найдена"),
        @ApiResponse(responseCode = "404", description = "Клиника не найдена")
    })
    @GetMapping("/{id}")
    public GetClinicDto getById(@PathVariable long id) {
        return clinicService.getById(id);
    }

    @Operation(summary = "Обновить клинику", description = "Обновляет информацию о клинике")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Клиника успешно обновлена"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные"),
        @ApiResponse(responseCode = "404", description = "Клиника не найдена")
    })
    @PutMapping("/{id}")
    public GetClinicDto update(
        @PathVariable long id,
        @Valid @RequestBody CreateClinicDto dto) {
        return clinicService.update(id, dto);
    }

    @Operation(summary = "Удалить клинику", description = "Удаляет клинику по идентификатору")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Клиника успешно удалена"),
        @ApiResponse(responseCode = "404", description = "Клиника не найдена")
    })
    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        clinicService.delete(id);
    }
}