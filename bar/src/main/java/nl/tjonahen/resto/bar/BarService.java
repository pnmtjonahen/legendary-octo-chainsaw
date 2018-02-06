package nl.tjonahen.resto.bar;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import nl.tjonahen.resto.BarApplication;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@Slf4j
@Service
public class BarService {

    @RabbitListener(queues = BarApplication.BAR_QUEUE)
    public void receiveDrink(final Message message) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        CouponMessage drinks = objectMapper.readValue(message.getBody(), CouponMessage.class);
      log.info("message received: {}", drinks.getItems().length);
    }
}
