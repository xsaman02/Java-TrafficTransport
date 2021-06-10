package vut.fit.ija.main.model.schedule.impl;

import vut.fit.ija.main.model.map.Line;
import vut.fit.ija.main.model.map.Stop;
import vut.fit.ija.main.model.map.Street;
import vut.fit.ija.main.model.schedule.Direction;
import vut.fit.ija.main.model.schedule.LineIntervalScheme;
import vut.fit.ija.main.model.schedule.StopTime;
import vut.fit.ija.main.model.schedule.Trip;

import java.time.LocalTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Trip implementation
 * @author xkarpi06
 * @version 1.0
 * @since 1.0
 * created: 27-3-2020, xkarpi06
 * updated: 22-4-2020, xkarpi06 added getRoute(), Comparable
 */
public class TripImpl implements Trip, Comparable{
    private Line line;
    private Direction direction;
    private List<AbstractMap.SimpleImmutableEntry<Stop, StopTime>> schedule;

    private TripImpl(Line line, Direction direction, List<AbstractMap.SimpleImmutableEntry<Stop, StopTime>> schedule) {
        this.line = line;
        this.line.addTrip(this);
        this.direction = direction;
        this.schedule = schedule;
    }

    /**
     * static constructor
     * @param line where this trip happens
     * @param direction direction
     * @param departure departure time from first stop
     * @return Trip if times is compatible with route, null otherwise
     */
    public static TripImpl create(Line line, Direction direction, LocalTime departure) {
        if (line != null && line.getIntervalScheme() != null) {
            return new TripImpl(line, direction, createSchedule(line, direction, departure));
        } else {
            return null;
        }
    }

    /**
     * returns list of stops and corresponding times, referred to as schedule
     * @param line line with stops
     * @param direction direction
     * @param departure time of departure from first stop
     * @return schedule
     */
    private static List<AbstractMap.SimpleImmutableEntry<Stop, StopTime>> createSchedule(Line line, Direction direction, LocalTime departure) {
        List<AbstractMap.SimpleImmutableEntry<Stop, StopTime>> newSchedule = new ArrayList<>();
        List<Stop> stops = new ArrayList<>(line.getStops());
        LineIntervalScheme scheme = line.getIntervalScheme();
        if (direction == Direction.WAY_BACK) {
            Collections.reverse(stops);
            scheme = line.getReverseIntervalScheme();
        }
        int i = 0;
        newSchedule.add(new AbstractMap.SimpleImmutableEntry<>(stops.get(i), new StopTime(null, departure)));
        LocalTime lastDeparture = departure;
        for (i = 1; i < stops.size() - 1; i++) {
            LocalTime stopArrival = lastDeparture.plus(scheme.getMoving().get(i-1));
            LocalTime stopDeparture = stopArrival.plus(scheme.getWaiting().get(i-1));
            newSchedule.add(new AbstractMap.SimpleImmutableEntry<>(stops.get(i), new StopTime(stopArrival, stopDeparture)));
            lastDeparture = stopDeparture;
        }
        LocalTime stopArrival = lastDeparture.plus(scheme.getMoving().get(i-1));
        newSchedule.add(new AbstractMap.SimpleImmutableEntry<>(stops.get(i), new StopTime(stopArrival, null)));
        return newSchedule;
    }

    @Override
    public Line getLine() {
        return line;
    }

    @Override
    public List<AbstractMap.SimpleImmutableEntry<Street, Stop>> getRoute() {
        if (direction == Direction.WAY_THERE) {
            return line.getRoute();
        } else {    //Direction.WAY_BACK
            return line.getReverseRoute();
        }
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public List<AbstractMap.SimpleImmutableEntry<Stop, StopTime>> getSchedule() {
        return Collections.unmodifiableList(schedule);
    }

    @Override
    public LocalTime getDeparture() {
        return schedule.get(0).getValue().getDeparture();
    }

    @Override
    public LocalTime getArrival() {
        return schedule.get(schedule.size()-1).getValue().getArrival();
    }

    @Override
    public String toString() {
        String result =  "Trip on '" + line.getId() + "':";
        for (AbstractMap.SimpleImmutableEntry<Stop,StopTime> pair : schedule) {
            Stop s = pair.getKey();
            StopTime t = pair.getValue();
            result += String.format("\n ->%30s  a:%s d:%s", s.getId(), t.getArrival(), t.getDeparture());
        }
        return result;
    }

    @Override
    public int compareTo(Object o) {
        if (this == o) return 0;
        if (!(o instanceof Trip)) return -1;
        Trip trip = (Trip) o;
        return (int)(this.getDeparture().toSecondOfDay() - trip.getDeparture().toSecondOfDay());
    }
}
