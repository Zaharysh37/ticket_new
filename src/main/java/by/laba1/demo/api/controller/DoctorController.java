package by.laba1.demo.api.controller;

import by.laba1.demo.api.dto.doctor.CreateDoctorDto;
import by.laba1.demo.api.dto.doctor.GetDoctorDto;
import by.laba1.demo.core.service.DoctorService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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
public class DoctorController {
    private final DoctorService doctorService;

    @PostMapping
    public GetDoctorDto create(@RequestBody CreateDoctorDto dto) {
        return doctorService.create(dto);
    }

    @GetMapping
    public List<GetDoctorDto> getAll() {
        return doctorService.getAll();
    }

    @GetMapping("/{id}")
    public GetDoctorDto getById(@PathVariable long id) {
        return doctorService.getById(id);
    }

    @GetMapping("/available")
    public List<GetDoctorDto> getAvailable(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentTime,
        @RequestParam String specialization
    ) {
        return doctorService.findAvailable(appointmentTime, specialization);
    }

    @PutMapping("/{id}")
    public GetDoctorDto update(
        @PathVariable long id,
        @RequestBody CreateDoctorDto dto) {
        return doctorService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        doctorService.delete(id);
    }
}

