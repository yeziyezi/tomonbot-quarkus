package one.yezii.tomon.eventhandler;

import one.yezii.tomon.WsSessionContext;

public interface EventHandler {
    void handle(WsSessionContext context);
}
