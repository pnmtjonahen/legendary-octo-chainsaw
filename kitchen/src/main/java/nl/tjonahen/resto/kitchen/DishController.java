package nl.tjonahen.resto.kitchen;

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
@RequestMapping("/api/dish")
public class DishController {

    private static final List<Dish> DISHES = Arrays.asList(
                Dish.builder().ref("1").name("Bread Basket").description("Assortment of fresh baked fruit breads and muffins").price(550L).build(),
                Dish.builder().ref("2").name("Honey Almond Granola with Fruits").description("Natural cereal of honey toasted oats, raisins, almonds and dates").price(700L).build(),
                Dish.builder().ref("3").name("Belgian Waffle").description("Vanilla flavored batter with malted flour").price(750L).build(),
                Dish.builder().ref("4").name("Scrambled eggs").description("Scra,mbled eggs, roasted red pepper and garlic, with green onions").price(750L).build(),
                Dish.builder().ref("5").name("Blueberry Pancakes").description("With syrup, butter and lots of berries").price(850L).build()
        );
    
    @GetMapping
    public List<Dish> getAllDishes() {
        return DISHES;
    }
    
    @GetMapping("/{ref}")
    public Dish getDish(@PathVariable String ref) {
        return DISHES.stream().filter(d -> d.getRef().equals(ref)).findFirst().orElse(Dish.builder().build());
    }

}
