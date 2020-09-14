package one.yezii.tomon.function.drawcard;

import com.google.common.util.concurrent.AtomicDouble;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

public class RollHelper {
    /**
     * 从map中以指定的概率roll出一个key
     *
     * @param map key为需要roll的对象，value为概率
     * @param <T> key的泛型
     * @return roll出的结果
     */
    public static <T> T roll(Map<T, Double> map) {
        AtomicDouble sum = new AtomicDouble();
        Map<T, Double> rollMap = map.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> sum.addAndGet(entry.getValue())));
        double point = Dice.rollDouble(sum.get());
        return rollMap.entrySet().stream()
                .sorted(Comparator.comparingDouble(Map.Entry::getValue))
                .filter(entry -> entry.getValue() > point)
                .findFirst()
                .orElseThrow()
                .getKey();
    }

    /**
     * 给定总概率和命中概率，计算是否命中
     *
     * @return 是否命中
     */
    public static boolean hit(double hitNum, double totalNum) {
        return Dice.rollDouble(totalNum) < hitNum;
    }
}
