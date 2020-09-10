package one.yezii.tomon.function.drawcard;

public class Operator {
    private String name;//干员名字
    private int star;//星数
    private int upRate;//up后概率，如果没up则为0

    private Operator() {
    }

    public static Operator of(String name, int star, int upRate) {
        Operator operator = new Operator();
        operator.name = name;
        operator.star = star;
        operator.upRate = upRate;
        return operator;
    }

    public String getName() {
        return name;
    }

    public int getStar() {
        return star;
    }

    public int getUpRate() {
        return upRate;
    }
}
