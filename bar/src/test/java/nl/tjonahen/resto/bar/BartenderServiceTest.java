package nl.tjonahen.resto.bar;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Philippe Tjon - A - Hen philippe@tjonahen.nl
 */
public class BartenderServiceTest {
    

    @Test
    public void testGetAllDrinksExpect5Drinks() {
   
        final BartenderService sut = new BartenderService(null);
        final List<Drink> drinks = sut.getAllDrinks();
        
        Assert.assertEquals(5, drinks.size());
        
    }

    @Test
    public void testServeDrinkExpectRestCall() {
        
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        
        final BartenderService sut = new BartenderService(restTemplate);
        Mockito.when(restTemplate.postForLocation("null/api/order/271266/serve/drinks", Void.class)).thenReturn(null);
        
        sut.serveDrink(271266L);
        
        verify(restTemplate).postForLocation("null/api/order/271266/serve/drinks", Void.class);
        
    }
    
}
