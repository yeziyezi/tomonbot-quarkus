package one.yezii.tomon.eventhandler;

import one.yezii.tomon.DispatchMessageHandler;
import one.yezii.tomon.TomonWsMessage;
import one.yezii.tomon.WsSessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MessageEventHandler implements EventHandler {
    private final static ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
    private final static int HEARTBEAT_ACK_TIMEOUT = 100 * 1000;
    private final Logger logger = LoggerFactory.getLogger(MessageEventHandler.class);
    private final DispatchMessageHandler dispatchMessageHandler = new DispatchMessageHandler();
    private Timer timer = null;

    @Override
    public void handle(WsSessionContext context) {
        if (context.getMessage().isEmpty()) {
            return;
        }
        TomonWsMessage tm = context.getMessage().get();
        String d = Optional.ofNullable(tm.d).map(Objects::toString).orElse("");
        switch (tm.opEnum) {
            case HELLO:
                logger.info("hello message:" + d);
                int heartbeatInterval = tm.d.get("heartbeat_interval").asInt();
                startHeartbeatSendTask(heartbeatInterval / 2, context);
                startNewHeartbeatAckWaitingTimer();
                break;
            case HEARTBEAT_ACK:
                logger.debug("received heartbeat ack");
                stopHeartbeatAckWaitingTimer();
                startNewHeartbeatAckWaitingTimer();
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

    private void stopHeartbeatAckWaitingTimer() {
        timer.cancel();
        timer = null;
    }

    private void startHeartbeatSendTask(int heartbeatInterval, WsSessionContext context) {
        scheduledExecutor.scheduleAtFixedRate(
                () -> {
                    logger.debug("send heartbeat");
                    context.send(TomonWsMessage.heartbeat().toString());
                },
                heartbeatInterval, heartbeatInterval, TimeUnit.MILLISECONDS);
    }

    private void startNewHeartbeatAckWaitingTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                logger.warn("tomon server no response after " + HEARTBEAT_ACK_TIMEOUT +
                        " ms.session will be closed");

            }
        }, HEARTBEAT_ACK_TIMEOUT);
    }
}