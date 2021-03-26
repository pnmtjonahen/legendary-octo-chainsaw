package nl.tjonahen.resto.diner.menu;

import java.util.ArrayList;
import nl.tjonahen.resto.diner.order.service.OrderService;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;

@ExtendWith(MockitoExtension.class)
public class MenuControllerTest {

    @Mock
    private OrderService orderService;
    
    @InjectMocks
    private MenuController menuController;
    
    @Test
    public void testGetMenu() throws Exception {
        when(orderService.getDishes()).thenReturn(Flux.fromIterable(new ArrayList<>()));
        when(orderService.getDrinks()).thenReturn(Flux.fromIterable(new ArrayList<>()));
        
        var menu = menuController.getMenu();
        
        assertNotNull(menu);
    }

}
