package by.laba1.demo.api.aspects;

import by.laba1.demo.api.exception.ExceptionMessage;
import by.laba1.demo.api.exception.throwble.BadRequestException;
import by.laba1.demo.api.exception.throwble.ResourceNotFoundException;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.stream.Collectors;
import javax.naming.ServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogService {
    private final CounterAspect counterAspect;
    private static final String LOG_DIR = "logs/";
    private static final String LOG_PREFIX = "app.";
    private static final String LOG_SUFFIX = ".log";
    private static final DateTimeFormatter DATE_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");

    public File getLogFileByDate(String date) {
        try {
            // Валидация формата даты
            LocalDateTime.parse(date, DATE_FORMATTER);

            String logFilePath = LOG_DIR + LOG_PREFIX + date + LOG_SUFFIX;
            Path logPath = Paths.get(logFilePath).normalize();

            // Проверка на directory traversal
            if (!logPath.startsWith(LOG_DIR)) {
                throw new BadRequestException("Invalid file path");
            }

            File logFile = logPath.toFile();

            if (!logFile.exists()) {
                throw new ResourceNotFoundException(
                    ExceptionMessage.LOG_FILE_NOT_FOUND.format(date)
                );
            }

            return logFile;
        } catch (DateTimeParseException e) {
            throw new BadRequestException(
                ExceptionMessage.INVALID_DATE_FORMAT.getMessage()
            );
        }
    }

    @Transactional(readOnly = true)
    public Map<String, Integer> getMethodStats() throws ServiceUnavailableException {
        try {
            return counterAspect.getMethodCallStatistics()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    e -> e.getValue().get()
                ));
        } catch (Exception e) {
            throw new ServiceUnavailableException(
                ExceptionMessage.STATS_UNAVAILABLE.getMessage()
            );
        }
    }

    public void resetStatistics() {
        counterAspect.resetStatistics();
    }
}