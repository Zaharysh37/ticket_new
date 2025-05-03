package by.laba1.demo.core.service;

import by.laba1.demo.api.aspects.CounterAspect;
import by.laba1.demo.api.exception.ExceptionMessage;
import by.laba1.demo.api.exception.throwble.BadRequestException;
import by.laba1.demo.api.exception.throwble.ConflictException;
import by.laba1.demo.api.exception.throwble.ResourceNotFoundException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.naming.ServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
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
    private static final DateTimeFormatter FILE_DATE_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");
    private static final DateTimeFormatter LOG_DATE_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Map<String, String> taskStatuses = new ConcurrentHashMap<>();
    private final Map<String, String> taskFilePaths = new ConcurrentHashMap<>();

    public File getLogFileByDate(String date) {
        try {
            // Валидация формата даты
            LocalDateTime.parse(date, FILE_DATE_FORMATTER);

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

    @Async
    public CompletableFuture<String> generateLogFileForDateAsync(String dateTimeStr) {
        String taskId = UUID.randomUUID().toString();
        taskStatuses.put(taskId, "PROCESSING");

        try {
            if (!dateTimeStr.matches("^\\d{4}-\\d{2}-\\d{2}(_\\d{2}-\\d{2})?$")) {
                throw new BadRequestException("Invalid date format. Use YYYY-MM-DD or YYYY-MM-DD_HH-mm");
            }
            // Пробуем разные форматы даты
            LocalDateTime dateTime;
            try {
                // Сначала пробуем полный формат с часами и минутами
                dateTime = LocalDateTime.parse(dateTimeStr, FILE_DATE_FORMATTER);
            } catch (DateTimeParseException e1) {
                try {
                    // Если не получилось, пробуем формат только с датой
                    LocalDate date = LocalDate.parse(dateTimeStr, DateTimeFormatter.ISO_DATE);
                    dateTime = date.atStartOfDay();
                } catch (DateTimeParseException e2) {
                    taskStatuses.put(taskId, "FAILED: Invalid date/time format");
                    throw new BadRequestException(
                        "Invalid date/time format. Expected formats: " +
                            "YYYY-MM-DD or YYYY-MM-DD_HH-mm"
                    );
                }
            }

            LocalDateTime finalDateTime = dateTime;
            CompletableFuture.runAsync(() -> {
                try {
                    Thread.sleep(20_000);

                    // 1. Находим все файлы логов за указанную дату/время
                    List<Path> logFiles = findLogFilesForDateTime(finalDateTime);

                    if (logFiles.isEmpty()) {
                        throw new ResourceNotFoundException("No log files found for: " + dateTimeStr);
                    }

                    // 2. Читаем и фильтруем записи
                    List<String> filteredLines = filterLogEntries(logFiles, finalDateTime);

                    if (filteredLines.isEmpty()) {
                        throw new ResourceNotFoundException("No log entries found for: " + dateTimeStr);
                    }

                    // 3. Создаем результирующий файл
                    String resultFileName = createResultLogFile(taskId, dateTimeStr, filteredLines);

                    taskFilePaths.put(taskId, resultFileName);
                    taskStatuses.put(taskId, "COMPLETED");
                } catch (Exception e) {
                    taskStatuses.put(taskId, "FAILED: " + e.getMessage());
                    log.error("Log export failed", e);
                    throw new CompletionException(e);
                }
            });

        } catch (BadRequestException e) {
            return CompletableFuture.failedFuture(e);
        }

        return CompletableFuture.completedFuture(taskId);
    }

    private List<Path> findLogFilesForDateTime(LocalDateTime dateTime) throws IOException {
        LocalDate date = dateTime.toLocalDate();
        List<Path> result = new ArrayList<>();
        boolean exactTimeMatch = dateTime.getHour() != 0 || dateTime.getMinute() != 0;

        log.info("Searching logs for: {} (exactTimeMatch: {})", dateTime, exactTimeMatch);

        // 1. Проверяем архивные файлы
        try (Stream<Path> paths = Files.list(Paths.get(LOG_DIR))) {
            paths.filter(path -> {
                    String fileName = path.getFileName().toString();
                    boolean isLogFile = fileName.startsWith(LOG_PREFIX) &&
                        fileName.endsWith(LOG_SUFFIX) &&
                        !fileName.equals("app.log");

                    if (isLogFile) {
                        log.debug("Checking archive file: {}", fileName);
                    }
                    return isLogFile;
                })
                .filter(path -> {
                    boolean matches = isArchiveFileForDateTime(path, dateTime, exactTimeMatch);
                    if (matches) {
                        log.info("Matched archive file: {}", path.getFileName());
                    }
                    return matches;
                })
                .forEach(result::add);
        }

        // 2. Проверяем текущий app.log
        Path currentLog = Paths.get(LOG_DIR + "app.log");
        if (Files.exists(currentLog)) {
            log.debug("Checking current app.log");
            if (isCurrentLogRelevant(currentLog, dateTime, exactTimeMatch)) {
                log.info("Matched current app.log");
                result.add(currentLog);
            }
        }

        log.info("Total matched files: {}", result.size());
        return result;
    }

    private boolean isArchiveFileForDateTime(Path path, LocalDateTime dateTime, boolean exactTimeMatch) {
        try {
            String fileName = path.getFileName().toString();
            String datePart = fileName.substring(
                LOG_PREFIX.length(),
                fileName.length() - LOG_SUFFIX.length()
            );

            LocalDateTime fileDateTime = LocalDateTime.parse(datePart, FILE_DATE_FORMATTER);

            if (exactTimeMatch) {
                return fileDateTime.equals(dateTime);
            } else {
                return fileDateTime.toLocalDate().equals(dateTime.toLocalDate());
            }
        } catch (Exception e) {
            log.debug("Skipping invalid log file: {}", path.getFileName(), e);
            return false;
        }
    }

    private boolean isCurrentLogRelevant(Path logFile, LocalDateTime dateTime, boolean exactTimeMatch) {
        try {
            // Для app.log проверяем только дату (без точного времени)
            LocalDateTime lastModified = Files.getLastModifiedTime(logFile)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

            return exactTimeMatch ?
                lastModified.equals(dateTime) :
                lastModified.toLocalDate().equals(dateTime.toLocalDate());
        } catch (IOException e) {
            log.warn("Failed to check current log file relevance", e);
            return false;
        }
    }

    private List<String> filterLogEntries(List<Path> logFiles, LocalDateTime targetDateTime) {
        LocalDate targetDate = targetDateTime.toLocalDate();
        boolean exactTimeMatch = targetDateTime.getHour() != 0 || targetDateTime.getMinute() != 0;

        log.info("Filtering entries for: {} (exact: {})", targetDateTime, exactTimeMatch);

        List<String> filteredLines = logFiles.stream()
            .flatMap(path -> {
                try {
                    log.info("Processing file: {}", path.getFileName());
                    return Files.lines(path)
                        .filter(Objects::nonNull)
                        .filter(line -> !line.trim().isEmpty())
                        .filter(line -> isLineMatchesDateTime(line, targetDateTime, exactTimeMatch))
                        .peek(line -> log.debug("Matched line: {}", line));
                } catch (IOException e) {
                    log.error("Failed to read file: {}", path, e);
                    return Stream.empty();
                }
            })
            .collect(Collectors.toList());

        if (filteredLines.isEmpty()) {
            log.warn("No matches found, trying relaxed time matching");
            filteredLines = tryRelaxedMatching(logFiles, targetDateTime);
        }

        return filteredLines;
    }

    private List<String> tryRelaxedMatching(List<Path> logFiles, LocalDateTime targetDateTime) {
        return logFiles.stream()
            .flatMap(path -> {
                try {
                    return Files.lines(path)
                        .filter(line -> line.contains(targetDateTime.format(LOG_DATE_FORMATTER).substring(0, 16)));
                } catch (IOException e) {
                    return Stream.empty();
                }
            })
            .collect(Collectors.toList());
    }

    private boolean isLineMatchesDateTime(String line, LocalDateTime targetDateTime, boolean exactTimeMatch) {
        if (line.length() < 19) return false;

        try {
            String datePart = line.substring(0, 19);
            LocalDateTime logDateTime = LocalDateTime.parse(datePart, LOG_DATE_FORMATTER);

            if (exactTimeMatch) {
                // Сравниваем с точностью до минуты
                return logDateTime.getYear() == targetDateTime.getYear() &&
                    logDateTime.getMonth() == targetDateTime.getMonth() &&
                    logDateTime.getDayOfMonth() == targetDateTime.getDayOfMonth() &&
                    logDateTime.getHour() == targetDateTime.getHour() &&
                    logDateTime.getMinute() == targetDateTime.getMinute();
            } else {
                return logDateTime.toLocalDate().equals(targetDateTime.toLocalDate());
            }
        } catch (DateTimeParseException e) {
            log.debug("Skipping ... log line: {}", line.substring(0, Math.min(line.length(), 50)) + "...");
            return false;
        }
    }

    private String createResultLogFile(String taskId, String date, List<String> lines)
        throws IOException {

        String resultFileName = String.format(
            "%s%s%s_%s.log",
            LOG_DIR, "export_", date, taskId
        );

        Path resultPath = Paths.get(resultFileName);
        Files.createDirectories(resultPath.getParent());

        // Записываем с информационным заголовком
        List<String> content = new ArrayList<>();
        content.add("=== Log export for date: " + date + " ===");
        content.add("=== Generated at: " + LocalDateTime.now() + " ===");
        content.add("=== Total entries: " + lines.size() + " ===\n");
        content.addAll(lines);

        Files.write(resultPath, content, StandardOpenOption.CREATE);
        return resultFileName;
    }

    public String getTaskStatus(String taskId) {
        return taskStatuses.getOrDefault(taskId, "NOT_FOUND");
    }

    public File getTaskFile(String taskId) {
        if ("PROCESSING".equals(taskStatuses.get(taskId))) {
            throw new ConflictException("Log file has status \"PROCESSING\"");
        }
        String path = taskFilePaths.get(taskId);
        return path != null ? new File(path) : null;
    }
}