import application.service.OrderApprovalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.enums.CustomOrderStatus;
import domain.enums.StockOrderStatus;
import domain.event.OrderSentForApprovalEvent;
import domain.model.CustomOrder;
import domain.model.OutboxEvent;
import domain.model.StockOrder;
import domain.repository.CustomOrderRepository;
import domain.repository.OutboxEventRepository;
import domain.repository.StockOrderRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderApprovalServiceTest {

    private final CustomOrderRepository customOrderRepository = mock(CustomOrderRepository.class);
    private final StockOrderRepository stockOrderRepository = mock(StockOrderRepository.class);
    private final OutboxEventRepository outboxEventRepository = mock(OutboxEventRepository.class);
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private final OrderApprovalService service = new OrderApprovalService(
            customOrderRepository,
            stockOrderRepository,
            outboxEventRepository,
            objectMapper
    );

    @Test
    void payCustomOrderChangesStatusAndCreatesOutboxEvent() throws Exception {
        UUID orderId = UUID.randomUUID();
        CustomOrder order = new CustomOrder(
                orderId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                "BMW 320i",
                null,
                CustomOrderStatus.CREATED
        );

        when(customOrderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(customOrderRepository.save(order)).thenReturn(order);

        service.payCustomOrder(orderId);

        ArgumentCaptor<OutboxEvent> captor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxEventRepository).save(captor.capture());

        OutboxEvent outboxEvent = captor.getValue();
        OrderSentForApprovalEvent event = objectMapper.readValue(
                outboxEvent.getPayload(),
                OrderSentForApprovalEvent.class
        );

        assertThat(order.getStatus()).isEqualTo(CustomOrderStatus.PAID);
        assertThat(outboxEvent.getAggregateId()).isEqualTo(orderId);
        assertThat(outboxEvent.getTraceId()).isNotNull();
        assertThat(event.orderId()).isEqualTo(orderId);
        assertThat(event.orderType()).isEqualTo("CUSTOM");
        assertThat(event.carModel()).isEqualTo("BMW 320i");
    }

    @Test
    void payStockOrderChangesStatusAndCreatesOutboxEvent() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID carId = UUID.randomUUID();
        StockOrder order = new StockOrder(
                orderId,
                UUID.randomUUID(),
                carId,
                UUID.randomUUID(),
                StockOrderStatus.CREATED
        );

        when(stockOrderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(stockOrderRepository.save(order)).thenReturn(order);

        service.payStockOrder(orderId);

        ArgumentCaptor<OutboxEvent> captor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxEventRepository).save(captor.capture());

        OutboxEvent outboxEvent = captor.getValue();
        OrderSentForApprovalEvent event = objectMapper.readValue(
                outboxEvent.getPayload(),
                OrderSentForApprovalEvent.class
        );

        assertThat(order.getStatus()).isEqualTo(StockOrderStatus.PAID);
        assertThat(outboxEvent.getAggregateId()).isEqualTo(orderId);
        assertThat(outboxEvent.getTraceId()).isNotNull();
        assertThat(event.orderId()).isEqualTo(orderId);
        assertThat(event.orderType()).isEqualTo("STOCK");
        assertThat(event.carModel()).isEqualTo(carId.toString());
    }
}
