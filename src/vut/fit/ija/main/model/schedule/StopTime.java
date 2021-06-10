package vut.fit.ija.main.model.schedule;

import java.time.LocalTime;

/**
 * Represents arrival and departure times for a single stop
 * @author xkarpi06
 * @version 1.0
 * @since 1.0
 * created: 28-3-2020, xkarpi06
 * updated:
 */
public class StopTime {

    /** represents time of arrival to a stop */
    private LocalTime arrival;

    /** represents time of departure from a stop */
    private LocalTime departure;

    /**
     * Constructor
     * @param arrival input
     * @param departure input
     */
    public StopTime(LocalTime arrival, LocalTime departure) {
        this.arrival = arrival;
        this.departure = departure;
    }

    /**
     * Getter
     * @return arrival time
     */
    public LocalTime getArrival() {
        return arrival;
    }

    /**
     * Getter
     * @return departure time
     */
    public LocalTime getDeparture() {
        return departure;
    }
}
