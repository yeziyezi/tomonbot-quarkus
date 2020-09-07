package one.yezii.tomon;

import javax.websocket.Session;
import java.util.Optional;

public class WsSessionContext {
    private Session session;
    private WsMessage message;
    private Throwable error;
    private String token;
    private WsClient wsClient;

    public static WsSessionContext of(Session session, String message, Throwable error, String token, WsClient wsClient) {
        WsSessionContext context = new WsSessionContext();
        context.session = session;
        context.message = Optional.ofNullable(message).map(WsMessage::ofString).orElse(null);
        context.error = error;
        context.token = token;
        context.wsClient = wsClient;
        return context;
    }

    public void send(Object data) {
        session.getAsyncRemote().sendObject(data);
    }

    public void sendMessage(WsMessage message) {
        session.getAsyncRemote().sendText(message.toString());
    }

    public Session getSession() {
        return session;
    }

    public Optional<WsMessage> getMessage() {
        return Optional.ofNullable(message);
    }

    public Throwable getError() {
        return error;
    }

    public String getToken() {
        return token;
    }

    public WsClient getWsClient() {
        return wsClient;
    }
}
