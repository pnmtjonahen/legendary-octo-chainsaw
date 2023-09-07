package nl.tjonahen.resto.diner.order.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Philippe Tjon - A - Hen
 */
@Entity
@Table(name = "RESTO_ORDERITEM")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "ITEM_REF")
  private String ref;

  private Long quantity;

  @Enumerated(EnumType.STRING)
  private OrderItemType orderItemType;

  @Column(name = "ITEM_PREPED")
  @Setter
  private boolean prepared;
}
