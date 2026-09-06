package infrastructure.web.controller;

import application.service.CarConfigurationService;
import domain.enums.ComponentType;
import domain.model.CarConfiguration;
import infrastructure.web.dto.CarConfigurationDto;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/configurations")
public class CarConfigurationController {

    private final CarConfigurationService service;
    private final ModelMapper mapper;

    public CarConfigurationController(CarConfigurationService service, ModelMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping("/{id}")
    public CarConfigurationDto getById(@PathVariable UUID id) {
        return toDto(service.getById(id));
    }

    @GetMapping
    public List<CarConfigurationDto> getAll(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) List<UUID> optionIds,
            @RequestParam(required = false) List<ComponentType> componentTypes
    ) {
        return service.findAll(brand, optionIds, componentTypes)
                .stream().map(this::toDto).toList();
    }

    private CarConfigurationDto toDto(CarConfiguration entity) {
        CarConfigurationDto dto = mapper.map(entity, CarConfigurationDto.class);

        if (entity.getSelectedOptions() != null) {
            Map<ComponentType, UUID> map = entity.getSelectedOptions().entrySet().stream()
                    .collect(java.util.stream.Collectors.toMap(
                            Map.Entry::getKey,
                            e -> e.getValue().getId()
                    ));
            dto.setSelectedOptions(map);
        }
        return dto;
    }
}
