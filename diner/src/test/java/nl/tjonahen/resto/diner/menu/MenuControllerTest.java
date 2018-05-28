package nl.tjonahen.resto.diner.menu;

import java.util.ArrayList;
import nl.tjonahen.resto.diner.order.service.OrderService;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author Philippe Tjon - A - Hen philippe@tjonahen.nl
 */
@RunWith(MockitoJUnitRunner.class)
public class MenuControllerTest {

    @Mock
    private OrderService orderService;
    
    @InjectMocks
    private MenuController menuController;
    
    @Test
    public void testGetMenu() throws Exception {
        when(orderService.getDishes()).thenReturn(new ArrayList<>());
        when(orderService.getDrinks()).thenReturn(new ArrayList<>());
        
        Menu menu = menuController.getMenu();
        
        assertTrue(menu.getDishes().isEmpty());
        assertTrue(menu.getDrinks().isEmpty());
    }

}
