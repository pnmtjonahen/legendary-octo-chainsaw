package nl.tjonahen.resto.diner.persistence;

import java.util.Optional;
import nl.tjonahen.resto.diner.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Philippe Tjon - A - Hen
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
  Optional<Order> getOrderById(final Long id);
}
