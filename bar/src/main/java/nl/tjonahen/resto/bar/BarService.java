package nl.tjonahen.resto.bar;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import nl.tjonahen.resto.BarApplication;
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
public class BarService {

    @Value("${dinerurl:http://localhost:8080/api/order}")
    private String dinerUrl;

    private final RestTemplate restTemplate;

    public BarService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private final static List<Drink> DRINKS = Arrays.asList(
            Drink.builder().ref("1").name("Coffee").description("Regular coffee").price(250L).build(),
            Drink.builder().ref("2").name("Chocolato").description("Chocolate espresso with milk").price(450L).build(),
            Drink.builder().ref("3").name("Corretto").description("Whiskey and coffee").price(500L).build(),
            Drink.builder().ref("4").name("Iced tea").description("Hot tea, except not hot").price(300L).build(),
            Drink.builder().ref("5").name("Soda").description("Coke, Sprite, Fanta, etc.").price(250L).build());

    public List<Drink> getAllDrinks() {
        return DRINKS;
    }

    @RabbitListener(queues = BarApplication.BAR_QUEUE)
    public void receiveDrink(final Message message) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        CouponMessage drinks = objectMapper.readValue(message.getBody(), CouponMessage.class);
        log.info("message received: {}", drinks.getItems().length);

        restTemplate.postForLocation(String.format("%s/%d/drinks",dinerUrl, drinks.getOrderid()), Void.class);
    }
}
