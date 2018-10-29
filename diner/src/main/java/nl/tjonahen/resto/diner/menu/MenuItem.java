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
    public static enum Type {DISH, DRINK};
    private final Type type;
    private final String ref;
    private final String name;
    private final String description;
    private final Long price;
}
