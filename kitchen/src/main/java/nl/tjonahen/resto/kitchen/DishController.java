package nl.tjonahen.resto.kitchen;

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
@RequestMapping("/api/dish")
public class DishController {

    @GetMapping
    public List<Dish> getAllDishes() {
        return Arrays.asList(Dish.builder().name("Bread Basket").description("Assortment of fresh baked fruit breads and muffins").price(550L).build(),
                Dish.builder().name("Honey Almond Granola with Fruits").description("Natural cereal of honey toasted oats, raisins, almonds and dates").price(700L).build(),
                 Dish.builder().name("Belgian Waffle").description("Vanilla flavored batter with malted flour").price(750L).build(),
                 Dish.builder().name("Scrambled eggs").description("Scra,mbled eggs, roasted red pepper and garlic, with green onions").price(750L).build(),
                 Dish.builder().name("Blueberry Pancakes").description("With syrup, butter and lots of berries").price(850L).build()
        );
    }

}
