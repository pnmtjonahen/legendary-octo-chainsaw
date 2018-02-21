package nl.tjonahen.resto;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@Component
public class OrderStatusBroker implements WebSocketHandler {

    private final Map<Long, WebSocketSession> sessions = new TreeMap<>();

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
                .findFirst()
                .ifPresent(id -> sessions.remove(id));
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

    public void sendStatus(Long id, String msg) throws IOException {
        if (sessions.containsKey(id)) {
            sessions.get(id).sendMessage(new TextMessage(msg));
        }

    }

}

@Getter
@Setter
class OrderId {

    private String orderid;
}
