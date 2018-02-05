package nl.tjonahen.resto.diner.order.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *
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
}
