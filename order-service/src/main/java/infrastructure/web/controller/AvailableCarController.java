package infrastructure.web.controller;

import application.service.AvailableCarService;
import infrastructure.web.dto.AvailableCarDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cars")
public class AvailableCarController {

    private final AvailableCarService availableCarService;

    public AvailableCarController(AvailableCarService availableCarService) {
        this.availableCarService = availableCarService;
    }

    @GetMapping
    public List<AvailableCarDto> getAvailableCars() {
        return availableCarService.getAvailableCars();
    }

    @GetMapping("/{id}")
    public AvailableCarDto getAvailableCarById(@PathVariable UUID id) {
        return availableCarService.getAvailableCarById(id);
    }
}
