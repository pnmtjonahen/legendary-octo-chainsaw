package nl.tjonahen.resto;

import org.springframework.amqp.rabbit.test.RabbitListenerTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RabbitListenerTest(spy = false, capture = false)
public class IntegrationTestConfig {

  @Bean
  @Primary
  public RestTemplate restMockTemplate(final RestTemplateBuilder builder) {
    return builder.build();
  }

  @Bean
  @Primary
  public WebClient.Builder loadBalancedWebClientMockBuilder() {
    return WebClient.builder();
  }
}
