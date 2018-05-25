package nl.tjonahen.resto.diner.order.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import nl.tjonahen.resto.DinerApplication;
import nl.tjonahen.resto.diner.order.model.OrderItem;
import nl.tjonahen.resto.diner.order.model.OrderItemType;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
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

//    @Value("${chef.url:http://chef/chef}")
//    private String chefUrl;
//    @Value("${bartender.url:http://localhost:28080/bartender}")
//    private String bartenderUrl;

    
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
        rabbitTemplate.convertAndSend(DinerApplication.CHEF_EXCHANGE, DinerApplication.CHEF_KEY,
                RequestedMessage.builder()
                        .items(dishes.stream().map(item -> MessageItem.builder().quantity(item.getQuantity()).ref(item.getRef()).build()).collect(Collectors.toList()))
                        .orderid(orderid)
                        .build());
    }

    public List<Dish> getDishes() {
        ResponseEntity<List<Dish>> getResponse
                = restTemplate.exchange("http://chef/chef", HttpMethod.GET, null, new ParameterizedTypeReference<List<Dish>>() {
                });
        return getResponse.getBody();
    }

    public List<Drink> getDrinks() {
        ResponseEntity<List<Drink>> getResponse
                = restTemplate.exchange("http://bartender/bartender", HttpMethod.GET, null, new ParameterizedTypeReference<List<Drink>>() {
                });
        return getResponse.getBody();
    }

    public Long getPrice(OrderItem item) {
        if (item.getOrderItemType() == OrderItemType.DISH) {
            return restTemplate.getForObject("http://chef/chef" + "/" + item.getRef(), Dish.class).getPrice();
        }
        return restTemplate.getForObject("http://bartender/bartender" + "/" + item.getRef(), Dish.class).getPrice();
    }

    public String getName(OrderItem item) {
        if (item.getOrderItemType() == OrderItemType.DISH) {
            return restTemplate.getForObject("http://chef/chef" + "/" + item.getRef(), Dish.class).getName();
        }
        return restTemplate.getForObject("http://bartender/bartender" + "/" + item.getRef(), Dish.class).getName();

    }
    

}
