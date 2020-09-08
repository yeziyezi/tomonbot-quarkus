package one.yezii.tomon.eventhandler;

import one.yezii.tomon.ws.WsSessionContext;

public interface EventHandler {
    void handle(WsSessionContext context);
}
