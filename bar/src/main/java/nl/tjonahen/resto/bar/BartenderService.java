package nl.tjonahen.resto.bar;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import nl.tjonahen.resto.BartenderApplication;
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
public class BartenderService {

    @Value("${diner.url}")
    private String dinerUrl;

    private final RestTemplate restTemplate;

    public BartenderService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private final static List<Drink> DRINKS = Arrays.asList(
            Drink.builder().ref("1").preparationTime(30L).name("Coffee").description("Regular coffee").price(250L).build(),
            Drink.builder().ref("2").preparationTime(30L).name("Chocolato").description("Chocolate espresso with milk").price(450L).build(),
            Drink.builder().ref("3").preparationTime(30L).name("Corretto").description("Whiskey and coffee").price(500L).build(),
            Drink.builder().ref("4").preparationTime(10L).name("Iced tea").description("Hot tea, except not hot").price(300L).build(),
            Drink.builder().ref("5").preparationTime(30L).name("Soda").description("Coke, Sprite, Fanta, etc.").price(250L).build());

    public List<Drink> getAllDrinks() {
        return DRINKS;
    }

    @RabbitListener(queues = BartenderApplication.BARTENDER_QUEUE)
    public void receiveDrink(final Message message) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        CouponMessage drinks = objectMapper.readValue(message.getBody(), CouponMessage.class);
        log.info("Prepare drinks for order {}", drinks.getOrderid());

        for (Coupon drink : drinks.getItems()) {
            DRINKS.stream().filter(d -> d.getRef().equals(drink.getRef())).mapToLong(d -> d.getPreparationTime()).forEach(d -> {
                try {
                    Thread.sleep(d*100*drink.getQuantity());
                } catch (InterruptedException ex) {
                    log.error("Interupted while making beverage: ", ex);
                }
            });
        }
        log.info("Drinks are ready for service for order {}", drinks.getOrderid());
        restTemplate.postForLocation(String.format("%s/api/order/%d/serve/drinks",dinerUrl, drinks.getOrderid()), Void.class);
    }
}
