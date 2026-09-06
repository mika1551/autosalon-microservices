package infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.enums.OutboxStatus;
import domain.event.OrderSentForApprovalEvent;
import domain.model.OutboxEvent;
import domain.repository.OutboxEventRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component

public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public OutboxPublisher(
            OutboxEventRepository outboxEventRepository,
            RabbitTemplate rabbitTemplate,
            ObjectMapper objectMapper
    ) {
        this.outboxEventRepository = outboxEventRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay = 3000)
    public void publishNewEvents() {
        for (OutboxEvent event : outboxEventRepository.findByStatus(OutboxStatus.NEW)) {
            try {
                OrderSentForApprovalEvent payload = objectMapper.readValue(
                        event.getPayload(),
                        OrderSentForApprovalEvent.class
                );

                rabbitTemplate.convertAndSend(
                        RabbitMqConfig.EXCHANGE,
                        RabbitMqConfig.ORDER_SENT_ROUTING_KEY,
                        payload
                );

                event.setStatus(OutboxStatus.SENT);
                event.setSentAt(Instant.now());
                outboxEventRepository.save(event);
            } catch (Exception ex) {
                event.setStatus(OutboxStatus.FAILED);
                outboxEventRepository.save(event);
            }
        }
    }
}
