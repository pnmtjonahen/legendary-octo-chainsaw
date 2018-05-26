package nl.tjonahen.resto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.ExceptionWebSocketHandlerDecorator;

@Configuration
public class DinerWebSocketConfigurer implements WebSocketConfigurer {

    private OrderStatusBroker orderStatusBroker;

    @Autowired
    public void setOrderStatusBroker(OrderStatusBroker orderStatusBroker) {
        this.orderStatusBroker = orderStatusBroker;
    }

    /*
     * Register our OrderStatusBroker as the websocket handler, wrapped in a excption decorator handler. 
     * Set allow origins to * to enable other domains to connect to our websocket. 
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new ExceptionWebSocketHandlerDecorator(orderStatusBroker), "/orderstatus").setAllowedOrigins("*");
    }

}
