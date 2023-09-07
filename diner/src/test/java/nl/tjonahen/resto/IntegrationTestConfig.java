package nl.tjonahen.resto;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.test.RabbitListenerTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RabbitListenerTest(spy = true, capture = true)
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

  @RabbitListener(id = "testDrinks", queues = DinerApplication.BARTENDER_QUEUE)
  public void receiveDrinks(final Message message) {
    System.out.println(message.toString());
  }

  @RabbitListener(id = "testBroker", queues = DinerApplication.DINER_QUEUE)
  public void orderBroker(final Message message) {
    System.out.println(message.toString());
  }
}
