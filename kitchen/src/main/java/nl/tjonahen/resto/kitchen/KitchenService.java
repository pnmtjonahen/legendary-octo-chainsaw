package nl.tjonahen.resto.kitchen;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import nl.tjonahen.resto.KitchenApplication;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@Slf4j
@Service
public class KitchenService {
    @Value("${dinerurl:http://localhost:8080/api/order}")
    private String dinerUrl;

    private final RestTemplate restTemplate;

    public KitchenService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @RabbitListener(queues = KitchenApplication.KITCHEN_QUEUE)
    public void receiveDrink(final Message message) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        CouponMessage drinks = objectMapper.readValue(message.getBody(), CouponMessage.class);
        log.info("message received: {}", drinks.getItems().length);

        restTemplate.postForLocation(String.format("%s/%d/dishes",dinerUrl, drinks.getOrderid()), Void.class);
        
    }
}
