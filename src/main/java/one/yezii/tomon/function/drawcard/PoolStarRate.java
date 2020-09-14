package one.yezii.tomon.function.drawcard;

import java.util.HashMap;
import java.util.Map;

public class PoolStarRate {
    private final Map<Integer, Double> map = new HashMap<>();

    private PoolStarRate() {
    }

    public static PoolStarRate newInstance() {
        return new PoolStarRate();
    }

    public PoolStarRate setRate(int star, double rate) {
        this.map.put(star, rate);
        return this;
    }

    public Map<Integer, Double> getMap() {
        return map;
    }


    public Double getRate(int star) {
        return map.get(star);
    }
}
