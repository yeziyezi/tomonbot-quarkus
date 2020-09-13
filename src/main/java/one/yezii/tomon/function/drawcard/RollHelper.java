package one.yezii.tomon.function.drawcard;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class RollHelper {
    /**
     * 从map中以指定的概率roll出一个key
     *
     * @param map key为需要roll的对象，value为概率
     * @param <T> key的泛型
     * @return roll出的结果
     */
    public static <T> T roll(Map<T, Integer> map) {
        AtomicInteger sum = new AtomicInteger();
        Map<T, Integer> rollMap = map.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> sum.addAndGet(entry.getValue())));
        int point = Dice.roll(sum.get());
        return rollMap.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .filter(entry -> entry.getValue() > point)
                .findFirst()
                .orElseThrow()
                .getKey();
    }

    /**
     * 给定全部事件数量和命中事件所占的数量，计算是否命中
     *
     * @return 是否命中
     */
    public static boolean hit(int hitNum, int totalNum) {
        return Dice.roll(totalNum) < hitNum;
    }
}
