package nl.tjonahen.resto.bar;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
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

    @HystrixCommand
    public void serveDrink(final Long orderId) {
        log.info("Drinks are ready for service for order {}", orderId);
        restTemplate.postForLocation(String.format("%s/api/order/%d/serve/drinks",dinerUrl, orderId), Void.class);
    }
}
