package one.yezii.tomon.function.drawcard;

public enum PoolDescriptor {
    SINGLE_SIX_UP("单六星UP"),
    LIMIT_UP("限定池"),
    ONLY_TWO_FIVE_UP("双五星UP"),
    TWO_SIX_UP("双六星UP"),
    COMMON_ROTATE("常驻轮换"),
    FIVE_AND_SIX_BEAMED("五星&六星定向UP"),
    OTHER("其他");
    private String label;

    PoolDescriptor(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
