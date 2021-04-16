package nl.tjonahen.resto.diner.order.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.util.stream.IntStream;
import nl.tjonahen.resto.DinerApplication;
import nl.tjonahen.resto.WireMockInitializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.rabbit.test.RabbitListenerTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:test.properties")
@RabbitListenerTest(spy = false, capture = false)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = DinerApplication.class)
@ContextConfiguration(initializers = {WireMockInitializer.class})
public class OrderServiceCircuitBreakerTest {

    @Autowired
    private WireMockServer wireMockServer;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Autowired
    private OrderService orderService;

    @BeforeEach
    public void beforeEach() {
        this.wireMockServer.resetAll();
        this.circuitBreakerRegistry.getAllCircuitBreakers().forEach(CircuitBreaker::reset);
    }

    @Test
    public void testGetDishesMenuCircuitBreaker() {
        this.wireMockServer.stubFor(get(urlEqualTo("/chef/api/menu"))
                .willReturn(aResponse().withStatus(500)));
        
        final CircuitBreaker getDishesCB = circuitBreakerRegistry.getAllCircuitBreakers().filter(cb -> cb.getName().equals("getdishes")).get();

        IntStream.range(0, 4).forEach(i -> {
            orderService.getDishes();
        });

        Assertions.assertEquals(0, getDishesCB.getMetrics().getNumberOfSuccessfulCalls());
        Assertions.assertEquals(2, getDishesCB.getMetrics().getNumberOfFailedCalls());
        Assertions.assertEquals(2, getDishesCB.getMetrics().getNumberOfNotPermittedCalls());
    }

    @Test
    public void testGetDrinksMenuCircuitBreaker() {
        this.wireMockServer.stubFor(get(urlEqualTo("/bartender/api/menu"))
                .willReturn(aResponse().withStatus(500)));

        final CircuitBreaker getDrinksCB = circuitBreakerRegistry.getAllCircuitBreakers().filter(cb -> cb.getName().equals("getdrinks")).get();

        IntStream.range(0, 4).forEach(i -> {
            orderService.getDrinks().blockFirst();
        });

        Assertions.assertEquals(0, getDrinksCB.getMetrics().getNumberOfSuccessfulCalls());
        Assertions.assertEquals(2, getDrinksCB.getMetrics().getNumberOfFailedCalls());
        Assertions.assertEquals(2, getDrinksCB.getMetrics().getNumberOfNotPermittedCalls());

    }
}
