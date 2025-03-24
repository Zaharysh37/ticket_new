package by.laba1.demo.api.controller;

import by.laba1.demo.core.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.File;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
@Tag(name = "Log Management", description = "APIs for managing application logs")
public class LogController {

    private final LogService logService;

    @GetMapping("/{date}/download")
    @Operation(
        summary = "Download log file by date",
        description = "Downloads the log file for a specific date"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Log file downloaded successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid date format"),
        @ApiResponse(responseCode = "404", description = "Log file for the specified date not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error while downloading the log file")
    })
    public ResponseEntity<FileSystemResource> getLogFile(@PathVariable String date) {
        File logFile = logService.getLogFileByDate(date);

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + logFile.getName())
            .body(new FileSystemResource(logFile));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Integer>> getMethodStats() {
        Map<String, Integer> stats = logService.getMethodStats();
        return ResponseEntity.ok(stats);
    }
}