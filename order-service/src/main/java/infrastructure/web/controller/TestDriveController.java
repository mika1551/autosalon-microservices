package infrastructure.web.controller;

import application.service.TestDriveService;
import infrastructure.security.CurrentUserProvider;
import infrastructure.web.dto.TestDriveRequestDto;
import infrastructure.web.dto.request.TestDriveCreateRequest;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/test-drives")

public class TestDriveController {

    private final TestDriveService service;
    private final ModelMapper mapper;
    private final CurrentUserProvider currentUserProvider;

    public TestDriveController(TestDriveService service, ModelMapper mapper, CurrentUserProvider currentUserProvider) {
        this.service = service;
        this.mapper = mapper;
        this.currentUserProvider = currentUserProvider;
    }

    @PostMapping
    public TestDriveRequestDto create(@RequestBody TestDriveCreateRequest request) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();
        return mapper.map(
                service.createTestDriveRequest(currentUserId, request.carId(), request.date()),
                TestDriveRequestDto.class
        );
    }

    @GetMapping("/{id}")
    public TestDriveRequestDto getById(@PathVariable UUID id) {
        return mapper.map(service.getById(id), TestDriveRequestDto.class);
    }

    @GetMapping
    public List<TestDriveRequestDto> getAll(
            @RequestParam(required = false) UUID clientId,
            @RequestParam(required = false) UUID carId
    ) {
        var list = (clientId != null) ? service.getByClientId(clientId)
                : (carId != null) ? service.getByCarId(carId)
                : service.getAll();
        return list.stream().map(r -> mapper.map(r, TestDriveRequestDto.class)).toList();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.deleteById(id);
    }
}
