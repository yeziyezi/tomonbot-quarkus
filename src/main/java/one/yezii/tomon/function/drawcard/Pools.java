package one.yezii.tomon.function.drawcard;

public class Pools {
    public static Pool simpleSingleSixUpPool() {
        Pool pool = Pool.newPool(PoolStarRate.newInstance()
                .setRate(3, 0.4)
                .setRate(4, 0.5)
                .setRate(5, 0.08)
                .setRate(6, 0.02));
        //todo
        return null;
    }
}
