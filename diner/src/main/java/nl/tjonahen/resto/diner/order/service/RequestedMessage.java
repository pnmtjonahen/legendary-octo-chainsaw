package nl.tjonahen.resto.diner.order.service;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@Getter
@Builder
public class RequestedMessage {

    private final Long orderid;
    private final List<MessageItem> items;
}
