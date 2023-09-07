package nl.tjonahen.resto.diner.menu;

import static org.mockito.Mockito.when;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.ArrayList;
import nl.tjonahen.resto.diner.order.service.OrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;

@ExtendWith(MockitoExtension.class)
class MenuControllerTest {

  @Mock private OrderService orderService;
  @Mock private MeterRegistry registry;
  @Mock private Counter counter;

  @InjectMocks private MenuController menuController;

  @Test
  void testGetMenu() throws Exception {
    when(registry.counter("diner.getmenu")).thenReturn(counter);
    when(orderService.getDishes()).thenReturn(new ArrayList<>());
    when(orderService.getDrinks()).thenReturn(Flux.fromIterable(new ArrayList<>()));

    var menu = menuController.getMenu();

    Assertions.assertNotNull(menu);
  }
}
