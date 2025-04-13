package by.laba1.demo.api.controller;

import by.laba1.demo.core.interceptor.VisitCounterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/visits")
@RequiredArgsConstructor
@Tag(name = "Visit Statistics", description = "Статистика посещений")
public class VisitCounterController {
    private final VisitCounterService visitCounterService;

    @Operation(summary = "Получить статистику по URL")
    @GetMapping("/url")
    public ResponseEntity<Integer> getVisitCount(
        @Parameter(description = "URL для проверки")
        @RequestParam String url) {
        return ResponseEntity.ok(visitCounterService.getVisitCount(url));
    }

    @Operation(summary = "Полная статистика посещений")
    @GetMapping
    public ResponseEntity<Map<String, Integer>> getAllVisitCounts() {
        return ResponseEntity.ok(visitCounterService.getAllVisitCounts());
    }
}