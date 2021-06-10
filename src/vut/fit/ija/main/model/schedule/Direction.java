package vut.fit.ija.main.model.schedule;

import java.util.HashMap;
import java.util.Map;

/**
 * Direction determines which way the vehicle travels through a route
 * @author xkarpi06
 * @version 1.0
 * @since 1.0
 * created: 23-3-2020, xkarpi06
 * updated: 29-3-2020, xkarpi06
 */
public enum Direction {
    WAY_THERE(0),          // iterates through streets in order
    WAY_BACK(1);           // iterates through streets backwards

    private final int value;
    private static Map<Integer,Direction> directionByInt = new HashMap<>();

    static {
        for (Direction d : Direction.values()) {
            directionByInt.put(d.value, d);
        }
    }

    /** Constructor */
    Direction(int value) {
        this.value = value;
    }

    /**
     * @return int value of Direction
     */
    public int getValue() {
        return this.value;
    }

    /**
     * @return Direction with int value of x
     */
    public static Direction valueOf(int x) {
        return directionByInt.get(x);
    }
}
