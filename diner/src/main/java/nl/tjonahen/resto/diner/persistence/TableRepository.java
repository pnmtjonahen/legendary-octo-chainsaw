package nl.tjonahen.resto.diner.persistence;

import nl.tjonahen.resto.diner.order.model.Table;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TableRepository extends JpaRepository<Table, Long> {

}
