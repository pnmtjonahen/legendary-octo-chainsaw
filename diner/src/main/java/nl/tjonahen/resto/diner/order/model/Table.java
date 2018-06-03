package nl.tjonahen.resto.diner.order.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@javax.persistence.Table(name = "RESTO_TABLE")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Table {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}
