package one.yezii.tomon.eventhandler;

import one.yezii.tomon.WsSessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorEventHandler implements EventHandler {
    private final Logger logger = LoggerFactory.getLogger(ErrorEventHandler.class);

    @Override
    public void handle(WsSessionContext context) {
        logger.error("error", context.getError());
    }
}
