package nl.tjonahen.resto.diner.order.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import nl.tjonahen.resto.DinerApplication;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * OrderStatusBroker is a amqp message broker that publishes and subscribes to
 * status update messages
 */
@Slf4j
@Service
public class OrderStatusBroker {

    /**
     * The order status update message
     */
    @Getter
    @Setter
    @NoArgsConstructor 
    @AllArgsConstructor
    private static class OrderStatusMessage {
        private Long id;
        private String msg;
    }
    
    private final RabbitTemplate rabbitTemplate;
    private final OrderStatusWebSocketHandler orderStatusWebSocketHandler;

    public OrderStatusBroker(RabbitTemplate rabbitTemplate, OrderStatusWebSocketHandler orderStatusWebSocketHandler) {
        this.rabbitTemplate = rabbitTemplate;
        this.orderStatusWebSocketHandler = orderStatusWebSocketHandler;
    }

    public void sendStatusUpdate(Long id, String msg) throws IOException, OrderNotFoundException {
        rabbitTemplate.convertAndSend(DinerApplication.DINER_EXCHANGE, DinerApplication.DINER_KEY,
                new OrderStatusMessage(id, msg));

    }

    @RabbitListener(queues = DinerApplication.DINER_QUEUE)
    public void receiveStatusUpdate(final Message message) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        OrderStatusMessage orderStatus = objectMapper.readValue(message.getBody(), OrderStatusMessage.class);
        try {
            orderStatusWebSocketHandler.sendStatus(orderStatus.getId(), orderStatus.getMsg());
        } catch (OrderNotFoundException ex) {
            log.info("Unable to update order status for order {} with message {}", orderStatus.getId(), orderStatus.getMsg());
        }
    }

}
