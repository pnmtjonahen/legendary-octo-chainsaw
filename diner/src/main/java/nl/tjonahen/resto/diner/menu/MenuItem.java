package nl.tjonahen.resto.diner.menu;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MenuItem {
    public enum Type {DISH, DRINK}
    private final Type type;
    private final String ref;
    private final String name;
    private final String description;
    private final Long price;
}
