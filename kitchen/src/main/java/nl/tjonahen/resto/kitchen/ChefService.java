package nl.tjonahen.resto.kitchen;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import nl.tjonahen.resto.ChefApplication;
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
public class ChefService {
    @Value("${diner.url}")
    private String dinerUrl;

    private final RestTemplate restTemplate;

    public ChefService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    private static final List<Dish> DISHES = Arrays.asList(
                Dish.builder().ref("1").preparationTime(60L).name("Bread Basket").description("Assortment of fresh baked fruit breads and muffins").price(550L).build(),
                Dish.builder().ref("2").preparationTime(120L).name("Honey Almond Granola with Fruits").description("Natural cereal of honey toasted oats, raisins, almonds and dates").price(700L).build(),
                Dish.builder().ref("3").preparationTime(120L).name("Belgian Waffle").description("Vanilla flavored batter with malted flour").price(750L).build(),
                Dish.builder().ref("4").preparationTime(120L).name("Scrambled eggs").description("Scra,mbled eggs, roasted red pepper and garlic, with green onions").price(750L).build(),
                Dish.builder().ref("5").preparationTime(60L).name("Blueberry Pancakes").description("With syrup, butter and lots of berries").price(850L).build()
        );
    
    public List<Dish> getAllDishes() {
        return DISHES;
    }
    
    @RabbitListener(queues = ChefApplication.CHEF_QUEUE)
    public void receiveDrink(final Message message) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        CouponMessage dishes = objectMapper.readValue(message.getBody(), CouponMessage.class);
        log.info("Prepare food for order {}", dishes.getOrderid());
        for (Coupon dish : dishes.getItems()) {
            DISHES.stream().filter(d -> d.getRef().equals(dish.getRef())).mapToLong(d -> d.getPreparationTime()).forEach(d -> {
                try {
                    Thread.sleep(d*100*dish.getQuantity());
                } catch (InterruptedException ex) {
                    log.error("Interupted while preparing food: ", ex);
                }
            });
        }
        log.info("Food is ready for service for order {}", dishes.getOrderid());
        restTemplate.postForLocation(String.format("%s/%d/dishes",dinerUrl, dishes.getOrderid()), Void.class);
        
    }
}
