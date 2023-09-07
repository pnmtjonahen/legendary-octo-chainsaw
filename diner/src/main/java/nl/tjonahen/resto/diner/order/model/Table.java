package nl.tjonahen.resto.diner.order.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@jakarta.persistence.Table(name = "RESTO_TABLE")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Table {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
}
