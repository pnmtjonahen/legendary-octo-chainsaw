package nl.tjonahen.resto.diner.menu;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import nl.tjonahen.resto.logging.LogField;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@Getter
@Builder
public class Menu {
    @LogField
    private final List<MenuItem> dishes;
    
    private final List<MenuItem> drinks;
}
