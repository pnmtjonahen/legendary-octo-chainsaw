
package nl.tjonahen.resto.diner.order.service;

import java.util.List;
import nl.tjonahen.resto.diner.order.model.OrderItem;
import nl.tjonahen.resto.diner.order.model.OrderItemType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@Service
public class OrderService {

    private final RestTemplate  restTemplate;
    @Value("${dishesurl:http://localhost:18080/api/dish}")
    private String dishesUrl;
    @Value("${drinksurl:http://localhost:28080/api/drink}")
    private String drinksUrl;

    public OrderService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    @Async
    public void processDrinks(List<OrderItem> drinks) {
        
    } 

    @Async
    public void processDishes(List<OrderItem> dishes) {
        
    } 
    
    public List<Dish> getDishes() {
        return restTemplate.getForObject(dishesUrl, List.class);
    }

    public List<Drink> getDrinks() {
        return restTemplate.getForObject(drinksUrl, List.class);
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
