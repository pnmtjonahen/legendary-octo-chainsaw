package nl.tjonahen.resto.diner.menu;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@RestController
@RequestMapping("/api/menu")
public class MenuController {

    private final RestTemplate restTemplate;
    
    @Value("${dishesurl:http://localhost:18080/api/dish}")
    private String dishesUrl;
    @Value("${drinksurl:http://localhost:28080/api/drink}")
    private String drinksUrl;
    
    public MenuController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    
    @GetMapping
    public Menu getMenu() {
        return Menu.builder().dishes(getDishes()).drinks(getDrinks()).build();
        
    }

    private List<Dish> getDishes() {
        return restTemplate.getForObject(dishesUrl, List.class);
    }

    private List<Drink> getDrinks() {
        return restTemplate.getForObject(drinksUrl, List.class);
    }
}
