package nl.tjonahen.resto;

import com.github.tomakehurst.wiremock.WireMockServer;
import nl.tjonahen.resto.bar.Drink;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ContextConfiguration(initializers = {WireMockInitializer.class})
class BartenderApplicationTests {
    @Autowired
    private WireMockServer wireMockServer;

    @Autowired
    private WebTestClient webTestClient;
    
    @LocalServerPort
    private Integer port;
    

    @AfterEach
    void afterEach() {
        this.wireMockServer.resetAll();
    }
    
    @Test
    void getMenu() {
        this.webTestClient.get()
                .uri(String.format("http://localhost:%d/api/menu", port))
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(Drink.class)
                .returnResult()
                .getResponseBody();// for now a placeholder to have actual integration tests
    }

}
