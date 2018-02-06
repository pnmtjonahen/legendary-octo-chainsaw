package nl.tjonahen.resto.bar;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@Getter
@Setter
@NoArgsConstructor
public class Coupon {
    private String ref;
    private Long quantity;
}
