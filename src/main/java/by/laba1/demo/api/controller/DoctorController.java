package by.laba1.demo.api.controller;

import by.laba1.demo.api.dto.doctor.CreateDoctorDto;
import by.laba1.demo.api.dto.doctor.GetDoctorDto;
import by.laba1.demo.core.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctors", description = "Doctor management API")
@Slf4j
public class DoctorController {
    private final DoctorService doctorService;

    @Operation(summary = "Create a new doctor", description = "Adds a new doctor to the system")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Doctor successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<GetDoctorDto> create(@Valid @RequestBody CreateDoctorDto dto) {
        GetDoctorDto createdDoctor = doctorService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDoctor);
    }

    @Operation(summary = "Get all doctors", description = "Returns a list of all doctors")
    @ApiResponse(responseCode = "200", description = "List of all doctors successfully received")
    @GetMapping
    public ResponseEntity<List<GetDoctorDto>> getAll() {
        return ResponseEntity.ok(doctorService.getAll());
    }

    @Operation(summary = "Get doctor by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Doctor found"),
        @ApiResponse(responseCode = "400", description = "Specialization are required")
    })
    @GetMapping("/{id}")
    public ResponseEntity<GetDoctorDto> getById(@PathVariable long id) {
        return ResponseEntity.ok(doctorService.getById(id));
    }

    @Operation(summary = "Find available doctors", description = "Finds doctors available at a given time for a specific specialization")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of all doctors are found"),
        @ApiResponse(responseCode = "404", description = "List of all doctors are not found")
    })
    @GetMapping("/available")
    public List<GetDoctorDto> getAvailable(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime appointmentTime,
        @RequestParam String specialization
    ) {
        return doctorService.findAvailable(appointmentTime, specialization);
    }

    @Operation(summary = "Update doctor", description = "Updates an existing doctor's details")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Doctor updated"),
        @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    @PutMapping("/{id}")
    public GetDoctorDto update(
        @PathVariable long id,
        @Valid @RequestBody CreateDoctorDto dto) {
        return doctorService.update(id, dto);
    }

    @Operation(summary = "Delete doctor", description = "Removes a doctor from the system")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Doctor deleted"),
        @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        doctorService.delete(id);
    }
}

