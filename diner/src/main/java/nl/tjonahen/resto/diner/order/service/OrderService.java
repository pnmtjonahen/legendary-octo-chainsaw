package nl.tjonahen.resto.diner.order.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import nl.tjonahen.resto.DinerApplication;
import nl.tjonahen.resto.diner.order.model.OrderItem;
import nl.tjonahen.resto.diner.order.model.OrderItemType;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
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

    @Value("${dishesurl:http://localhost:18080/api/kitchen}")
    private String dishesUrl;
    @Value("${drinksurl:http://localhost:28080/api/bar}")
    private String drinksUrl;

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
        log.info("Sending ordered drinks to the bar......");
        rabbitTemplate.convertAndSend(DinerApplication.BAR_EXCHANGE, DinerApplication.BAR_KEY,
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
        log.info("Sending ordered dishes to the kitchen......");
        rabbitTemplate.convertAndSend(DinerApplication.KITCHEN_EXCHANGE, DinerApplication.KITCHEN_KEY,
                RequestedMessage.builder()
                        .items(dishes.stream().map(item -> MessageItem.builder().quantity(item.getQuantity()).ref(item.getRef()).build()).collect(Collectors.toList()))
                        .orderid(orderid)
                        .build());
    }

    public List<Dish> getDishes() {
        ResponseEntity<List<Dish>> getResponse
                = restTemplate.exchange(dishesUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<Dish>>() {
                });
        return getResponse.getBody();
    }

    public List<Drink> getDrinks() {
        ResponseEntity<List<Drink>> getResponse
                = restTemplate.exchange(drinksUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<Drink>>() {
                });
        return getResponse.getBody();
    }

    public Long getPrice(OrderItem item) {
        if (item.getOrderItemType() == OrderItemType.DISH) {
            return restTemplate.getForObject(dishesUrl + "/" + item.getRef(), Dish.class).getPrice();
        }
        return restTemplate.getForObject(drinksUrl + "/" + item.getRef(), Dish.class).getPrice();
    }

    public String getName(OrderItem item) {
        if (item.getOrderItemType() == OrderItemType.DISH) {
            return restTemplate.getForObject(dishesUrl + "/" + item.getRef(), Dish.class).getName();
        }
        return restTemplate.getForObject(drinksUrl + "/" + item.getRef(), Dish.class).getName();

    }
    

}
