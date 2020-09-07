package one.yezii.tomon.eventhandler;

import one.yezii.tomon.WsSessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloseEventHandler implements EventHandler {

    private final Logger logger = LoggerFactory.getLogger(CloseEventHandler.class);

    @Override
    public void handle(WsSessionContext context) {
        logger.warn("Connection closed.Try to reconnecting...");
        context.getWsClient().connectToServer();
    }
}
