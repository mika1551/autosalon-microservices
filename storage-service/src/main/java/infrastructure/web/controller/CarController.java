package infrastructure.web.controller;

import application.service.CarCatalogService;
import domain.model.Car;
import infrastructure.web.dto.CarDto;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cars")

public class CarController {

    private final CarCatalogService service;
    private final ModelMapper mapper;

    public CarController(CarCatalogService service, ModelMapper mapper)
    {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping("/{id}")
    public CarDto getCarById(@PathVariable UUID id)
    {
        return mapper.map((service.getById(id)), CarDto.class);
    }

    @GetMapping
    public List<CarDto> getAll(
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String bodyType,
            @RequestParam(required = false) String fuelType,
            @RequestParam(required = false) Integer minPower,
            @RequestParam(required = false) Integer maxPower,
            @RequestParam(required = false) Integer minEngineVolume,
            @RequestParam(required = false) Integer maxEngineVolume,
            @RequestParam(required = false) String transmissionType,
            @RequestParam(required = false) String driveType,
            @RequestParam(required = false) String color

    ) {
        return service.filterCars(
                minPrice,maxPrice, brand, model, bodyType, fuelType,
                minPower, maxPower, minEngineVolume, maxEngineVolume,
                transmissionType, driveType, color
        ).stream().map(c -> mapper.map(c, CarDto.class)).toList();
    }

    @PostMapping
    public void create(@RequestBody CarDto dto)
    {
        Car car = mapper.map(dto, Car.class);
        service.save(car);
    }

    @DeleteMapping("/{id}")
    public void deleteCar(@PathVariable UUID id)
    {
        service.delete(id);
    }

}
