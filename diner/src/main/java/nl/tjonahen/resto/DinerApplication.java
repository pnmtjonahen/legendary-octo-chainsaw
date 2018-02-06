package nl.tjonahen.resto;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableRabbit
public class DinerApplication {

    public static final String BAR_QUEUE = "bar-queue";
    public static final String BAR_EXCHANGE = "bar-exchange";
    public static final String BAR_KEY = "bar-key";

    public static final String KITCHEN_QUEUE = "kitchen-queue";
    public static final String KITCHEN_EXCHANGE = "kitchen-exchange";
    public static final String KITCHEN_KEY = "kitchen-key";
    
    public static void main(String[] args) {
        SpringApplication.run(DinerApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    

    @Bean
    public Binding barBinding() {
        return BindingBuilder.bind(new Queue(BAR_QUEUE, false)).to(new TopicExchange(BAR_EXCHANGE)).with(BAR_KEY);
    }
    @Bean
    public Binding kitchenBinding() {
        return BindingBuilder.bind(new Queue(KITCHEN_QUEUE, false)).to(new TopicExchange(KITCHEN_EXCHANGE)).with(KITCHEN_KEY);
    }


    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }

    
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
