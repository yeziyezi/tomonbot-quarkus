package one.yezii.tomon.function.drawcard;

public class Dice {
    /**
     * 随机掷出整数x,0<=x<upperBound
     *
     * @param upperBound 骰子上界
     * @return 随机数
     */
    public static int roll(int upperBound) {
        return (int) rollDouble(upperBound * 1.0);
    }

    /**
     * 随机掷出双精度浮点数x,0<=x<upperBound
     *
     * @param upperBound 骰子上界
     * @return 随机double
     */
    public static double rollDouble(double upperBound) {
        return Math.random() * upperBound;
    }
}

