package one.yezii.tomon.function.drawcard;

public class Dice {
    /**
     * 随机掷出整数x,0<=x<upperBound
     *
     * @param upperBound 骰子上界
     * @return 随机数
     */
    public static int roll(int upperBound) {
        return (int) (Math.random() * upperBound);
    }
}

