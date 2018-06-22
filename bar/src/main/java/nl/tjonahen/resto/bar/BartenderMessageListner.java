package nl.tjonahen.resto.bar;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import nl.tjonahen.resto.BartenderApplication;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@Slf4j
@Service
public class BartenderMessageListner {

    private final BartenderService bartenderService;
    
    public BartenderMessageListner(BartenderService bartenderService) {
        this.bartenderService = bartenderService;
    }


    @RabbitListener(queues = BartenderApplication.BARTENDER_QUEUE)
    public void receiveDrink(final Message message) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        CouponMessage drinks = objectMapper.readValue(message.getBody(), CouponMessage.class);
        log.info("Prepare drinks for order {}", drinks.getOrderid());

        for (Coupon drink : drinks.getItems()) {
            bartenderService.getAllDrinks().stream().filter(d -> d.getRef().equals(drink.getRef())).mapToLong(d -> d.getPreparationTime()).forEach(d -> {
                try {
                    Thread.sleep(d*100*drink.getQuantity());
                } catch (InterruptedException ex) {
                    log.error("Interupted while making beverage: ", ex);
                }
            });
        }
        bartenderService.serveDrink(drinks.getOrderid());
    }
}
