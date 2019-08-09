package nl.tjonahen.resto;

import brave.sampler.Sampler;
import java.util.Collections;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableRabbit
@EnableDiscoveryClient
@EnableCircuitBreaker
@EnableSwagger2
public class BartenderApplication implements RabbitListenerConfigurer {

    public static final String BARTENDER_QUEUE = "bartender-queue";
    public static final String BARTENDER_EXCHANGE = "bartender-exchange";
    public static final String BARTENDER_KEY = "bartender-key";

    public static void main(String[] args) {
        SpringApplication.run(BartenderApplication.class, args);
    }

    @Bean
    public Queue queue() {
        return new Queue(BARTENDER_QUEUE, false);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(BARTENDER_EXCHANGE);
    }

    @LoadBalanced
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(BARTENDER_KEY);
    }

    private DefaultMessageHandlerMethodFactory messageHandlerMethodFactory() {
        DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
        factory.setMessageConverter(new MappingJackson2MessageConverter());
        return factory;
    }

    @Override
    public void configureRabbitListeners(final RabbitListenerEndpointRegistrar registrar) {
        registrar.setMessageHandlerMethodFactory(messageHandlerMethodFactory());
    }

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
                        "Bar REST API",
                        "Bar REST API used by the diner.",
                        "V1",
                        "n/a",
                        new Contact("PN Tjon-A-Hen", "www.tjonahen.nl", "philippe@tjonahen.nl"),
                        "DBAD", 
                        "https://dbad-license.org/", 
                        Collections.emptyList()));
    }    
}
