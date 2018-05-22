package nl.tjonahen.resto.diner.menu;

import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import nl.tjonahen.resto.diner.order.service.OrderService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@Slf4j
@RestController
@RequestMapping("/api/menu")
public class MenuController {

    private final OrderService orderService;

    public MenuController(OrderService orderService) {
        this.orderService = orderService;
    }

    @CrossOrigin
    @GetMapping
    public Menu getMenu() {
        log.info("Getting Menu");
        return Menu.builder()
                .dishes(orderService.getDishes()
                        .stream()
                        .map(d -> MenuItem.builder()
                                .ref(d.getRef())
                                .name(d.getName())
                                .description(d.getDescription())
                                .price(d.getPrice())
                                .build())
                        .collect(Collectors.toList()))
                .drinks(orderService.getDrinks()
                        .stream()
                        .map(d -> MenuItem.builder()
                                .ref(d.getRef())
                                .name(d.getName())
                                .description(d.getDescription())
                                .price(d.getPrice())
                                .build())
                        .collect(Collectors.toList())).build();

    }

}
