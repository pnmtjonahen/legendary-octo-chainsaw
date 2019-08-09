package nl.tjonahen.resto.kitchen;

import com.fasterxml.jackson.annotation.JsonView;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@RestController
@RequestMapping("/api")
public class ChefController {

    private final ChefService chefService;

    public ChefController(ChefService chefService) {
        this.chefService = chefService;
    }

    @GetMapping("/menu")
    @JsonView(PublicView.class)
    public List<Dish> menu() {
        return chefService.getAllDishes();
    }

    @GetMapping("/dish/{ref}")
    public Dish dish(@PathVariable String ref) {
        return chefService.getAllDishes().stream().filter(d -> d.getRef().equals(ref)).findFirst().orElse(Dish.builder().build());
    }

    @PostMapping("/order")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void order(@RequestBody CouponMessage couponMessage) {
        chefService.processCoupon(couponMessage);
    }

}
