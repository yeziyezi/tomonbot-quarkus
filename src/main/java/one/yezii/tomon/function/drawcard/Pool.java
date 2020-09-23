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
    private final PoolStarRate poolCurrentRate = PoolStarRate.newInstance();
    private PoolStarRate poolBaseRate;
    private int drawTimesWithoutSix = 0;
    private String openTime;
    private PoolDescriptor poolDescriptor;
    private String name;

    private Pool() {
    }

    public static Pool newPool(PoolStarRate poolBaseRate) {
        Pool pool = new Pool();
        pool.poolBaseRate = poolBaseRate;
        return pool;
    }

    public void addOperator(Operator operator) {
        upMap.putIfAbsent(operator.getStar(), new HashSet<>());
        commonMap.putIfAbsent(operator.getStar(), new HashSet<>());
        (operator.getUpRate() == 0.0 ? commonMap : upMap).get(operator.getStar()).add(operator);
    }

    public Operator draw() {
        if (drawTimesWithoutSix == 0) {
            initPoolCurrentStarRate();
        }
        //超过50抽，保底机制开始生效
        if (drawTimesWithoutSix >= 50) {
            improveSixStarRate();
        }
        drawTimesWithoutSix++;
        int star = RollHelper.roll(poolCurrentRate.getMap());
        if (star == 6) {
            initPoolCurrentStarRate();
            drawTimesWithoutSix = 0;
        }
        boolean isUp = rollIsUp(star);
        if (isUp) {
            return rollOperatorFromUpPool(star);
        }
        return rollOperatorFromCommonPool(star);
    }

    private void initPoolCurrentStarRate() {
        poolCurrentRate.getMap().putAll(poolBaseRate.getMap());
    }

    private void improveSixStarRate() {
        //将六星的概率提高2%
        double sixStarRate = poolCurrentRate.getRate(6) + 0.02;
        poolCurrentRate.setRate(6, sixStarRate);

        //将六星概率提升后，将剩余概率按比例分给剩下的星级
        //根据现有概率之和和原有概率之和计算出其他星级概率的缩小比率，按此比率缩小其他星级的概率
        double restStarCurrentRateSum = poolCurrentRate.getMap().values().stream().mapToDouble(v -> v).sum();
        double restStarNewRateSum = 1 - sixStarRate;
        double reductionRatio = restStarNewRateSum / restStarCurrentRateSum;
        Map<Integer, Double> newStarRateMap = poolCurrentRate.getMap()
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey() != 6)//过滤掉六星不进行处理
                .peek(entry -> entry.setValue(entry.getValue() * reductionRatio))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        poolCurrentRate.getMap().putAll(newStarRateMap);
    }

    private Operator rollOperatorFromCommonPool(int star) {
        Map<Operator, Double> rateMap = commonMap.get(star)
                .stream()
                .peek(operator -> operator.setUpRate(1))
                .collect(Collectors.toMap(operator -> operator, Operator::getUpRate));
        return RollHelper.roll(rateMap);
    }

    private Operator rollOperatorFromUpPool(int star) {
        Map<Operator, Double> rateMap = upMap.get(star)
                .stream()
                .collect(Collectors.toMap(operator -> operator, Operator::getUpRate));
        return RollHelper.roll(rateMap);
    }

    private boolean rollIsUp(int star) {
        double hitNum = upMap.get(star).stream().mapToDouble(Operator::getUpRate).sum();
        return RollHelper.hit(hitNum, 1.0);
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public PoolDescriptor getPoolDescriptor() {
        return poolDescriptor;
    }

    public void setPoolDescriptor(PoolDescriptor poolDescriptor) {
        this.poolDescriptor = poolDescriptor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<Integer, Set<Operator>> getUpMap() {
        return upMap;
    }

    public boolean isUp(Operator operator) {
        return upMap.containsKey(operator.getStar())
                && upMap.get(operator.getStar()).stream()
                .anyMatch(op -> op.getName().equals(operator.getName()));
    }
}
