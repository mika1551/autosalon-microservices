package application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.enums.CustomOrderStatus;
import domain.enums.StockOrderStatus;
import domain.event.OrderSentForApprovalEvent;
import domain.exception.EntityNotFoundException;
import domain.model.CustomOrder;
import domain.model.OutboxEvent;
import domain.model.StockOrder;
import domain.repository.CustomOrderRepository;
import domain.repository.OutboxEventRepository;
import domain.repository.StockOrderRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class OrderApprovalService {

    private final CustomOrderRepository customOrderRepository;
    private final StockOrderRepository stockOrderRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public OrderApprovalService(
            CustomOrderRepository customOrderRepository,
            StockOrderRepository stockOrderRepository,
            OutboxEventRepository outboxEventRepository,
            ObjectMapper objectMapper
    ) {
        this.customOrderRepository = customOrderRepository;
        this.stockOrderRepository = stockOrderRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @security.isCustomOrderOwner(#id)")
    public CustomOrder payCustomOrder(UUID id) {
        CustomOrder order = customOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CustomOrder not found: " + id));

        order.setStatus(CustomOrderStatus.PAID);
        CustomOrder savedOrder = customOrderRepository.save(order);

        saveOutboxEvent(new OrderSentForApprovalEvent(
                savedOrder.getId(),
                UUID.randomUUID(),
                savedOrder.getClientId(),
                savedOrder.getManagerId(),
                "CUSTOM",
                savedOrder.getCarModel(),
                Instant.now()
        ));

        return savedOrder;
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @security.isStockOrderOwner(#id)")
    public StockOrder payStockOrder(UUID id) {
        StockOrder order = stockOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("StockOrder not found: " + id));

        order.setStatus(StockOrderStatus.PAID);
        StockOrder savedOrder = stockOrderRepository.save(order);

        saveOutboxEvent(new OrderSentForApprovalEvent(
                savedOrder.getId(),
                UUID.randomUUID(),
                savedOrder.getClientId(),
                savedOrder.getManagerId(),
                "STOCK",
                savedOrder.getCarId().toString(),
                Instant.now()
        ));

        return savedOrder;
    }

    private void saveOutboxEvent(OrderSentForApprovalEvent event) {
        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setAggregateId(event.orderId());
        outboxEvent.setTraceId(event.traceId());
        outboxEvent.setEventType("OrderSentForApproval");
        outboxEvent.setPayload(toJson(event));

        outboxEventRepository.save(outboxEvent);
    }

    private String toJson(Object event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Cannot serialize outbox event", ex);
        }
    }
}
