package infrastructure.web.controller;

import application.service.OrderApprovalService;
import application.service.StockOrderService;
import infrastructure.security.CurrentUserProvider;
import infrastructure.web.dto.StockOrderDto;
import infrastructure.web.dto.request.StockOrderCreateRequest;
import infrastructure.web.dto.request.StockOrderStatusRequest;
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
@RequestMapping("/orders/stock")
public class StockOrderController {

    private final StockOrderService service;
    private final OrderApprovalService orderApprovalService;
    private final ModelMapper mapper;
    private final CurrentUserProvider currentUserProvider;

    public StockOrderController(
            StockOrderService service,
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
    public StockOrderDto create(@RequestBody StockOrderCreateRequest request) {
        return mapper.map(
                service.createOrder(request.carId(), currentUserProvider.getCurrentUserId()),
                StockOrderDto.class
        );
    }

    @PostMapping("/{id}/pay")
    public StockOrderDto pay(@PathVariable UUID id) {
        return mapper.map(orderApprovalService.payStockOrder(id), StockOrderDto.class);
    }

    @GetMapping("/{id}")
    public StockOrderDto getById(@PathVariable UUID id) {
        return mapper.map(service.getById(id), StockOrderDto.class);
    }

    @GetMapping
    public List<StockOrderDto> getAll() {
        UUID currentUserId = currentUserProvider.getCurrentUserId();
        var list = service.getVisibleOrders(currentUserId);
        return list.stream()
                .map(order -> mapper.map(order, StockOrderDto.class))
                .toList();
    }

    @PatchMapping("/{id}/status")
    public StockOrderDto updateStatus(@PathVariable UUID id, @RequestBody StockOrderStatusRequest request) {
        return mapper.map(service.updateStatus(id, request.status()), StockOrderDto.class);
    }

    @PatchMapping("/{id}/cancel")
    public StockOrderDto cancel(@PathVariable UUID id) {
        return mapper.map(service.cancelOrder(id), StockOrderDto.class);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.deleteById(id);
    }
}
