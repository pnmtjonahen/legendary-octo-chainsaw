package nl.tjonahen.resto;

import brave.sampler.Sampler;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication
@EnableRabbit
@EnableWebSocket
@EnableRetry
@EnableEurekaClient
@EnableAsync
@EnableCircuitBreaker
public class DinerApplication {

    public static final String BARTENDER_QUEUE = "bartender-queue";
    public static final String BARTENDER_EXCHANGE = "bartender-exchange";
    public static final String BARTENDER_KEY = "bartender-key";

    public static void main(String[] args) {
        SpringApplication.run(DinerApplication.class, args);
    }

    /*
     * LoadBalanced RestTemplate this uses the EurekaClient for service discovery and Ribbon for loadbalancing 
     * 
     */
    @LoadBalanced
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


    /*
     * The Queue binding 
     */
    @Bean
    public Binding barBinding() {
        return BindingBuilder.bind(new Queue(BARTENDER_QUEUE, false)).to(new TopicExchange(BARTENDER_EXCHANGE)).with(BARTENDER_KEY);
    }

    /*
     * The rabbit template, uses a jackson2json message converter to seamingless conver to and from json.
     */
    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
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

}
