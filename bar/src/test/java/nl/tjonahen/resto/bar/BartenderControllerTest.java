package nl.tjonahen.resto.bar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BartenderControllerTest {

  @Mock private BartenderService bartenderServiceMock;

  @InjectMocks private BartenderController bartenderController;

  @Test
  void testGetAllDrinks() {
    when(bartenderServiceMock.getAllDrinks()).thenReturn(new ArrayList<>());
    assertNotNull(bartenderController.getAllDrinks());
  }

  @Test
  void testGetDrink() {
    when(bartenderServiceMock.getAllDrinks())
        .thenReturn(
            Arrays.asList(
                new Drink("test-drink", "test-name", "", Long.MIN_VALUE, Long.MIN_VALUE)));
    assertNotNull(bartenderController.getDrink("test-drink"));
    assertEquals("test-name", bartenderController.getDrink("test-drink").getName());
  }

  @Test
  void testGetDrink_drinkNotFound() {
    when(bartenderServiceMock.getAllDrinks()).thenReturn(new ArrayList<>());
    assertNotNull(bartenderController.getDrink("test-drink"));
    assertNull(bartenderController.getDrink("test-drink").getName());
  }
}
