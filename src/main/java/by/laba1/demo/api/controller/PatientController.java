package by.laba1.demo.api.controller;

import by.laba1.demo.core.dao.PatientRepository;
import by.laba1.demo.core.entities.Patient;
import by.laba1.demo.core.service.PatientService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/patients")
public class PatientController {
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public List<Patient> getAllPatients() {
        return patientService.getAl
    }
}
