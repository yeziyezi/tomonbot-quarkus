package one.yezii.tomon.function.drawcard;

public class Operator {
    private String name = "";//干员名字
    private int star;//星数
    private double upRate = 0;//up后概率，如果没up则为0

    private Operator() {
    }

    /**
     * @param name   干员名称
     * @param star   星数
     * @param upRate up后在同星级中所占的概率，如果非up则null
     * @return Operator
     */
    public static Operator of(String name, int star, Integer upRate) {
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

    public double getUpRate() {
        return upRate;
    }

    public void setUpRate(Integer upRate) {
        this.upRate = upRate;
    }
}
