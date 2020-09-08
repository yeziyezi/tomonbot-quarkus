package one.yezii.tomon.eventhandler;

import one.yezii.tomon.ws.WsMessage;
import one.yezii.tomon.ws.WsSessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenEventHandler implements EventHandler {
    private final Logger logger = LoggerFactory.getLogger(OpenEventHandler.class);

    @Override
    public void handle(WsSessionContext context) {
        logger.info("do authentication...");
        context.send(WsMessage.identify(context.getToken()).toString());
    }
}
