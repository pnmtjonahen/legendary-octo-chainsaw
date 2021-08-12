package nl.tjonahen.resto.bar;

import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BartenderControllerTest {

    @Mock
    private BartenderService bartenderServiceMock;
    
    @InjectMocks
    private BartenderController bartenderController;
            

    @Test
    void testGetAllDrinks() {
        Mockito.when(bartenderServiceMock.getAllDrinks()).thenReturn(new ArrayList<>());
        assertNotNull(bartenderController.getAllDrinks());
    }

   
}
