package nl.tjonahen.resto.front;

import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class FrontApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrontApplication.class, args);
    }
}

@Getter
@Builder
class Config {

    private final String http_server;
    private final String ws_server;
}

@RestController
@RequestMapping("/")
class ConfigController {

    @Value("${server.http:http://localhost:8083}")
    private String httpServer;
    @Value("${server.ws:ws://localhost:8083}")
    private String wsServer;

    @CrossOrigin
    @GetMapping(value = "/configuration.js", produces = "application/javascript")
    public String getConfig() {
        return String.format("const config = {\n"
                + "    http_server: \"%s\",\n"
                + "    ws_server: \"%s\"\n"
                + "};", httpServer, wsServer);

    }

}
