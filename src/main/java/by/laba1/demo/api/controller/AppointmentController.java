package by.laba1.demo.api.controller;

import by.laba1.demo.api.dto.appointment.CreateAppointmentDto;
import by.laba1.demo.api.dto.appointment.GetAppointmentDto;
import by.laba1.demo.core.service.AppointmentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;

    @PostMapping
    public GetAppointmentDto create(@RequestBody CreateAppointmentDto dto) {
        return appointmentService.create(dto);
    }

    @GetMapping
    public List<GetAppointmentDto> getAll() {
        return appointmentService.getAll();
    }

    @GetMapping("/{id}")
    public GetAppointmentDto getById(@PathVariable long id) {
        return appointmentService.getById(id);
    }

    @GetMapping("/filter")
    public List<GetAppointmentDto> getByPatientName(
        @RequestParam String patientName
    ) {
        return appointmentService.findByPatientName(patientName);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        appointmentService.delete(id);
    }
}
