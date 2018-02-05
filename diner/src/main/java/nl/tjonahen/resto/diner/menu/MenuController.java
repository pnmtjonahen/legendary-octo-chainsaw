package nl.tjonahen.resto.diner.menu;

import nl.tjonahen.resto.diner.order.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@RestController
@RequestMapping("/api/menu")
public class MenuController {

    private final OrderService orderService;

    public MenuController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    @GetMapping
    public Menu getMenu() {
        return Menu.builder().dishes(orderService.getDishes()).drinks(orderService.getDrinks()).build();
        
    }

}
