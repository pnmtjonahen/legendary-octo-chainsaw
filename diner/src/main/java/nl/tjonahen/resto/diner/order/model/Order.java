package nl.tjonahen.resto.diner.order.model;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
@Entity
@Table(name = "RESTO_ORDER")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Setter
    private OrderStatus status;

    @OneToMany(cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    private boolean hasDishes() {
        return orderItems.stream().filter(i -> i.getOrderItemType() == OrderItemType.DISH).count() > 0;
    }

    private boolean hasDrinks() {
        return orderItems.stream().filter(i -> i.getOrderItemType() == OrderItemType.DRINK).count() > 0;
    }

    synchronized public OrderStatus serveDrinks() {

        if (this.hasDishes()) {
            if (this.getStatus() == OrderStatus.FOOD_SERVED) {
                this.setStatus(OrderStatus.BILLING);
            } else {
                this.setStatus(OrderStatus.DRINK_SERVED);
            }
        } else {
            this.setStatus(OrderStatus.BILLING);
        }
        return this.getStatus();
    }

    synchronized public OrderStatus serveFood() {

        if (this.hasDrinks()) {
            if (this.getStatus() == OrderStatus.DRINK_SERVED) {
                this.setStatus(OrderStatus.BILLING);
            } else {
                this.setStatus(OrderStatus.FOOD_SERVED);
            }
        } else {
            this.setStatus(OrderStatus.BILLING);
        }

        return this.getStatus();
    }

}
