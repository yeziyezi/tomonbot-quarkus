package one.yezii.tomon;

import io.quarkus.runtime.Startup;
import one.yezii.tomon.eventhandler.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import java.net.URI;

@ClientEndpoint
@Startup
public class WsClient {
    private final String token;
    private final String url;
    private final EventHandler openEventHandler = new OpenEventHandler();
    private final EventHandler messageEventHandler = new MessageEventHandler();
    private final EventHandler errorEventHandler = new ErrorEventHandler();
    private final EventHandler closeEventHandler = new CloseEventHandler();
    private final WebSocketContainer container = ContainerProvider.getWebSocketContainer();
    private final Logger logger = LoggerFactory.getLogger(WsClient.class);
    private Session session;

    public WsClient(@ConfigProperty(name = "tomon.token") String token,
                    @ConfigProperty(name = "tomon.websocket.url") String url) {
        this.token = token;
        this.url = url;
        connectToServer();
    }

    public void connectToServer() {
        try {
            this.session = container.connectToServer(this, new URI(url));
        } catch (Exception e) {
            throw new RuntimeException("connect failed", e);
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        openEventHandler.handle(getContext(session, null, null));
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        messageEventHandler.handle(getContext(session, message, null));
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        errorEventHandler.handle(getContext(session, null, throwable));
    }

    @OnClose
    public void onClose(Session session) {
        closeEventHandler.handle(getContext(session, null, null));
    }


    private WsSessionContext getContext(Session session, String message, Throwable throwable) {
        return WsSessionContext.of(session, message, throwable, token, this);
    }
}
