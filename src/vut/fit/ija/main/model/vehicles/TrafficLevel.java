package vut.fit.ija.main.model.vehicles;

/**
 * Represents intensity of traffic on a street
 * @author xkarpi06
 * @version 1.0
 * @since 1.0
 * created: 22-03-2020, xkarpi06
 * updated: 22-04-2020, xkarpi06, added speed coefficient speedRate()
 */
public enum TrafficLevel {
    ZERO(0),
    GOOD(1),
    NORMAL(2),
    WORSE(3),
    BAD(4),
    JAMMED(5);

    private final int value;

    private TrafficLevel(int value) {
        this.value = value;
    }

    /**
     * Getter
     * @return value of traffic level
     */
    public int getValue() {
        return value;
    }

    /**
     * Returns the coefficient of speed according to traffic level
     * @return coefficient of speed
     */
    public double speedRate() {
        switch (this) {
            case ZERO: return 2;
            case GOOD: return 1.5;
            case NORMAL: return 1.0;
            case WORSE: return 0.8;
            case BAD: return 0.4;
            case JAMMED: return 0.1;
            default: break;
        }
        return 1.0;
    }
}
