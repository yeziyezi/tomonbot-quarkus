package one.yezii.tomon.eventhandler;

import one.yezii.tomon.ws.WsSessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CloseEventHandler implements EventHandler {

    private final Logger logger = LoggerFactory.getLogger(CloseEventHandler.class);

    @Override
    public void handle(WsSessionContext context) {
        //todo 增加重连失败后的再次重试
        logger.warn("Connection closed.Try to reconnecting...");
        context.getWsClient().connectToServer();
    }
}
