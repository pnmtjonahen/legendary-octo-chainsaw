package nl.tjonahen.resto.diner.menu;

import java.util.ArrayList;
import nl.tjonahen.resto.diner.order.service.OrderService;
import static org.junit.Assert.assertNotNull;
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
    private OrderService orderServiceMock;
    
    @InjectMocks
    private MenuController menuController;
    
    
    @Test
    public void testGetMenu() {
        
        when(orderServiceMock.getDishes()).thenReturn(new ArrayList<>());
        when(orderServiceMock.getDrinks()).thenReturn(new ArrayList<>());
        Menu menu = menuController.getMenu();
        
        assertNotNull(menu);
        assertNotNull(menu.getDishes());
        assertNotNull(menu.getDrinks());
    }
    
}
