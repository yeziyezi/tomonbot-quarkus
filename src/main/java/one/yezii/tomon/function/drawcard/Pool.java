package one.yezii.tomon.function.drawcard;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Pool {
    //星级&UP干员集合Map
    private final Map<Integer, Set<Operator>> upMap = new HashMap<>();
    //星级&非干员集合map
    private final Map<Integer, Set<Operator>> commonMap = new HashMap<>();
    //将星级的概率逐级叠加作为roll出当前星级的点数上界
    //key 星级 value roll(sum)当值小于value时抽中该星级
    private LinkedHashMap<Integer, Integer> rollStarMap;
    //各星级up干员所占概率之和
    private Map<Integer, Integer> upRateSumOfStar;

    private Pool() {
    }

    public static Pool newPool(PoolBaseRate poolBaseRate) {
        Pool pool = new Pool();
        //初始化roll星级使用的map
        AtomicInteger sum = new AtomicInteger();
        pool.rollStarMap = poolBaseRate.getMap().entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> sum.addAndGet(entry.getValue()),
                        (v1, v2) -> v2,
                        LinkedHashMap::new));
        if (sum.get() != 100) {
            throw new RuntimeException("各星级概率之和不为100");
        }
        return pool;
    }

    public void addOperator(Operator operator) {
        upMap.putIfAbsent(operator.getStar(), new HashSet<>());
        commonMap.putIfAbsent(operator.getStar(), new HashSet<>());
        (operator.getUpRate() == 0 ? commonMap : upMap).get(operator.getStar()).add(operator);
    }

    //结束干员添加开始初始化一些常用数据
    public void finish() {
        upRateSumOfStar = upMap.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> entry.getValue()
                                .stream()
                                .mapToInt(Operator::getUpRate)
                                .sum()));
    }

    public Operator draw() {
        int star = rollStar();
        boolean isUp = rollUpOrNot(star);
        return Operator.of(star + (isUp ? "_up" : ""), star, 0);
    }

    private boolean rollUpOrNot(int star) {
        return Dice.roll(100) < upRateSumOfStar.get(star);
    }

    private int rollStar() {
        int roll = Dice.roll(100);
        return rollStarMap.entrySet()
                .stream()
                .filter(entry -> roll < entry.getValue())
                .findFirst()
                .map(Map.Entry::getKey)
                .orElseThrow();
    }

}
