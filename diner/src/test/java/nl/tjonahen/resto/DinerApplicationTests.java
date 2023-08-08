package nl.tjonahen.resto;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import nl.tjonahen.resto.diner.menu.MenuItem;
import nl.tjonahen.resto.diner.order.RequestedItem;
import nl.tjonahen.resto.diner.order.status.OrderNotFoundException;
import nl.tjonahen.resto.diner.order.status.OrderStatusWebSocketHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.test.RabbitListenerTestHarness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = {WireMockInitializer.class})
@ActiveProfiles(profiles = "non-async")
class DinerApplicationTests {

  @Autowired private WireMockServer wireMockServer;

  @Autowired private WebTestClient webTestClient;

  @Autowired private CircuitBreakerRegistry circuitBreakerRegistry;

  @Autowired private RabbitListenerTestHarness harness;

  @LocalServerPort private Integer port;

  @MockBean private OrderStatusWebSocketHandler orderStatusWebSocketHandler;

  @AfterEach
  void afterEach() {
    this.wireMockServer.resetAll();
    this.circuitBreakerRegistry.getAllCircuitBreakers().forEach(CircuitBreaker::reset);
  }

  @Test
  void testGetMenu() throws Exception {
    this.wireMockServer.stubFor(
        get(urlEqualTo("/chef/api/menu"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        "[{\"name\":\"frites\",\"ref\":\"frites\",\"description\":\"patatjes\",\"price\":\"5\"}]")));

    this.wireMockServer.stubFor(
        get(urlEqualTo("/bartender/api/menu"))
            .willReturn(
                WireMock.okForContentType(
                    MediaType.TEXT_EVENT_STREAM_VALUE,
                    "data: {\"name\":\"cola\",\"ref\":\"cola\",\"description\":\"zero\",\"price\":\"2\"}\n\n")));

    final List<MenuItem> result =
        this.webTestClient
            .get()
            .uri(String.format("http://localhost:%d/api/menu", port))
            .accept(MediaType.TEXT_EVENT_STREAM)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
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
    this.wireMockServer.stubFor(
        get(urlEqualTo("/chef/api/menu"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        "[{\"name\":\"frites\",\"ref\":\"frites\",\"description\":\"patatjes\",\"price\":\"5\"}]")));

    this.wireMockServer.stubFor(
        get(urlEqualTo("/bartender/api/menu")).willReturn(aResponse().withStatus(500)));

    final List<MenuItem> result =
        this.webTestClient
            .get()
            .uri(String.format("http://localhost:%d/api/menu", port))
            .accept(MediaType.TEXT_EVENT_STREAM)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
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
    this.wireMockServer.stubFor(
        get(urlEqualTo("/chef/api/menu")).willReturn(aResponse().withStatus(500)));

    this.wireMockServer.stubFor(
        get(urlEqualTo("/bartender/api/menu")).willReturn(aResponse().withStatus(500)));

    final List<MenuItem> result =
        this.webTestClient
            .get()
            .uri(String.format("http://localhost:%d/api/menu", port))
            .accept(MediaType.TEXT_EVENT_STREAM)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
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

  @Test
  void testPlaceOrder() throws InterruptedException {
    this.wireMockServer.stubFor(
        post(urlEqualTo("/chef/api/order")).willReturn(aResponse().withStatus(202)));

    final List<RequestedItem> orderItems = new ArrayList<>();
    orderItems.add(new RequestedItem());
    this.webTestClient
        .post()
        .uri(String.format("http://localhost:%d/api/order", port))
        .body(
            Mono.just(
                "[{\"ref\":\"cola\", \"quantity\":\"1\",\"type\":\"DRINK\"}, {\"ref\":\"frites\", \"quantity\":\"1\",\"type\":\"DISH\"}]"),
            String.class)
        .header("Content-type", "application/json")
        .exchange()
        .expectStatus()
        .is2xxSuccessful();

    this.wireMockServer.verify(
        postRequestedFor(urlEqualTo("/chef/api/order"))
            .withHeader("Content-Type", equalTo("application/json"))
            .withRequestBody(
                WireMock.equalToJson(
                    "{\"orderid\":2, \"items\":[{\"id\":4,\"ref\":\"frites\",\"quantity\":1}]}")));

    final RabbitListenerTestHarness.InvocationData invocationData =
        this.harness.getNextInvocationDataFor("testDrinks", 10, TimeUnit.SECONDS);

    assertNotNull(invocationData);
    final Message message = (Message) invocationData.getArguments()[0];
    final String body = new String(message.getBody());
    assertEquals("{\"orderid\":2,\"items\":[{\"id\":null,\"ref\":\"cola\",\"quantity\":1}]}", body);
  }

  @Test
  void serveDishes() throws InterruptedException, IOException, OrderNotFoundException {
    this.webTestClient
        .post()
        .uri(String.format("http://localhost:%d/api/order/1/serve/dishes", port))
        .exchange()
        .expectStatus()
        .is2xxSuccessful();
  }

  @Test
  void serveDishes_ordernotfound() {
    this.webTestClient
        .post()
        .uri(String.format("http://localhost:%d/api/order/9/serve/dishes", port))
        .exchange()
        .expectStatus()
        .is4xxClientError();
  }

  @Test
  void serveDrinks() throws InterruptedException {
    this.webTestClient
        .post()
        .uri(String.format("http://localhost:%d/api/order/1/serve/drinks", port))
        .exchange()
        .expectStatus()
        .is2xxSuccessful();
  }

  @Test
  void serveDrinks_ordernotfound() {
    this.webTestClient
        .post()
        .uri(String.format("http://localhost:%d/api/order/9/serve/drinks", port))
        .exchange()
        .expectStatus()
        .is4xxClientError();
  }
}
