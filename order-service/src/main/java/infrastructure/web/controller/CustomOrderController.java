package infrastructure.web.controller;

import application.service.CustomOrderService;
import application.service.OrderApprovalService;
import infrastructure.security.CurrentUserProvider;
import infrastructure.web.dto.CustomOrderDto;
import infrastructure.web.dto.request.CustomOrderCreateRequest;
import infrastructure.web.dto.request.CustomOrderStatusRequest;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders/custom")
public class CustomOrderController {

    private final CustomOrderService service;
    private final OrderApprovalService orderApprovalService;
    private final ModelMapper mapper;
    private final CurrentUserProvider currentUserProvider;

    public CustomOrderController(
            CustomOrderService service,
            OrderApprovalService orderApprovalService,
            ModelMapper mapper,
            CurrentUserProvider currentUserProvider
    ) {
        this.service = service;
        this.orderApprovalService = orderApprovalService;
        this.mapper = mapper;
        this.currentUserProvider = currentUserProvider;
    }

    @PostMapping
    public CustomOrderDto create(@RequestBody CustomOrderCreateRequest request) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();
        return mapper.map(
                service.createOrder(currentUserId, request.carModel(), request.configurationId()),
                CustomOrderDto.class
        );
    }

    @GetMapping("/{id}")
    public CustomOrderDto getById(@PathVariable UUID id) {
        return mapper.map(service.getById(id), CustomOrderDto.class);
    }

    @GetMapping
    public List<CustomOrderDto> getAll() {
        UUID currentUserId = currentUserProvider.getCurrentUserId();
        return service.getVisibleOrders(currentUserId).stream().map(o -> mapper.map(o, CustomOrderDto.class)).toList();
    }

    @PostMapping("/{id}/pay")
    public CustomOrderDto pay(@PathVariable UUID id) {
        return mapper.map(orderApprovalService.payCustomOrder(id), CustomOrderDto.class);
    }

    @PatchMapping("/{id}/status")
    public CustomOrderDto updateStatus(@PathVariable UUID id, @RequestBody CustomOrderStatusRequest req) {
        return mapper.map(service.updateStatus(id, req.status()), CustomOrderDto.class);
    }

    @PatchMapping("/{id}/cancel")
    public CustomOrderDto cancel(@PathVariable UUID id) {
        return mapper.map(service.cancelOrder(id), CustomOrderDto.class);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.deleteById(id);
    }
}
