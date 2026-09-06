package infrastructure.messaging;

import domain.enums.CustomOrderStatus;
import domain.enums.StockOrderStatus;
import domain.event.OrderApprovedEvent;
import domain.event.OrderRejectedEvent;
import domain.repository.CustomOrderRepository;
import domain.repository.StockOrderRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OrderDecisionListener {

    private final CustomOrderRepository customOrderRepository;
    private final StockOrderRepository stockOrderRepository;

    public OrderDecisionListener(
            CustomOrderRepository customOrderRepository,
            StockOrderRepository stockOrderRepository
    ) {
        this.customOrderRepository = customOrderRepository;
        this.stockOrderRepository = stockOrderRepository;
    }

    @Transactional
    @RabbitListener(queues = RabbitMqConfig.ORDER_APPROVED_QUEUE)
    public void handleApproved(OrderApprovedEvent event) {
        if ("CUSTOM".equals(event.orderType())) {
            customOrderRepository.findById(event.orderId()).ifPresent(order -> {
                order.setStatus(CustomOrderStatus.READY_FOR_DELIVERY);
                customOrderRepository.save(order);
            });
            return;
        }

        if ("STOCK".equals(event.orderType())) {
            stockOrderRepository.findById(event.orderId()).ifPresent(order -> {
                order.setStatus(StockOrderStatus.READY_FOR_DELIVERY);
                stockOrderRepository.save(order);
            });
        }
    }

    @Transactional
    @RabbitListener(queues = RabbitMqConfig.ORDER_REJECTED_QUEUE)
    public void handleRejected(OrderRejectedEvent event) {
        if ("CUSTOM".equals(event.orderType())) {
            customOrderRepository.findById(event.orderId()).ifPresent(order -> {
                order.setStatus(CustomOrderStatus.CANCELED);
                customOrderRepository.save(order);
            });
            return;
        }

        if ("STOCK".equals(event.orderType())) {
            stockOrderRepository.findById(event.orderId()).ifPresent(order -> {
                order.setStatus(StockOrderStatus.CANCELED);
                stockOrderRepository.save(order);
            });
        }
    }
}
