package nl.tjonahen.resto;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import nl.tjonahen.resto.kitchen.Coupon;
import nl.tjonahen.resto.kitchen.CouponMessage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class KitchenApplicationTests {

    @Rule
    public WireMockRule rule = new WireMockRule(19999);

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testGetMenu() throws Exception {
        String result = this.restTemplate.getForObject("http://localhost:" + port + "/api/menu", String.class);
        assertNotNull(result);
        System.out.println(result);
    }

    @Test
    public void testGetDish() throws Exception {
        String result = this.restTemplate.getForObject("http://localhost:" + port + "/api/dish/1", String.class);
        assertNotNull(result);
        System.out.println(result);
    }

    @Test
    public void testProcessCoupon() throws Exception {
        final CouponMessage couponMessage = new CouponMessage();
        couponMessage.setOrderid(1L);
        final Coupon coupon = new Coupon();
        coupon.setId(1L);
        coupon.setQuantity(1L);
        coupon.setRef("1");
        couponMessage.setItems(new Coupon[]{coupon});
        HttpEntity<CouponMessage> request = new HttpEntity<>(couponMessage);
        HttpStatus statusCode = this.restTemplate.exchange("http://localhost:" + port + "/api/order", HttpMethod.POST, request, CouponMessage.class).getStatusCode();
        assertEquals(HttpStatus.ACCEPTED, statusCode);
    }
}
