package nl.tjonahen.resto.kitchen;

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
public class CouponMessage {
    private Long orderid;
    private Coupon[] items;
    
}
