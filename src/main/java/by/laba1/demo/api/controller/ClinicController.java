package by.laba1.demo.api.controller;

import by.laba1.demo.api.dto.clinic.CreateClinicDto;
import by.laba1.demo.api.dto.clinic.GetClinicDto;
import by.laba1.demo.core.service.ClinicService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clinics")
@RequiredArgsConstructor
public class ClinicController {
    private final ClinicService clinicService;

    @PostMapping
    public GetClinicDto create(@RequestBody CreateClinicDto dto) {
        return clinicService.create(dto);
    }

    @GetMapping
    public List<GetClinicDto> getAll() {
        return clinicService.getAll();
    }

    @GetMapping("/{id}")
    public GetClinicDto getById(@PathVariable long id) {
        return clinicService.getById(id);
    }

    @PutMapping("/{id}")
    public GetClinicDto update(
        @PathVariable long id,
        @RequestBody CreateClinicDto dto) {
        return clinicService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        clinicService.delete(id);
    }
}