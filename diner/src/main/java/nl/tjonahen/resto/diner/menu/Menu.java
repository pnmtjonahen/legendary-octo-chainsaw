package nl.tjonahen.resto.diner.menu;

import nl.tjonahen.resto.diner.order.service.Dish;
import nl.tjonahen.resto.diner.order.service.Drink;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@Getter
@Builder
public class Menu {

    private final List<Dish> dishes;
    private final List<Drink> drinks;
}
