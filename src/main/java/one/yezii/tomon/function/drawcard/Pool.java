package one.yezii.tomon.function.drawcard;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Pool {
    //星级&UP干员集合Map
    private final Map<Integer, Set<Operator>> upMap = new HashMap<>();
    //星级&非干员集合map
    private final Map<Integer, Set<Operator>> commonMap = new HashMap<>();
    //卡池基础概率 key星级 value 概率
    private PoolBaseRate poolBaseRate;

    private Pool() {
    }

    public static Pool newPool(PoolBaseRate poolBaseRate) {
        Pool pool = new Pool();
        pool.poolBaseRate = poolBaseRate;
        return pool;
    }

    public static void main(String[] args) {
        Pool pool = Pool.newPool(PoolBaseRate.newInstance()
                .setRate(3, 40)
                .setRate(4, 50)
                .setRate(5, 8)
                .setRate(6, 2)
        );
        System.out.println("体验一下没有保底机制的10连抽吧");
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                pool.draw();
                System.out.print("\t");
            }
            System.out.println();
        }
    }

    public void addOperator(Operator operator) {
        upMap.putIfAbsent(operator.getStar(), new HashSet<>());
        commonMap.putIfAbsent(operator.getStar(), new HashSet<>());
        (operator.getUpRate() == 0 ? commonMap : upMap).get(operator.getStar()).add(operator);
    }

    public Operator draw() {
        AtomicInteger sum = new AtomicInteger();
        //将星级的概率逐级叠加作为roll出当前星级的点数上界
        //key 星级 value roll(sum)当值小于value时抽中该星级
        Map<Integer, Integer> rollStarMap = poolBaseRate.getMap().entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> sum.addAndGet(entry.getValue())));
        int roll1 = Dice.roll(sum.get());
        int star = rollStarMap.entrySet()
                .stream()
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .filter(entry -> entry.getValue() > roll1)
                .findFirst()
                .map(Map.Entry::getKey)
                .orElseThrow();
        System.out.print(star);
        //todo
        return null;
    }

}
