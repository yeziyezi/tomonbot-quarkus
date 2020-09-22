package one.yezii.tomon.function.drawcard;

import java.util.*;
import java.util.stream.Collectors;

public class PoolContext {
    private static List<Pool> pools = new ArrayList<>();

    public static List<Pool> getPools() {
        return pools;
    }

    public static String getPoolListMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < pools.size(); i++) {
            Pool pool = pools.get(i);
            String poolTitleText = String.format("%d.%s\n%s\n", (i + 1), pool.getName(), pool.getOpenTime());
            stringBuilder.append(poolTitleText);
            Map<Integer, Set<Operator>> upMap = pool.getUpMap();
            String upOperatorText = upMap.values().stream()
                    .flatMap(Collection::stream)
                    .sorted((o1, o2) -> Integer.compare(o2.getStar(), o1.getStar()))
                    .map(operator -> operator.getName() + "(" + operator.getStar() + ")")
                    .collect(Collectors.joining(" "));
            stringBuilder.append(upOperatorText).append("\n");
        }
        return stringBuilder.toString();
    }

    public static void setPools(List<Pool> pools) {
        PoolContext.pools = pools;
    }
}
