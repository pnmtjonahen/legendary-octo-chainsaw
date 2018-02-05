package nl.tjonahen.resto.diner.order.persistence;

import nl.tjonahen.resto.diner.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Philippe Tjon - A - Hen
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

}
