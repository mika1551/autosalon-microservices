package infrastructure.messaging;

import application.service.OrderApprovalProcessor;
import domain.event.OrderSentForApprovalEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderSentForApprovalListener {

    private final OrderApprovalProcessor orderApprovalProcessor;

    public OrderSentForApprovalListener(OrderApprovalProcessor orderApprovalProcessor) {
        this.orderApprovalProcessor = orderApprovalProcessor;
    }

    @RabbitListener(queues = RabbitMqConfig.ORDER_SENT_QUEUE)
    public void handle(OrderSentForApprovalEvent event) {
        orderApprovalProcessor.process(event);
    }
}
