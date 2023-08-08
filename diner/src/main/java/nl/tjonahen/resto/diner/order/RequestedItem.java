package nl.tjonahen.resto.diner.order;

import lombok.Getter;

/**
 * @author Philippe Tjon - A - Hen
 */
@Getter
public class RequestedItem {

  private String ref;
  private Long quantity;
  private RequestedItemType type;
}
