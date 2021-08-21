package nl.tjonahen.resto.bar;

import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class BartenderServiceTest {
    
    @Mock
    private RestTemplate restTemplateMock;
    
    @InjectMocks
    private BartenderService sut;
    @Test
    void testGetAllDrinksExpect5Drinks() {
   
        final List<Drink> drinks = sut.getAllDrinks();
        
        assertEquals(5, drinks.size());
        
    }

    @Test
    void testServeDrinkExpectRestCall() {
        
        Mockito.when(restTemplateMock.postForLocation("null/api/order/271266/serve/drinks", Void.class)).thenReturn(null);
        
        sut.serveDrink(271266L);
        
        verify(restTemplateMock).postForLocation("null/api/order/271266/serve/drinks", Void.class);
        
    }
    
}
