package nl.tjonahen.resto.diner.menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {
    public enum Type {DISH, DRINK}
    private Type type;
    private String ref;
    private String name;
    private String description;
    private Long price;
}
