package one.yezii.tomon.function.drawcard;

import java.util.HashMap;
import java.util.Map;

public class PoolBaseRate {
    private final Map<Integer, Integer> map = new HashMap<>();

    private PoolBaseRate() {
    }

    public static PoolBaseRate newInstance() {
        return new PoolBaseRate();
    }

    public PoolBaseRate setRate(int star, int rate) {
        this.map.put(star, rate);
        return this;
    }

    public Map<Integer, Integer> getMap() {
        return map;
    }
}
