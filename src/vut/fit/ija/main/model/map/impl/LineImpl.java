package vut.fit.ija.main.model.map.impl;

import vut.fit.ija.main.model.map.Line;
import vut.fit.ija.main.model.map.Stop;
import vut.fit.ija.main.model.map.Street;
import vut.fit.ija.main.model.schedule.Direction;
import vut.fit.ija.main.model.schedule.LineIntervalScheme;
import vut.fit.ija.main.model.schedule.Trip;
import vut.fit.ija.main.model.schedule.impl.TripImpl;
import vut.fit.ija.main.model.vehicles.TransitType;

import java.util.*;

/**
 * Represents Line
 * order of actions:
 *  create Line
 *  add Stops and Streets
 *  add LineIntervalScheme
 *  add Trips
 * @author xkarpi06
 * @version 1.0
 * @since 1.0
 * created: 27-3-2020, xkarpi06
 * updated: 22-4-2020, xkarpi06 added getReverseRoute(), getTripsInDirection()
 */
public class LineImpl implements Line {

    /** line identifier */
    private String id;

    /** Vehicle type serving the line */
    private TransitType transitType;

    /** Route consisting of empty streets and stops (on streets) */
    private List<AbstractMap.SimpleImmutableEntry<Street, Stop>> route;

    /** Line schedule */
    private List<Trip> trips;

    /** Template for trips realized on this line */
    private LineIntervalScheme intervalScheme;

    /** Template for reverse trips realized on this line */
    private LineIntervalScheme reverseIntervalScheme;

    /**
     * Constructor
     * @param id input
     * @param transitType input
     */
    private LineImpl(String id, TransitType transitType) {
        this.id = id;
        this.transitType = transitType;
        this.route = new ArrayList<>();
        this.trips = new ArrayList<>();
    }

    /**
     * static Constructor
     * @param id input
     * @param t input
     * @return instance
     */
    public static LineImpl create(String id, TransitType t) {
        return new LineImpl(id, t);
    }


    @Override
    public String getId() {
        return id;
    }

    @Override
    public TransitType getTransitType() {
        return transitType;
    }

    @Override
    public List<AbstractMap.SimpleImmutableEntry<Street, Stop>> getRoute() {
        return Collections.unmodifiableList(route);
    }

    @Override
    public List<AbstractMap.SimpleImmutableEntry<Street, Stop>> getReverseRoute() {
        List<AbstractMap.SimpleImmutableEntry<Street, Stop>> reverseRoute = new ArrayList<>(route);
        Collections.reverse(reverseRoute);
        return Collections.unmodifiableList(reverseRoute);
    }

    @Override
    public List<Trip> getTrips() {
        return Collections.unmodifiableList(trips);
    }

    @Override
    public List<Trip> getTripsInDirection(Direction direction) {
        List<TripImpl> result = new ArrayList<>();
        for (Trip t : trips) {
            if (t.getDirection() == direction) {
                result.add((TripImpl) t);
            }
        }
        Collections.sort(result);
        return Collections.unmodifiableList(result);
    }

    @Override
    public LineIntervalScheme getIntervalScheme() {
        return intervalScheme;
    }

    @Override
    public LineIntervalScheme getReverseIntervalScheme() {
        return reverseIntervalScheme;
    }

    @Override
    public boolean addStop(Stop stop) {
        if (stop == null) {
            return false;
        }
        if (route.isEmpty()) {
            route.add(new AbstractMap.SimpleImmutableEntry<>(stop.getStreet(), stop));
            return true;
        } else {
            Street last = route.get(route.size()-1).getKey();
            Street stNew = stop.getStreet();
            if (stNew.follows(last)) {
                route.add(new AbstractMap.SimpleImmutableEntry<>(stop.getStreet(), stop));
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean addTrip(Trip trip) {
        if (trip != null && trip.getLine().equals(this)) {
            trips.add(trip);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean addStreet(Street street) {
        if (street == null || route.isEmpty()) {
            return false;
        }
        if (route.get(route.size()-1).getKey().follows(street)) {
            route.add(new AbstractMap.SimpleImmutableEntry<>(street, null));
            return true;
        }
        return false;
    }

    @Override
    public boolean setIntervalScheme(LineIntervalScheme scheme) {
        if (scheme != null || this.getStops().size() < 2) {
            // vehicle waits at transit stops, not final stops
            if (this.amountOfTransitStops() == (scheme.getWaiting().size())) {
                this.intervalScheme = scheme;
                this.reverseIntervalScheme = LineIntervalScheme.reverse(scheme);
                return true;
            }
        }
        return false;
    }

    private int amountOfTransitStops() {
        return getStops().size() - 2;
    }

    @Override
    public List<Stop> getStops() {
        List<Stop> stops = new ArrayList<>();
        for (AbstractMap.SimpleImmutableEntry<Street,Stop> pair : route) {
            if (pair.getValue() != null) {
                stops.add(pair.getValue());
            }
        }
        return Collections.unmodifiableList(stops);
    }

    @Override
    public boolean goesViaStreet(Street street) {
        for (AbstractMap.SimpleImmutableEntry<Street,Stop> pair : route) {
            if (pair.getKey().equals(street)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LineImpl)) return false;
        LineImpl line = (LineImpl) o;
        return id.equals(line.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        String result =  "Line '" + id + "':";
        for (AbstractMap.SimpleImmutableEntry<Street,Stop> pair : route) {
            Street s = pair.getKey();
            Stop sto = pair.getValue();
            result += String.format("\n ->%15s  %s", s.getId(), (sto == null) ? "" : sto.getId());
        }
        if (intervalScheme != null) {
            result += String.format("\nScheme: %s", intervalScheme);
            result += "\nTrips:";
            int counter = 0;
            for (Trip t : trips) {
                String direction = (t.getDirection() == Direction.WAY_BACK) ? "(R)" : "";
                result += String.format(" %s%s", t.getDeparture(), direction);
                if (++counter == 30) {
                    result += "\n\t";
                    counter = 0;
                }
            }
        }
        return result;
    }

    @Override
    public String toString2() {
        return "Line '" + id + "', transit=" + transitType;
    }
}
