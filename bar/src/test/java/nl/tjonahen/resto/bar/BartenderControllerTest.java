package nl.tjonahen.resto.bar;

import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author Philippe Tjon - A - Hen philippe@tjonahen.nl
 */
@RunWith(MockitoJUnitRunner.class)
public class BartenderControllerTest {

    @Mock
    private BartenderService bartenderServiceMock;
    
    @InjectMocks
    private BartenderController bartenderController;
            

    @Test
    public void testGetAllDrinks() {
        Mockito.when(bartenderServiceMock.getAllDrinks()).thenReturn(new ArrayList<>());
        Assert.assertNotNull(bartenderController.getAllDrinks());
    }

   
}
