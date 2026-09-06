package infrastructure.web.controller;

import application.service.SparePartService;
import domain.model.SparePart;
import infrastructure.web.dto.SparePartDto;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/spare-parts")


public class SparePartController {

    private final SparePartService service;
    private final ModelMapper mapper;

    public SparePartController(SparePartService service, ModelMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping("/{id}")
    public SparePartDto get(@PathVariable UUID id) {
        return mapper.map(service.getById(id), SparePartDto.class);
    }

    @GetMapping
    public List<SparePartDto> getAll(@RequestParam(required = false) String category, @RequestParam(required = false) String brand, @RequestParam(required = false) String carModel) {
        var list = (carModel == null) ? service.getAll() : service.getByCompatibleCarModel(carModel);
        return list.stream().map(s -> mapper.map(s, SparePartDto.class)).toList();
    }

    @PostMapping
    public SparePartDto create(@RequestBody SparePartDto dto) {
        SparePart part = mapper.map(dto, SparePart.class);
        return mapper.map(service.add(part), SparePartDto.class);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.deleteById(id);
    }

}
