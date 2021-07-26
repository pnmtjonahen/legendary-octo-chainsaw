package nl.tjonahen.resto;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.util.List;
import nl.tjonahen.resto.diner.menu.MenuItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.rabbit.test.RabbitListenerTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:test.properties")
@RabbitListenerTest(spy = false, capture = false)
@ContextConfiguration(initializers = {WireMockInitializer.class})
class DinerApplicationTests {

    @Autowired
    private WireMockServer wireMockServer;

    @Autowired
    private WebTestClient webTestClient;
    
    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @LocalServerPort
    private Integer port;

    @AfterEach
    void afterEach() {
        this.wireMockServer.resetAll();
        this.circuitBreakerRegistry.getAllCircuitBreakers().forEach(CircuitBreaker::reset);
    }

    @Test
    void testGetMenu() throws Exception {
        this.wireMockServer.stubFor(get(urlEqualTo("/chef/api/menu"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"name\":\"frites\",\"ref\":\"frites\",\"description\":\"patatjes\",\"price\":\"5\"}]")));
        
        this.wireMockServer.stubFor(get(urlEqualTo("/bartender/api/menu"))
                .willReturn(WireMock.okForContentType(MediaType.TEXT_EVENT_STREAM_VALUE, "data: {\"name\":\"cola\",\"ref\":\"cola\",\"description\":\"zero\",\"price\":\"2\"}\n\n")));
 
        final List<MenuItem> result = this.webTestClient.get()
                .uri(String.format("http://localhost:%d/api/menu", port))
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(MenuItem.class)
                .returnResult()
                .getResponseBody();
        
        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        
        Assertions.assertEquals(MenuItem.Type.DISH, result.get(0).getType());
        Assertions.assertEquals("frites", result.get(0).getName());
        Assertions.assertEquals("frites", result.get(0).getRef());
        Assertions.assertEquals("patatjes", result.get(0).getDescription());
        Assertions.assertEquals(5L, result.get(0).getPrice().longValue());

        Assertions.assertEquals(MenuItem.Type.DRINK, result.get(1).getType());
        Assertions.assertEquals("cola", result.get(1).getName());
        Assertions.assertEquals("cola", result.get(1).getRef());
        Assertions.assertEquals("zero", result.get(1).getDescription());
        Assertions.assertEquals(2L, result.get(1).getPrice().longValue());
    }
    
    @Test
    void testGetMenu_noDrinks() throws Exception {
        this.wireMockServer.stubFor(get(urlEqualTo("/chef/api/menu"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"name\":\"frites\",\"ref\":\"frites\",\"description\":\"patatjes\",\"price\":\"5\"}]")));
        
        this.wireMockServer.stubFor(get(urlEqualTo("/bartender/api/menu"))
                .willReturn(aResponse().withStatus(500)));
 
        final List<MenuItem> result = this.webTestClient.get()
                .uri(String.format("http://localhost:%d/api/menu", port))
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(MenuItem.class)
                .returnResult()
                .getResponseBody();
        
        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        
        Assertions.assertEquals(MenuItem.Type.DISH, result.get(0).getType());
        Assertions.assertEquals("frites", result.get(0).getName());
        Assertions.assertEquals("frites", result.get(0).getRef());
        Assertions.assertEquals("patatjes", result.get(0).getDescription());
        Assertions.assertEquals(5L, result.get(0).getPrice().longValue());

        Assertions.assertEquals(MenuItem.Type.DRINK, result.get(1).getType());
        Assertions.assertEquals("water", result.get(1).getName());
        Assertions.assertEquals("water", result.get(1).getRef());
        Assertions.assertEquals("complementary water", result.get(1).getDescription());
        Assertions.assertEquals(0L, result.get(1).getPrice().longValue());
    }
    
    @Test
    void testGetMenu_noDishesAndNoDrinks() throws Exception {
        this.wireMockServer.stubFor(get(urlEqualTo("/chef/api/menu"))
                .willReturn(aResponse().withStatus(500)));
        
        this.wireMockServer.stubFor(get(urlEqualTo("/bartender/api/menu"))
                .willReturn(aResponse().withStatus(500)));
 
        final List<MenuItem> result = this.webTestClient.get()
                .uri(String.format("http://localhost:%d/api/menu", port))
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(MenuItem.class)
                .returnResult()
                .getResponseBody();
        
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        
        Assertions.assertEquals(MenuItem.Type.DRINK, result.get(0).getType());
        Assertions.assertEquals("water", result.get(0).getName());
        Assertions.assertEquals("water", result.get(0).getRef());
        Assertions.assertEquals("complementary water", result.get(0).getDescription());
        Assertions.assertEquals(0L, result.get(0).getPrice().longValue());
    }

}
