package nl.tjonahen.resto.bar;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;

@ExtendWith(MockitoExtension.class)
class BartenderMessageListnerTest {
    @Mock
    private BartenderService serviceMock;
    @Mock
    private ObjectMapper objectMapperMock;
    
    @InjectMocks
    private BartenderMessageListner sut;
    
    @Test
    void testReceiveDrink() throws Exception {
        final Message message = new Message("".getBytes(), null);
        final CouponMessage couponMessage = new CouponMessage();
        couponMessage.setOrderid(1L);
        final Coupon[] items = { newCoupon()};
        couponMessage.setItems(items);
        when(objectMapperMock.readValue(eq("".getBytes()), eq(CouponMessage.class))).thenReturn(couponMessage);
        
        when(serviceMock.getAllDrinks()).thenReturn(Arrays.asList(new Drink("test-ref", "test-drink", "short drink", 1L, 0L)));
        
        sut.receiveDrink(message);
        verify(serviceMock).serveDrink(1l);
    }
    
    private Coupon newCoupon() {
        final Coupon coupon = new Coupon();
        coupon.setQuantity(1L);
        coupon.setRef("test-ref");
        return coupon;
    }
}
