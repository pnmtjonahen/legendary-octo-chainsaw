package nl.tjonahen.resto.front;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:test.properties")
class FrontApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;
    @LocalServerPort
    private Integer port;
    
    @Test
    void getConfig() {
        final String response = this.restTemplate.getForObject(String.format("http://localhost:%d//configuration.js", port), String.class);
    assertEquals(
        """
            const config = {
              http_server: "http://localhost:8083",
              ws_server: "ws://localhost:8083"
            };""",
        response);
    }

}
