package nl.tjonahen.resto.diner.order.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import nl.tjonahen.resto.DinerApplication;
import nl.tjonahen.resto.diner.order.model.OrderItem;
import nl.tjonahen.resto.diner.order.model.OrderItemType;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@Slf4j
@Service
public class OrderService {
    @Value("${chef.url}")
    private String cherUrl;
    @Value("${bartender.url}")
    private String bartenderUrl;

    private final RestTemplate restTemplate;

    private final RabbitTemplate rabbitTemplate;

    public OrderService(RestTemplate restTemplate, RabbitTemplate rabbitTemplate) {
        this.restTemplate = restTemplate;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Async
    public void processDrinks(Long orderid, List<OrderItem> drinks) {
        if (drinks.isEmpty()) {
            return;
        }
        log.info("Sending ordered drinks to the bartender......");
        rabbitTemplate.convertAndSend(DinerApplication.BARTENDER_EXCHANGE, DinerApplication.BARTENDER_KEY,
                RequestedMessage.builder()
                        .items(drinks.stream().map(item -> MessageItem.builder().quantity(item.getQuantity()).ref(item.getRef()).build()).collect(Collectors.toList()))
                        .orderid(orderid)
                        .build());
    }

    @Async
    public void processDishes(Long orderid, List<OrderItem> dishes) {
        if (dishes.isEmpty()) {
            return;
        }
        log.info("Sending ordered dishes to the chef......");
        dishes.stream().forEach(item -> {
            HttpEntity<RequestedMessage> request = new HttpEntity<>(RequestedMessage.builder()
                    .items(Arrays.asList(MessageItem.builder().quantity(item.getQuantity()).ref(item.getRef()).id(item.getId()).build()))
                    .orderid(orderid)
                    .build());
            restTemplate.exchange(cherUrl + "/order", HttpMethod.POST, request, RequestedMessage.class);
        });
    }

    @HystrixCommand(fallbackMethod = "defaultDishes")
    public List<Dish> getDishes() {
        ResponseEntity<List<Dish>> getResponse
                = restTemplate.exchange(cherUrl + "/menu", HttpMethod.GET, null, new ParameterizedTypeReference<List<Dish>>() {
                });
        return getResponse.getBody();
    }
    public List<Dish> defaultDishes() { 
        return new ArrayList<>();
    }

    @HystrixCommand(fallbackMethod = "defaultDrinks")
    public List<Drink> getDrinks() {
        ResponseEntity<List<Drink>> getResponse
                = restTemplate.exchange(bartenderUrl + "/menu", HttpMethod.GET, null, new ParameterizedTypeReference<List<Drink>>() {
                });
        return getResponse.getBody();
    }
    
    public List<Drink> defaultDrinks() {
        return Arrays.asList(new Drink("water", "water", "complementary water", 0L));
    }
    
    public Long getPrice(OrderItem item) {
        if (item.getOrderItemType() == OrderItemType.DISH) {
            return restTemplate.getForObject(cherUrl + "/dish/" + item.getRef(), Dish.class).getPrice();
        }
        return restTemplate.getForObject(bartenderUrl + "/drink/" + item.getRef(), Dish.class).getPrice();
    }

    public String getName(OrderItem item) {
        if (item.getOrderItemType() == OrderItemType.DISH) {
            return restTemplate.getForObject(cherUrl + "/dish/" + item.getRef(), Dish.class).getName();
        }
        return restTemplate.getForObject(bartenderUrl + "/drink/" + item.getRef(), Dish.class).getName();

    }

}
