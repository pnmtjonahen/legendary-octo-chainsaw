package nl.tjonahen.resto.diner.order;

import lombok.Getter;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@Getter
public class Item {

    private String ref;
    private Long quantity;
    private ItemType type;
}
