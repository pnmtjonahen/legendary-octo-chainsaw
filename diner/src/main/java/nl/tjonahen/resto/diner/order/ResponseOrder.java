package nl.tjonahen.resto.diner.order;

import lombok.Builder;
import lombok.Getter;

/**
 * @author Philippe Tjon - A - Hen
 */
@Builder
@Getter
public class ResponseOrder {

  private final Long ref;
  private final String billUrl;
}
