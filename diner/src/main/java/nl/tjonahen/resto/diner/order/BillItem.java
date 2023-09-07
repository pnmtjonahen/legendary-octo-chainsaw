package nl.tjonahen.resto.diner.order;

import lombok.Builder;
import lombok.Getter;

/**
 * @author Philippe Tjon - A - Hen
 */
@Getter
@Builder
public class BillItem {

  private final String name;
  private final Long quantity;
  private final Long price;

  public Long getTotal() {
    return quantity * price;
  }
}
