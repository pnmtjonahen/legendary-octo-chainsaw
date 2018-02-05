package nl.tjonahen.resto.diner.order;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@Getter
@Builder
public class Bill {

    private final Long total;
    private final List<BillItem> items;
}
