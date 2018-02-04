package nl.tjonahen.resto.bar;

import lombok.Builder;
import lombok.Getter;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@Getter
@Builder        
public class Drink {

    private final String name;
    private final String description;
    private final Long price;
}
