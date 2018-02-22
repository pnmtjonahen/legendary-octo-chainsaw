package nl.tjonahen.resto.diner.order.model;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
public enum OrderStatus {
    /* initial order */
    INITIAL,
    /* order is being prepared by either kitchen or bar */
    PREPARING,
    /* Drinks are served to the customer */
    DRINK_SERVED,
    /* Food is served to the customer */
    FOOD_SERVED,
    /* The bil is made */
    BILLING,
    /* The order is payed money, money, money */
    PAYED,
    /* No customer could be found while serving drinks or food */
    NO_CUSTOMER;
    
}
