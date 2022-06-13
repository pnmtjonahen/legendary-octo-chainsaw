package nl.tjonahen.resto;

import brave.sampler.Sampler;
import java.util.Collections;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableRabbit
@EnableWebSocket
@EnableDiscoveryClient
@EnableSwagger2
public class DinerApplication implements RabbitListenerConfigurer {

    public static final String BARTENDER_QUEUE = "bartender-queue";
    public static final String BARTENDER_EXCHANGE = "bartender-exchange";
    public static final String BARTENDER_KEY = "bartender-key";

    public static final String DINER_QUEUE = "diner-queue";
    public static final String DINER_EXCHANGE = "diner-exchange";
    public static final String DINER_KEY = "diner-key";

    public static void main(String[] args) {
        SpringApplication.run(DinerApplication.class, args);
    }

    /*
     * LoadBalanced RestTemplate this uses the EurekaClient for service discovery and Ribbon for loadbalancing 
     * 
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    /*
     * The Queue binding 
     */
    @Bean
    public Binding barBinding() {
        return BindingBuilder.bind(new Queue(BARTENDER_QUEUE, false)).to(new TopicExchange(BARTENDER_EXCHANGE)).with(BARTENDER_KEY);
    }

    @Bean
    public Queue queue() {
        return new Queue(DINER_QUEUE, false);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(DINER_EXCHANGE);
    }

    @Bean
    public Binding dinerBinding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(DINER_KEY);
    }

    @Override
    public void configureRabbitListeners(final RabbitListenerEndpointRegistrar registrar) {
        final var factory = new DefaultMessageHandlerMethodFactory();
        factory.setMessageConverter(new MappingJackson2MessageConverter());
        registrar.setMessageHandlerMethodFactory(factory);
    }

    /*
     * The rabbit template, uses a jackson2json message converter to seamingless conver to and from json.
     */
    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final var rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    /*
     * Sleuth/zipkin brave sampler, Set to sample all requests. 
     */
    @Bean
    public Sampler defaultSampler() {
        return Sampler.ALWAYS_SAMPLE;
    }

    /*
    * Swagger 2 API documentation
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("nl.tjonahen.resto"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(new ApiInfo(
                        "Diner REST API",
                        "Diner REST API used by both the frontend and also by the Bar and Kitchen.",
                        "V1",
                        "n/a",
                        new Contact("PN Tjon-A-Hen", "www.tjonahen.nl", "philippe@tjonahen.nl"),
                        "DBAD", 
                        "https://dbad-license.org/", 
                        Collections.emptyList()));

    }

}
