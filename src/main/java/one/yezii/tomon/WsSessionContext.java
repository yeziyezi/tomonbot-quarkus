package one.yezii.tomon;

import javax.websocket.Session;
import java.util.Optional;
import java.util.concurrent.Future;

public class WsSessionContext {
    private Session session;
    private TomonWsMessage message;
    private Throwable error;
    private String token;
    private WsClient wsClient;

    public static WsSessionContext of(Session session, String message, Throwable error, String token, WsClient wsClient) {
        WsSessionContext context = new WsSessionContext();
        context.session = session;
        context.message = Optional.ofNullable(message).map(TomonWsMessage::ofString).orElse(null);
        context.error = error;
        context.token = token;
        context.wsClient = wsClient;
        return context;
    }

    public Future<Void> send(Object data) {
        System.out.println(session == null);
        return session.getAsyncRemote().sendObject(data);
    }

    public Session getSession() {
        return session;
    }

    public Optional<TomonWsMessage> getMessage() {
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
