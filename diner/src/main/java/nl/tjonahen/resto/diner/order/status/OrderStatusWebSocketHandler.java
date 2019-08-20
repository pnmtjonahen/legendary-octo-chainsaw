package nl.tjonahen.resto.diner.order.status;

import brave.Span;
import brave.Tracer;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * OrderStatusBroker is a WebSocket that allows the server to push an order
 * status update to the client.
 */
@Slf4j
@Service
public class OrderStatusWebSocketHandler implements WebSocketHandler {

    private final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();

    /*
     * Tracer object to allow adding the websocket push to the current trace span. 
     * Programatically adding trace to sleuth/zipkin 
     */
    private Tracer tracer;

    @Autowired
    public void setTracer(Tracer tracer) {
        this.tracer = tracer;
    }

    /**
     * Handle he websocket message, The frontend wil send the orderid to the
     * websocket to indicate on what order it is waiting.
     *
     * @param session -
     * @param encodedMessage the message containing the order
     * @throws Exception -
     */
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> encodedMessage) throws Exception {
        OrderId value = new ObjectMapper().readValue(encodedMessage.getPayload().toString(), OrderId.class);
        final Long orderid = Long.valueOf(value.getOrderid());
        sessions.put(orderid, session);
        log.info("Register socket {} for orderid {}", session.getId(), orderid);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.entrySet()
                .stream()
                .filter(e -> e.getValue().getId().equals(session.getId()))
                .map(Map.Entry::getKey)
                .forEach(id -> sessions.remove(id));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("afterConnectionEstablish {}", session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable th) throws Exception {
        log.info("ignore transport errors.");
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /*
    * Retryable method, it is posible that the order processing is faster then the browser is able to setup a WebSocket. 
    * So we retry the sendstatus a number of times.
     */
    @Retryable(backoff = @Backoff(delay = 5000))
    public void sendStatus(Long id, String msg) throws IOException, OrderNotFoundException {
        if (sessions.containsKey(id)) {
            // create new span to trace websocket cal to client
            final Span span = tracer.newChild(tracer.currentSpan().context()).name("sendStatus").start();
            sessions.get(id).sendMessage(new TextMessage(msg));
            span.finish();
        } else {
            log.warn("Table for order {} not (yet) found", id);
            throw new OrderNotFoundException();
        }
    }

}

@Getter
@Setter
class OrderId {
    private String orderid;
}
