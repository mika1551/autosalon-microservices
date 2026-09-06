package infrastructure.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE = "autosalon.exchange";

    public static final String ORDER_SENT_QUEUE = "order.sent-for-approval.queue";
    public static final String ORDER_APPROVED_QUEUE = "order.approved.queue";
    public static final String ORDER_REJECTED_QUEUE = "order.rejected.queue";

    public static final String ORDER_SENT_ROUTING_KEY = "order.sent-for-approval";
    public static final String ORDER_APPROVED_ROUTING_KEY = "order.approved";
    public static final String ORDER_REJECTED_ROUTING_KEY = "order.rejected";

    @Bean
    public DirectExchange autosalonExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue orderSentQueue() {
        return new Queue(ORDER_SENT_QUEUE, true);
    }

    @Bean
    public Queue orderApprovedQueue() {
        return new Queue(ORDER_APPROVED_QUEUE, true);
    }

    @Bean
    public Queue orderRejectedQueue() {
        return new Queue(ORDER_REJECTED_QUEUE, true);
    }

    @Bean
    public Binding orderSentBinding(Queue orderSentQueue, DirectExchange autosalonExchange) {
        return BindingBuilder.bind(orderSentQueue).to(autosalonExchange).with(ORDER_SENT_ROUTING_KEY);
    }

    @Bean
    public Binding orderApprovedBinding(Queue orderApprovedQueue, DirectExchange autosalonExchange) {
        return BindingBuilder.bind(orderApprovedQueue).to(autosalonExchange).with(ORDER_APPROVED_ROUTING_KEY);
    }

    @Bean
    public Binding orderRejectedBinding(Queue orderRejectedQueue, DirectExchange autosalonExchange) {
        return BindingBuilder.bind(orderRejectedQueue).to(autosalonExchange).with(ORDER_REJECTED_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
