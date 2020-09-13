package one.yezii.tomon.function.drawcard;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 有保底机制的寻访池
 */
public class Pool {
    //星级&UP干员集合Map
    private final Map<Integer, Set<Operator>> upMap = new HashMap<>();
    //星级&非干员集合map
    private final Map<Integer, Set<Operator>> commonMap = new HashMap<>();
    private PoolBaseRate poolBaseRate;

    private Pool() {
    }

    public static Pool newPool(PoolBaseRate poolBaseRate) {
        Pool pool = new Pool();
        pool.poolBaseRate = poolBaseRate;
        return pool;
    }

    public void addOperator(Operator operator) {
        upMap.putIfAbsent(operator.getStar(), new HashSet<>());
        commonMap.putIfAbsent(operator.getStar(), new HashSet<>());
        (operator.getUpRate() == null ? commonMap : upMap).get(operator.getStar()).add(operator);
    }

    public Operator draw() {
        int star = RollHelper.roll(poolBaseRate.getMap());
        boolean isUp = rollUpOrNot(star);
        if (isUp) {
            return rollOperatorFromUpPool(star);
        }
        return rollOperatorFromCommonPool(star);
    }

    private Operator rollOperatorFromCommonPool(int star) {
        Map<Operator, Integer> rateMap = commonMap.get(star)
                .stream()
                .peek(operator -> operator.setUpRate(1))
                .collect(Collectors.toMap(operator -> operator, Operator::getUpRate));
        return RollHelper.roll(rateMap);
    }

    private Operator rollOperatorFromUpPool(int star) {
        Map<Operator, Integer> rateMap = upMap.get(star)
                .stream()
                .collect(Collectors.toMap(operator -> operator, Operator::getUpRate));
        return RollHelper.roll(rateMap);
    }

    private boolean rollUpOrNot(int star) {
        int hitNum = upMap.get(star).stream().mapToInt(Operator::getUpRate).sum();
        return RollHelper.hit(hitNum, 100);
    }

}
