package nl.tjonahen.resto;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@Service
public class OrderStatusBroker implements WebSocketHandler {

    private final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();

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
                .map(e -> e.getKey())
                .forEach(id -> sessions.remove(id));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("afterConnectionEstablish {}", session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable th) throws Exception {
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    @Retryable(backoff = @Backoff(delay=5000))
    public void sendStatus(Long id, String msg) throws IOException, OrderNotFoundException {
        if (sessions.containsKey(id)) {
            sessions.get(id).sendMessage(new TextMessage(msg));
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
