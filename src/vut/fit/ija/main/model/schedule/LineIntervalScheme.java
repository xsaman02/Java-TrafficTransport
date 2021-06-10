package vut.fit.ija.main.model.schedule;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents time intervals between stops and at them in main direction
 * serves as a template for trips on that line
 * @author xkarpi06
 * @version 1.0
 * @since 1.0
 * created: 29-3-2020, xkarpi06
 * updated:
 */
public class LineIntervalScheme {

    /**
     * Time sections when the vehicle is moving between stops
     * one more than waiting sections
     */
    List<Duration> moving;

    /**
     * Time sections when the vehicle is waiting at stops other than final stops
     * one less than moving sections
     */
    List<Duration> waiting;

    /**
     * private Constructor
     */
    private LineIntervalScheme(List<Duration> moving, List<Duration> waiting) {
        this.moving = moving;
        this.waiting = waiting;
    }

    /**
     * Static Constructor, both moving and waiting values must be greater than 0 every time
     * @param departure departure from first stop
     * @param transitStops arrivals and departures from transit stops
     * @param arrival arrival to final stop
     * @return instance if input is valid and chronological, null otherwise
     */
    public static LineIntervalScheme create(LocalTime departure, List<StopTime> transitStops, LocalTime arrival) {
        LineIntervalScheme newScheme = null;
        if (departure != null && transitStops != null && arrival != null) {
            List<Duration> moving = new ArrayList<>();
            List<Duration> waiting = new ArrayList<>();
            LocalTime lastDeparture = departure;
            boolean chronological = true;

            for (StopTime st : transitStops) {
                Duration moveTime = Duration.between(lastDeparture, st.getArrival());
                Duration waitTime = Duration.between(st.getArrival(), st.getDeparture());
                if (moveTime.getSeconds() > 0 && waitTime.getSeconds() > 0) {
                    moving.add(moveTime);
                    waiting.add(waitTime);
                    lastDeparture = st.getDeparture();
                } else {
                    chronological = false;
                    break;
                }
            }
            // last moving section to final stop
            Duration moveTime = Duration.between(lastDeparture, arrival);
            if (moveTime.getSeconds() > 0) {
                moving.add(moveTime);
            } else {
                chronological = false;
            }
            if (chronological) {
                newScheme = new LineIntervalScheme(moving, waiting);
            }
        }
        return newScheme;
    }

    /**
     * static Constructor returns LineIntervalScheme in reversed order
     * @param scheme input to reverse
     * @return reversed scheme
     */
    public static LineIntervalScheme reverse(LineIntervalScheme scheme) {
        List<Duration> moving = new ArrayList<>(scheme.moving);
        List<Duration> waiting = new ArrayList<>(scheme.waiting);
        Collections.reverse(moving);
        Collections.reverse(waiting);
        return new LineIntervalScheme(moving, waiting);
    }

    /**
     * Getter
     * @return unmodifiable list of moving durations
     */
    public List<Duration> getMoving() {
        return Collections.unmodifiableList(moving);
    }

    /**
     * Getter
     * @return unmodifiable list of waiting durations
     */
    public List<Duration> getWaiting() {
        return Collections.unmodifiableList(waiting);
    }

    @Override
    public String toString() {
        String res = String.format("%d stops: move(wait)", waiting.size() + 2);
        int i = 0;
        for (; i < waiting.size(); i++) {
            res += String.format(" %d(%d)", moving.get(i).getSeconds(), waiting.get(i).getSeconds());
        }
        res += String.format(" %d", moving.get(i).getSeconds());
        return res;
    }
}
