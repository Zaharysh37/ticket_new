package by.laba1.demo.api.controller;

import by.laba1.demo.api.dto.patient.CreatePatientDto;
import by.laba1.demo.api.dto.patient.GetPatientDto;
import by.laba1.demo.core.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Tag(name = "Patients", description = "Endpoints for managing patients")
public class PatientController {
    private final PatientService patientService;

    @Operation(summary = "Get patients by filter", description = "Retrieve patients by name and/or phone number")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Patients retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Patients not found")
    })
    @GetMapping
    public List<GetPatientDto> getByFilter(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String phoneNumber
    ) {
        return patientService.getPatientsByFilter(name, phoneNumber);
    }

    @Operation(summary = "Get patient by ID", description = "Retrieve a patient by their unique ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Patient retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @GetMapping("/{id}")
    public GetPatientDto getById(@PathVariable long id) {
        return patientService.getPatientById(id);
    }

    @Operation(summary = "Create a new patient", description = "Register a new patient with name and phone number")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Patient created successfully"),
        @ApiResponse(responseCode = "409", description = "Patient with the same phone number already exists")
    })
    @PostMapping
    public GetPatientDto create(@Valid @RequestBody CreatePatientDto dto) {
        return patientService.createPatient(dto);
    }

    @Operation(summary = "Update a patient", description = "Update an existing patient by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Patient updated successfully"),
        @ApiResponse(responseCode = "404", description = "Patient not found"),
        @ApiResponse(responseCode = "409", description = "Patient with the same phone number already exists")
    })
    @PutMapping("/{id}")
    public ResponseEntity<GetPatientDto> update(
        @PathVariable long id,
        @Valid @RequestBody CreatePatientDto dto) {
        return ResponseEntity.ok(patientService.updatePatient(id, dto));
    }

    @Operation(summary = "Delete a patient", description = "Remove a patient from the system")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Patient deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}

