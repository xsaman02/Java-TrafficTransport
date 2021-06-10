package vut.fit.ija.main.model.vehicles;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents types of transit for lines
 * @author xkarpi06
 * @version 1.0
 * @since 1.0
 * created: 22-3-2020, xkarpi06
 * updated:
 */
public enum TransitType {
    SUBWAY(0),
    TRAM(1),
    BUS(2);

    private final int value;
    private static Map<Integer,TransitType> typeByInt = new HashMap<>();

    private TransitType(int value) {
        this.value = value;
    }

    static {
        for (TransitType tt : TransitType.values()) {
            typeByInt.put(tt.value, tt);
        }
    }

    /**
     * @return TransitType with int value x
     */
    public static TransitType valueOf(int x) {
        return typeByInt.get(x);
    }

    /**
     * @return int value of TransitType
     */
    public int getValue() { return this.value; }
}
