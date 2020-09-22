package one.yezii.tomon.scheduled;

import io.quarkus.scheduler.Scheduled;
import one.yezii.tomon.ws.WsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

//定时检测Websocket session是否活跃并重连
@ApplicationScoped
public class SessionReconnectJob {
    private final Logger logger = LoggerFactory.getLogger(SessionReconnectJob.class);
    @Inject
    WsClient wsClient;

    @Scheduled(every = "60s", delay = 3, delayUnit = TimeUnit.MINUTES)
    public void run() {
        if (!wsClient.sessionActive()) {
            logger.warn("tomon session is inactive,retry to close");
            wsClient.connectToServer();
            if (wsClient.sessionActive()) {
                logger.warn("tomon session is active now");
            }
        }
    }
}
