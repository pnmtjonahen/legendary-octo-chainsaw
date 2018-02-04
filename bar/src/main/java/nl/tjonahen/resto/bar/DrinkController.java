package nl.tjonahen.resto.bar;

import java.util.Arrays;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@RestController
@RequestMapping("/api/drink")
public class DrinkController {

    @GetMapping
    public List<Drink> getAllDrinks() {
        return Arrays.asList(
                Drink.builder().name("Coffee").description("Regular coffee").price(250L).build(),
                Drink.builder().name("Chocolato").description("Chocolate espresso with milk").price(450L).build(),
                Drink.builder().name("Corretto").description("Whiskey and coffee").price(500L).build(),
                Drink.builder().name("Iced tea").description("Hot tea, except not hot").price(300L).build(),
                Drink.builder().name("Soda").description("Coke, Sprite, Fanta, etc.").price(250L).build());
    }
}
