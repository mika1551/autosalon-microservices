package infrastructure.web.controller;

import application.service.AssemblyOrderService;
import infrastructure.web.dto.AssemblyOrderDto;
import infrastructure.web.dto.request.AssemblyOrderCreateRequest;
import infrastructure.web.dto.request.AssemblyOrderUpdateRequest;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/assembly-orders")
public class AssemblyOrderController {

    private final AssemblyOrderService service;
    private final ModelMapper mapper;

    public AssemblyOrderController(AssemblyOrderService service, ModelMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public AssemblyOrderDto create(@RequestBody AssemblyOrderCreateRequest request) {
        return mapper.map(
                service.create(
                        request.sourceOrderId(),
                        request.sourceOrderType(),
                        request.carModel(),
                        request.requiredComponentIds(),
                        request.warehouseAdminId()
                ),
                AssemblyOrderDto.class
        );
    }

    @GetMapping("/{id}")
    public AssemblyOrderDto getById(@PathVariable UUID id) {
        return mapper.map(service.getById(id), AssemblyOrderDto.class);
    }

    @GetMapping
    public List<AssemblyOrderDto> getAll() {
        return service.getAll().stream()
                .map(order -> mapper.map(order, AssemblyOrderDto.class))
                .toList();
    }

    @PatchMapping("/{id}/status")
    public AssemblyOrderDto updateStatus(@PathVariable UUID id, @RequestBody AssemblyOrderUpdateRequest request) {
        return mapper.map(service.updateStatus(id, request.status()), AssemblyOrderDto.class);
    }

    @PatchMapping("/{id}/warehouse-admin")
    public AssemblyOrderDto assignWarehouseAdmin(
            @PathVariable UUID id,
            @RequestBody AssemblyOrderUpdateRequest request
    ) {
        return mapper.map(service.assignWarehouseAdmin(id, request.warehouseAdminId()), AssemblyOrderDto.class);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.deleteById(id);
    }
}
