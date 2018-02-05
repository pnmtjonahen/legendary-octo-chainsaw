package nl.tjonahen.resto.bar;

import java.util.Arrays;
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
@RequestMapping("/api/drink")
public class DrinkController {
    private final static List<Drink> DRINKS = Arrays.asList(
                Drink.builder().ref("1").name("Coffee").description("Regular coffee").price(250L).build(),
                Drink.builder().ref("2").name("Chocolato").description("Chocolate espresso with milk").price(450L).build(),
                Drink.builder().ref("3").name("Corretto").description("Whiskey and coffee").price(500L).build(),
                Drink.builder().ref("4").name("Iced tea").description("Hot tea, except not hot").price(300L).build(),
                Drink.builder().ref("5").name("Soda").description("Coke, Sprite, Fanta, etc.").price(250L).build());


    @GetMapping
    public List<Drink> getAllDrinks() {
        return DRINKS;
    }
    
    @GetMapping("/{ref}")
    public Drink getDrink(@PathVariable String ref) {
        return DRINKS.stream().filter(d -> d.getRef().equals(ref)).findFirst().orElse(Drink.builder().build());
    }
    
}
