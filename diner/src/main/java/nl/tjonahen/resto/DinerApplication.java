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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.ExceptionWebSocketHandlerDecorator;

@SpringBootApplication
@EnableRabbit
@EnableWebSocket
@EnableRetry
public class DinerApplication implements WebSocketConfigurer {

    public static final String BARTENDER_QUEUE = "bartender-queue";
    public static final String BARTENDER_EXCHANGE = "bartender-exchange";
    public static final String BARTENDER_KEY = "bartender-key";

    public static final String CHEF_QUEUE = "chef-queue";
    public static final String CHEF_EXCHANGE = "chef-exchange";
    public static final String CHEF_KEY = "chef-key";

    public static void main(String[] args) {
        SpringApplication.run(DinerApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Binding barBinding() {
        return BindingBuilder.bind(new Queue(BARTENDER_QUEUE, false)).to(new TopicExchange(BARTENDER_EXCHANGE)).with(BARTENDER_KEY);
    }

    @Bean
    public Binding kitchenBinding() {
        return BindingBuilder.bind(new Queue(CHEF_QUEUE, false)).to(new TopicExchange(CHEF_EXCHANGE)).with(CHEF_KEY);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Autowired
    private OrderStatusBroker orderStatusBroker;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler(orderStatusBroker), "/orderstatus").setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler handler(OrderStatusBroker orderStatusBroker) {
        return new ExceptionWebSocketHandlerDecorator(orderStatusBroker);
    }

    @Bean
    public OrderStatusBroker orderStatusBroker() {
        return new OrderStatusBroker();
    }

    @Bean
    public Sampler defaultSampler() {
        return Sampler.ALWAYS_SAMPLE;
    }
    
}
