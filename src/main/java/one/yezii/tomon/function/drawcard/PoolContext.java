package one.yezii.tomon.function.drawcard;

import java.util.ArrayList;
import java.util.List;

public class PoolContext {
    private static List<Pool> pools = new ArrayList<>();

    public static List<Pool> getPools() {
        return pools;
    }

    public static void setPools(List<Pool> pools) {
        PoolContext.pools = pools;
    }
}
