package infrastructure.messaging;

import domain.event.OrderApprovedEvent;
import domain.event.OrderRejectedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderDecisionPublisher {

    private final RabbitTemplate rabbitTemplate;

    public OrderDecisionPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishApproved(OrderApprovedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE,
                RabbitMqConfig.ORDER_APPROVED_ROUTING_KEY,
                event
        );
    }

    public void publishRejected(OrderRejectedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE,
                RabbitMqConfig.ORDER_REJECTED_ROUTING_KEY,
                event
        );
    }
}
