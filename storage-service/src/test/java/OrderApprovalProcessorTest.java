import application.service.AssemblyOrderService;
import application.service.OrderApprovalProcessor;
import domain.enums.AssemblyOrderStatus;
import domain.event.OrderApprovedEvent;
import domain.event.OrderSentForApprovalEvent;
import domain.model.AssemblyOrder;
import domain.model.SparePart;
import domain.repository.CarRepository;
import domain.repository.SparePartRepository;
import infrastructure.messaging.OrderDecisionPublisher;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderApprovalProcessorTest {

    private final AssemblyOrderService assemblyOrderService = mock(AssemblyOrderService.class);
    private final CarRepository carRepository = mock(CarRepository.class);
    private final SparePartRepository sparePartRepository = mock(SparePartRepository.class);
    private final OrderDecisionPublisher orderDecisionPublisher = mock(OrderDecisionPublisher.class);
    private final OrderApprovalProcessor processor = new OrderApprovalProcessor(
            assemblyOrderService,
            carRepository,
            sparePartRepository,
            orderDecisionPublisher
    );

    @Test
    void createsAssemblyOrderAndPublishesApprovedDecision() {
        UUID orderId = UUID.randomUUID();
        UUID traceId = UUID.randomUUID();
        OrderSentForApprovalEvent event = new OrderSentForApprovalEvent(
                orderId,
                traceId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                "CUSTOM",
                "BMW 320i",
                Instant.now()
        );

        AssemblyOrder created = assemblyOrder(orderId, "CUSTOM", AssemblyOrderStatus.CREATED);
        AssemblyOrder assembled = assemblyOrder(orderId, "CUSTOM", AssemblyOrderStatus.ASSEMBLED);

        when(assemblyOrderService.findBySourceOrderIdAndSourceOrderType(orderId, "CUSTOM"))
                .thenReturn(Optional.empty());
        when(assemblyOrderService.create(orderId, "CUSTOM", "BMW 320i", Set.of(), null))
                .thenReturn(created);
        when(assemblyOrderService.updateStatus(created.getId(), AssemblyOrderStatus.ASSEMBLED))
                .thenReturn(assembled);
        when(sparePartRepository.findByCompatibleCarModel("BMW 320i"))
                .thenReturn(List.of(new SparePart()));

        processor.process(event);

        ArgumentCaptor<OrderApprovedEvent> captor = ArgumentCaptor.forClass(OrderApprovedEvent.class);
        verify(orderDecisionPublisher).publishApproved(captor.capture());

        assertThat(captor.getValue().orderId()).isEqualTo(orderId);
        assertThat(captor.getValue().traceId()).isEqualTo(traceId);
        assertThat(captor.getValue().orderType()).isEqualTo("CUSTOM");
    }

    @Test
    void duplicateEventDoesNotCreateSecondAssemblyOrder() {
        UUID orderId = UUID.randomUUID();
        UUID traceId = UUID.randomUUID();
        OrderSentForApprovalEvent event = new OrderSentForApprovalEvent(
                orderId,
                traceId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                "STOCK",
                UUID.randomUUID().toString(),
                Instant.now()
        );

        AssemblyOrder existing = assemblyOrder(orderId, "STOCK", AssemblyOrderStatus.ASSEMBLED);

        when(assemblyOrderService.findBySourceOrderIdAndSourceOrderType(orderId, "STOCK"))
                .thenReturn(Optional.of(existing));

        processor.process(event);

        verify(assemblyOrderService, never()).create(any(), any(), any(), any(), any());
        verify(assemblyOrderService, never()).updateStatus(any(), eq(AssemblyOrderStatus.ASSEMBLED));
        verify(orderDecisionPublisher).publishApproved(any(OrderApprovedEvent.class));
    }

    private AssemblyOrder assemblyOrder(UUID sourceOrderId, String sourceOrderType, AssemblyOrderStatus status) {
        AssemblyOrder order = new AssemblyOrder();
        order.setId(UUID.randomUUID());
        order.setSourceOrderId(sourceOrderId);
        order.setSourceOrderType(sourceOrderType);
        order.setStatus(status);
        return order;
    }
}
