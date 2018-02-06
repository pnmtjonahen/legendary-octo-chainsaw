package nl.tjonahen.resto.diner.menu;

import lombok.Builder;
import lombok.Getter;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@Getter
@Builder
public class MenuItem {
    private final String name;
    private final String description;
    private final Long price;
}
