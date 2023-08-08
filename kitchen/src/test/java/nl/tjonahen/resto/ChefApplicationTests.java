package nl.tjonahen.resto;

import nl.tjonahen.resto.kitchen.Coupon;
import nl.tjonahen.resto.kitchen.CouponMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChefApplicationTests {

  //  @Rule public WireMockRule rule = new WireMockRule(19999);

  @LocalServerPort private int port;

  @Autowired private TestRestTemplate restTemplate;

  @Test
  public void testGetMenu() throws Exception {
    String result =
        this.restTemplate.getForObject("http://localhost:" + port + "/api/menu", String.class);
    Assertions.assertNotNull(result);
    System.out.println(result);
  }

  @Test
  public void testGetDish() throws Exception {
    String result =
        this.restTemplate.getForObject("http://localhost:" + port + "/api/dish/1", String.class);
    Assertions.assertNotNull(result);
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
    couponMessage.setItems(new Coupon[] {coupon});
    HttpEntity<CouponMessage> request = new HttpEntity<>(couponMessage);
    HttpStatusCode statusCode =
        this.restTemplate
            .exchange(
                "http://localhost:" + port + "/api/order",
                HttpMethod.POST,
                request,
                CouponMessage.class)
            .getStatusCode();
    Assertions.assertEquals(HttpStatus.ACCEPTED, statusCode);
  }
}
