package nl.tjonahen.resto.diner.order.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.tjonahen.resto.DinerApplication;
import nl.tjonahen.resto.diner.order.model.OrderItem;
import nl.tjonahen.resto.diner.order.model.OrderItemType;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;


/**
 *
 * @author Philippe Tjon - A - Hen
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private static final String WATER = "water";

    @Value("${chef.url}")
    private String chefUrl;
    @Value("${bartender.url}")
    private String bartenderUrl;

    private final RestTemplate restTemplate;

    private final RabbitTemplate rabbitTemplate;
    
    private final WebClient.Builder webClientBuilder;

    @Async
    @CircuitBreaker(name = "processdrinks")
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
    @CircuitBreaker(name = "processdishes")
    public void processDishes(Long orderid, List<OrderItem> dishes) {
        if (dishes.isEmpty()) {
            return;
        }
        log.info("Sending ordered dishes to the chef({})......", chefUrl);
        dishes.stream().forEach(item -> {
            HttpEntity<RequestedMessage> request = new HttpEntity<>(RequestedMessage.builder()
                    .items(Arrays.asList(MessageItem.builder().quantity(item.getQuantity()).ref(item.getRef()).id(item.getId()).build()))
                    .orderid(orderid)
                    .build());
            restTemplate.exchange(chefUrl + "/api/order", HttpMethod.POST, request, RequestedMessage.class);
        });
    }

    @CircuitBreaker(name="getdishes", fallbackMethod = "defaultDishes")
    public List<Dish> getDishes() {
        return restTemplate.exchange(chefUrl + "/api/menu",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Dish>>() {}).getBody();
    }

    public List<Dish> defaultDishes(Exception t) {
        log.error("getDishes failed with {}", t.getMessage());
        return new ArrayList<>();
    }

    @CircuitBreaker(name="getdrinks", fallbackMethod = "defaultDrinks")
    public Flux<Drink> getDrinks() {
        return webClientBuilder.build().get().uri(bartenderUrl + "/api/menu").retrieve().bodyToFlux(Drink.class);
    }

    public Flux<Drink> defaultDrinks(Throwable t) {
        log.error("getDrinks failed with {}", t.getMessage());
        return Flux.fromIterable(Arrays.asList(new Drink(WATER, WATER, "complementary water", 0L)));
    }

    @CircuitBreaker(name = "getprice")
    public Long getPrice(OrderItem item) {
        if (item.getOrderItemType() == OrderItemType.DISH) {
            final var dish = restTemplate.getForObject(chefUrl + "/api/dish/" + item.getRef(), Dish.class);
            return dish == null ? 0L : dish.getPrice();
        }
        final var drink = restTemplate.getForObject(bartenderUrl + "/api/drink/" + item.getRef(), Drink.class);
        return drink == null ? 0L : drink.getPrice();
    }

    @CircuitBreaker(name="getname")
    public String getName(OrderItem item) {
        if (item.getOrderItemType() == OrderItemType.DISH) {
            final var dish = restTemplate.getForObject(chefUrl + "/api/dish/" + item.getRef(), Dish.class);
            return dish == null ? "" : dish.getName();
        }
        final var drink = restTemplate.getForObject(bartenderUrl + "/api/drink/" + item.getRef(), Drink.class);
        return drink == null ? "" : drink.getName();

    }

}

@Getter
@Builder
class MessageItem {
    private final Long id;
    private final String ref;
    private final Long quantity;
}

@Getter
@Builder
class RequestedMessage {
    private final Long orderid;
    private final List<MessageItem> items;
}
