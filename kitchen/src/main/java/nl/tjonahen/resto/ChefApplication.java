package nl.tjonahen.resto;

import brave.sampler.Sampler;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableRabbit
@EnableEurekaClient
public class ChefApplication  implements RabbitListenerConfigurer {
    public static final String CHEF_QUEUE = "chef-queue";
    public static final String CHEF_EXCHANGE = "chef-exchange";
    public static final String CHEF_KEY = "chef-key";

    public static void main(String[] args) {
        SpringApplication.run(ChefApplication.class, args);
    }


    @Bean
    public Queue queue() {
        return new Queue(CHEF_QUEUE, false);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(CHEF_EXCHANGE);
    }
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(CHEF_KEY);
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

}
