package by.laba1.demo.api.controller;

import by.laba1.demo.api.dto.patient.CreatePatientDto;
import by.laba1.demo.api.dto.patient.GetPatientDto;
import by.laba1.demo.core.service.PatientService;
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
public class PatientController {
    private final PatientService patientService;

    @GetMapping
    public List<GetPatientDto> getByFilter(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String phoneNumber
    ) {
        return patientService.getPatientsByFilter(name, phoneNumber);
    }

    @GetMapping("/{id}")
    public GetPatientDto getById(@PathVariable long id) {
        return patientService.getPatientById(id);
    }

    @PostMapping
    public GetPatientDto create(@RequestBody CreatePatientDto dto) {
        return patientService.createPatient(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GetPatientDto> update(
        @PathVariable long id,
        @RequestBody CreatePatientDto dto) {
        return ResponseEntity.ok(patientService.updatePatient(id, dto));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        patientService.deletePatient(id);
    }
}
