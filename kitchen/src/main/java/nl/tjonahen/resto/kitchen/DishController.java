package nl.tjonahen.resto.kitchen;

import com.fasterxml.jackson.annotation.JsonView;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@RestController
@RequestMapping("/api/dish")
public class DishController {

    private final KitchenService kitchenService;

    public DishController(KitchenService kitchenService) {
        this.kitchenService = kitchenService;
    }
    
    
    @GetMapping
    @JsonView(PublicView.class)
    public List<Dish> getAllDishes() {
        return kitchenService.getAllDishes();
    }
    
    @GetMapping("/{ref}")
    public Dish getDish(@PathVariable String ref) {
        return kitchenService.getAllDishes().stream().filter(d -> d.getRef().equals(ref)).findFirst().orElse(Dish.builder().build());
    }

}
