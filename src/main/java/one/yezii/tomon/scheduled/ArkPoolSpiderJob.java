package one.yezii.tomon.scheduled;

import io.quarkus.scheduler.Scheduled;
import one.yezii.tomon.function.drawcard.Pool;
import one.yezii.tomon.function.drawcard.PoolContext;
import one.yezii.tomon.function.drawcard.Spider;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ArkPoolSpiderJob {
    @Scheduled(every = "1H")
    public void run() {
        List<Pool> pools = new Spider().fetchPools();
        if (!pools.isEmpty()) {
            PoolContext.setPools(pools);
        }
    }
}
