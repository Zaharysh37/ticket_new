package by.laba1.demo.core.service;

import by.laba1.demo.api.aspects.CounterAspect;
import by.laba1.demo.api.error.BadRequestException;
import by.laba1.demo.api.error.ResourceNotFoundException;
import java.io.File;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogService {

    private final CounterAspect counterAspect;

    public File getLogFileByDate(String date) {
        if (!date.matches("\\d{4}-\\d{2}-\\d{2}_\\d{2}-\\d{2}")) {
            throw new BadRequestException("Invalid date format. Expected format: YYYY-MM-DD_HH-mm");
        }

        String logFilePath = "logs/app." + date + ".log";
        File logFile = new File(logFilePath);

        if (!logFile.exists()) {
            throw new ResourceNotFoundException("Log file for " + date + " not found.");
        }

        return logFile;
    }

    public Map<String, Integer> getMethodStats() {
        return counterAspect.getMethodCallStatistics()
            .entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get()));
    }
}
