package nl.tjonahen.resto.diner.order.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Drink {
    private String ref;
    private String name;
    private String description;
    private Long price;
}
