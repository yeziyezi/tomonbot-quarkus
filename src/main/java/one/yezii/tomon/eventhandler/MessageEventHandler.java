package one.yezii.tomon.eventhandler;

import one.yezii.tomon.ws.DispatchMessageHandler;
import one.yezii.tomon.ws.WsMessage;
import one.yezii.tomon.ws.WsSessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MessageEventHandler implements EventHandler {
    private final static ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
    private final static int HEARTBEAT_ACK_TIMEOUT = 100 * 1000;
    private final Logger logger = LoggerFactory.getLogger(MessageEventHandler.class);
    private final DispatchMessageHandler dispatchMessageHandler = new DispatchMessageHandler();

    @Override
    public void handle(WsSessionContext context) {
        if (context.getMessage().isEmpty()) {
            return;
        }
        WsMessage tm = context.getMessage().get();
        String d = Optional.ofNullable(tm.d).map(Objects::toString).orElse("");
        switch (tm.opEnum) {
            case HELLO:
                logger.info("hello message:" + d);
                int heartbeatInterval = tm.d.get("heartbeat_interval").asInt();
                startHeartbeatSendTask(heartbeatInterval / 2, context);
                break;
            case HEARTBEAT_ACK:
                logger.debug("received heartbeat ack");
                break;
            case DISPATCH:
                logger.info("received dispatch:" + tm.toString());
                dispatchMessageHandler.handle(context);
                break;
            case IDENTIFY:
                logger.info("identify result:" + d.substring(0, 50) + "...");
                break;
            default:
                logger.warn("unrecognized message:" + d);
        }
    }

    private void startHeartbeatSendTask(int heartbeatInterval, WsSessionContext context) {
        scheduledExecutor.scheduleAtFixedRate(
                () -> {
                    logger.debug("send heartbeat");
                    context.sendMessage(WsMessage.heartbeat());
                },
                heartbeatInterval, heartbeatInterval, TimeUnit.MILLISECONDS);
    }
}
