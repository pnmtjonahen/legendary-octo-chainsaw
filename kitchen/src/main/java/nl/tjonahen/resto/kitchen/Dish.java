package nl.tjonahen.resto.kitchen;

import lombok.Builder;
import lombok.Getter;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@Getter
@Builder        
public class Dish {

    private final String ref;
    private final String name;
    private final String description;
    private final Long price;

}
