package nl.tjonahen.resto.diner.table;

import lombok.extern.slf4j.Slf4j;
import nl.tjonahen.resto.diner.order.model.Table;
import nl.tjonahen.resto.diner.persistence.TableRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/table")
public class TableController {

  private final TableRepository tableRepository;

  public TableController(TableRepository tableRepository) {
    this.tableRepository = tableRepository;
  }

  @CrossOrigin
  @GetMapping("/reserve")
  public Table reserveTable() {
    final var table = Table.builder().build();
    tableRepository.save(table);
    return table;
  }
}
