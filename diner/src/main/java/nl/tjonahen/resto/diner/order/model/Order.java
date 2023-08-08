package nl.tjonahen.resto.diner.order.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
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
  @JoinColumn(name = "order_id")
  private List<OrderItem> orderItems;

  private boolean hasDishes() {
    return orderItems.stream().filter(i -> i.getOrderItemType() == OrderItemType.DISH).count() > 0;
  }

  private boolean hasDrinks() {
    return orderItems.stream().filter(i -> i.getOrderItemType() == OrderItemType.DRINK).count() > 0;
  }

  public synchronized OrderStatus serveDrinks() {

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

  public synchronized OrderStatus serveFood() {

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
