package by.laba1.demo.api.controller;

import by.laba1.demo.api.exception.throwble.BadRequestException;
import by.laba1.demo.api.exception.throwble.ResourceNotFoundException;
import by.laba1.demo.core.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.File;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javax.naming.ServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
@Tag(name = "Log API", description = "Работа с логами и статистикой")
public class LogController {
    private final LogService logService;

    @Operation(summary = "Скачать лог")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Неверный формат даты"),
        @ApiResponse(responseCode = "404", description = "Файл не найден")
    })
    @GetMapping("/{date}/download")
    public ResponseEntity<Resource> downloadLogFile(
        @Parameter(description = "Дата в формате YYYY-MM-DD_HH-mm", example = "2023-12-31_23-59")
        @PathVariable String date) {

        File logFile = logService.getLogFileByDate(date);
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + logFile.getName() + "\"")
            .body(new FileSystemResource(logFile));
    }

    @Operation(summary = "Статистика вызовов")
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping("/stats")
    public Map<String, Integer> getMethodStats() throws ServiceUnavailableException {
        return logService.getMethodStats();
    }

    @Operation(summary = "Сбросить статистику")
    @ApiResponse(responseCode = "204", description = "Сброшено")
    @PostMapping("/stats/reset")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetStats() {
        logService.resetStatistics();
    }

    @PostMapping("/export/{dateTime}")
    public CompletableFuture<ResponseEntity<Map<String, String>>> exportLogs(
        @PathVariable String dateTime) {

        return logService.generateLogFileForDateAsync(dateTime)
            .thenApply(taskId -> ResponseEntity.accepted().body(Map.of(
                "taskId", taskId,
                "statusUrl", "/api/logs/tasks/" + taskId + "/status"
            )))
            .exceptionally(ex -> {
                if (ex.getCause() instanceof BadRequestException) {
                    return ResponseEntity.badRequest()
                        .body(Map.of("error", ex.getCause().getMessage()));
                }
                if (ex.getCause() instanceof ResourceNotFoundException) {
                    return ResponseEntity.notFound().build();
                }
                return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal server error"));
            });
    }

    @Operation(summary = "Проверить статус задачи")
    @GetMapping("/tasks/{taskId}/status")
    public ResponseEntity<Map<String, String>> getTaskStatus(
        @PathVariable String taskId) {

        String status = logService.getTaskStatus(taskId);
        if ("NOT_FOUND".equals(status)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("status", status));
    }

    @Operation(summary = "Скачать результат задачи")
    @GetMapping("/tasks/{taskId}/download")
    public ResponseEntity<Resource> downloadTaskFile(
        @PathVariable String taskId) {

        File file = logService.getTaskFile(taskId);
        if (file == null || !file.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getName() + "\"")
            .body(new FileSystemResource(file));
    }
}