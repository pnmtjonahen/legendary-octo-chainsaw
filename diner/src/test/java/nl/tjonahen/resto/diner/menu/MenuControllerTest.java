package nl.tjonahen.resto.diner.menu;

import java.util.ArrayList;
import nl.tjonahen.resto.diner.order.service.OrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;

@ExtendWith(MockitoExtension.class)
class MenuControllerTest {

    @Mock
    private OrderService orderService;
    
    @InjectMocks
    private MenuController menuController;
    
    @Test
    void testGetMenu() throws Exception {
        when(orderService.getDishes()).thenReturn(new ArrayList<>());
        when(orderService.getDrinks()).thenReturn(Flux.fromIterable(new ArrayList<>()));
        
        var menu = menuController.getMenu();
        
        Assertions.assertNotNull(menu);
    }

}
