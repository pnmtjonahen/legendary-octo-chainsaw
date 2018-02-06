package nl.tjonahen.resto.diner.order.service;

import lombok.Builder;
import lombok.Getter;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@Getter
@Builder
public class MessageItem {
    private final String ref;
    private final Long quantity;
}
