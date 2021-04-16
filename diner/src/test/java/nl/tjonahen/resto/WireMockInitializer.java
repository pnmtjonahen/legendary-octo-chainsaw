package nl.tjonahen.resto;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;

public class WireMockInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static WireMockServer wireMockServer = null;
    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        if (wireMockServer == null) {
            wireMockServer = new WireMockServer(19888);
            wireMockServer.start();
        }

        configurableApplicationContext
                .getBeanFactory()
                .registerSingleton("wireMockServer", wireMockServer);

        configurableApplicationContext.addApplicationListener(applicationEvent -> {
            if (applicationEvent instanceof ContextClosedEvent) {
                if (wireMockServer != null) {
                    wireMockServer.stop();
                    wireMockServer = null;
                }
            }
        });
    }
}
