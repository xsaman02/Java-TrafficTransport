package vut.fit.ija.main.model.vehicles;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.logging.Level;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import vut.fit.ija.main.Log;
import vut.fit.ija.main.model.map.Coordinate;
import vut.fit.ija.main.model.map.Stop;
import vut.fit.ija.main.model.map.Street;
import vut.fit.ija.main.model.schedule.Direction;
import vut.fit.ija.main.model.schedule.StopTime;
import vut.fit.ija.main.model.schedule.Trip;


/**
 * Represents Vehicle on map and it's properties.
 *
 * The Vehicle's position is determined by current simulation time, by schedule and by traffic level of streets.
 * If current simulation time is set to new value manually, the vehicle position is computed not accounting for
 * traffic levels of streets. Else, for each auto-increment of simulation time, Vehicle "step" is computed.
 * The "average step" of Vehicle is computed from distance to next stop (obtained from vehicle's line route),
 * and predicted time to get to next stop (computed as scheduled arrival plus vehicle's delay minus simulation time).
 * The "actual step" is then the "average step" multiplied by street's traffic coefficient. Delay changes if
 * the "actual step" differs from "average step".
 *
 * @author xsaman02
 * @version 1.0
 * @since 1.0
 * created: 07-04-2020, xsaman02
 * updated: 22-04-2020, xkarpi06 find position at any sim time
 */
public class Vehicle {
    private int id;
    private static int lastId = 0;  // just unique id for debugging purposes
    private Circle guiRep;
    private Trip trip;
    private SimpleImmutableEntry<Stop, StopTime> lastStopWithTime;
    private List<Line> linesToNextStop;

    private Duration delay = Duration.ZERO;
    private Double lengthToNextStop = 0.0;
    public boolean arrivedToFinalStop = false;
    private Duration boardingTimeLeft = Duration.ZERO;

    public Vehicle(Trip t, Color colorInGUI) {
        id = ++lastId;
        trip = t;
        Coordinate startPoint = trip.getSchedule().get(0).getKey().getCoordinate();
        guiRep = new Circle(startPoint.getX(), startPoint.getY(), 4, colorInGUI);
        lastStopWithTime = trip.getSchedule().get(0);
        linesToNextStop = new ArrayList<Line>();
    }

    /**
     * Checks if vehicle is waiting for departure (is boarding passengers)
     * @param simTime current time of simulation
     * @param millisDeltaTime render period, simulation step in milliseconds
     * @return true if vehicle is waiting, false otherwise
     */
    public boolean waitingToDepart(LocalTime simTime, long millisDeltaTime) {
        if (boardingTimeLeft.toMillis() > 0) {
            Log.LOGGER.log(Level.FINEST, String.format("Vehicle '%d' is boarding at '%s', remaining: %.1fs\n", id, lastStopWithTime.getKey().getId(), boardingTimeLeft.toNanos()/1e9));
            boardingTimeLeft = boardingTimeLeft.minusMillis(millisDeltaTime);
            if (delay.isNegative()) delay = delay.plusMillis(millisDeltaTime);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns next stop in vehicles schedule
     * if schedule is on last stop, automaticly set new schedule and returns first from new one
     * @return Stop and Stop time of next Stop
     */
    public SimpleImmutableEntry<Stop, StopTime> getNextStop() {
        if (lastStopWithTime == null) {
            return trip.getSchedule().get(0);
        } else {
            int indexOfLastStop = trip.getSchedule().indexOf(lastStopWithTime);
            if (indexOfLastStop == trip.getSchedule().size() - 1) {
                arrivedToFinalStop = true;
                return null;
            } else {
                return trip.getSchedule().get(indexOfLastStop + 1);
            }
        }
    }

    /**
     * Calculates and sets new coordinates for vehicle
     *
     * If bus has already reached his next stop, lookup all lines from bus to next stop
     * and compute it's length. Both values are stored in Vehicle property, so there is no need
     * to compute lines every iteration
     *
     * @param simTime current time of simulation
     * @param streetLevel street trafic on current street the bus is
     * @return true if next step is possible, false if the vehicle is already at final stop
     */
    public boolean calculateNextStep(LocalTime simTime, TrafficLevel streetLevel) {
        if (arrivedToFinalStop)
            return false;

        // Check if bus has already reached his next stop
        if (lengthToNextStop <= 0) {
            getLinesFromBusToStop();
            lengthToNextStop = getLengthOfLines();
        }


        // Compute avg velocity from location and time to next stop
        // then include street traffic and compute actual speed and delay increment
        LocalTime arrivalTime = getNextStop().getValue().getArrival();
        long timeToNextStop = simTime.minus(delay).until(arrivalTime, ChronoUnit.SECONDS);
        double avgVelocity = lengthToNextStop / timeToNextStop;
        double actVelocity = avgVelocity * streetLevel.speedRate();
        double deltaTime = 1 - streetLevel.speedRate();
        Log.LOGGER.log(Level.FINEST, String.format("V: '%d' time=%s, delay=%ds, timeToNextStop=%d, lengthToNext=%.2f, avgVelocity=%.2f, actVelocity=%.2f, speedC=%.1f, deltaTime=%f\n",
                id, simTime, delay.getSeconds(), timeToNextStop, lengthToNextStop, avgVelocity, actVelocity, streetLevel.speedRate(), deltaTime));
        delay = delay.plusNanos((long)(deltaTime * 1e9));

        applyStep(actVelocity, simTime);
        return true;
    }

    /**
     * Getter
     * @return delay of vehicle
     */
    public Duration getDelay() {
        return delay;
    }

    /**
     * Set List of Lines between bus and next stop
     * Trims first and last line from bus to stop
     */
    public void getLinesFromBusToStop() {
        if (getNextStop() == null) {
            return;
        }
        linesToNextStop.clear();
        List<Street> loadedStreets = new ArrayList<>();
        boolean foundBus = false;
        boolean foundStop = false;


        // now the trip returns reversed route if it is WAY_BACK
        for (SimpleImmutableEntry<Street, Stop> streetStop : trip.getRoute()) {
            if (loadedStreets.contains(streetStop.getKey())) {
                continue;
            } else {
                loadedStreets.add(streetStop.getKey());
            }

            /**
             * STRONG DEPENDENCE ON STREETS BEING CONSTRUCTED IN SAME DIRECTION AS LINE DIRECTION !!!! BAD !!!
             */
            if (trip.getDirection() == Direction.WAY_THERE) {
                linesToNextStop.addAll(getLinesFoward(streetStop.getKey(), foundBus, foundStop));
            } else {
                linesToNextStop.addAll(getLinesBackward(streetStop.getKey(), foundBus, foundStop));
            }

            foundBus = foundBus();
            foundStop = foundStop();

            if (foundBus && foundStop) {
                break;
            }
        }
    }

    /**
     * Getter
     * @return returns last stop vehicle was or is in
     */
    public SimpleImmutableEntry<Stop, StopTime> getLastStopWithTime() {
        return lastStopWithTime;
    }

    /**
     * Getter
     * @return returns Line of vehicle
     */
    public vut.fit.ija.main.model.map.Line getLine() {
        return trip.getLine();
    }

    /**
     * Compute length of every Line in list and returs it's sum
     * Also sets sum as the new length to next stop
     * @return sum, 0 if empty list
     */
    public Double getLengthOfLines() {
        Double len = 0.0;
        for (javafx.scene.shape.Line line : linesToNextStop) {
            len += getLengthOfLine(line);
        }
        lengthToNextStop = len;
        return len;
    }

    /**
     * Help funtion, only prints line coordinates of given lines to stdout
     * @param lines
     */
    public void printAllLines(List<Line> lines) {
        for (Line line : lines) {
            printLineCoords(line);
        }
    }

    /**
     * Help funtion, only prints line coordinates of given line to stdout
     * @param line
     */
    public void printLineCoords(Line line) {
        System.out.println(String.format("%f, %f\t%f, %f", line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY()));
    }

    /**
     * Help function, only prints vehicle coordinates to stdout
     */
    public void printCoords() {
        System.out.println(String.format("Coords are %f, %f",getVehicleX(), getVehicleY()));

    }

    /**
     * Getter
     * @return circle representation of vehicle on map
     */
    public Circle getCircle() {
        return guiRep;
    }

    /**
     * Gets value of X axis of vehicle
     * @return X coordinate of vehicle
     */
    public Double getVehicleX() {
        return guiRep.getCenterX();
    }

    /**
     * Gets value of Y axis of vehicle
     * @return Y coordinate of vehicle
     */
    public Double getVehicleY() {
        return guiRep.getCenterY();
    }

    /**
     * Sets new X coordinate for vehicle
     * @param x
     */
    public void setVehicleX(Double x) {
        guiRep.setCenterX(x);
    }

    /**
     * Sets new Y coordinate for vehicle
     * @param y
     */
    public void setVehicleY(Double y) {
        guiRep.setCenterY(y);
    }

    /**
     * Checks object property "linesToNextStop"
     * and searches if vehicle is on first or last line
     * @return return true if vehicle found, else otherwise
     */
    private boolean foundBus() {
        if (! linesToNextStop.isEmpty()) {
            Line first = linesToNextStop.get(0);
            Line last = linesToNextStop.get(linesToNextStop.size()-1);
            return first.contains(getVehicleX(), getVehicleY()) || 
                   last.contains(getVehicleX(), getVehicleY());
        }
        return false;
    }

    /**
     * Checks object property "linesToNextStop"
     * and searches if stop is on first or last line
     * @return return true if stop found, else otherwise
     */
    private boolean foundStop() {
        if (! linesToNextStop.isEmpty()) {
            Line first = linesToNextStop.get(0);
            Line last = linesToNextStop.get(linesToNextStop.size()-1);
            Coordinate nextStopCoords = getNextStop().getKey().getCoordinate();
            return first.contains(nextStopCoords.getX(), nextStopCoords.getY()) || 
                   last.contains(nextStopCoords.getX(), nextStopCoords.getY());
        }
        return false;
    }


    /**
     * spread Polyline street to Lines and returs them as List
     * Trims first and last line so it starts from vehicle and ends on stop
     * parse direction of street is foward
     * @param s Polyline street to spread
     * @param foundBus Flag if bus was found in previous streets
     * @param foundStop Flag if stop was found in previous streets
     * @return List of Lines forming given street
     */
    private List<Line> getLinesFoward(Street s, boolean foundBus, boolean foundStop) {
        List<Line> guiLineList = new ArrayList<>();
        List<Coordinate> streetCoords = s.getCoordinates();
        Coordinate nextStopCoords =  getNextStop().getKey().getCoordinate();

        //Gets every point that polyline contains
        for (int i = 1; i < streetCoords.size(); i++) {
            Coordinate c1;
            Coordinate c2;
            c1 = streetCoords.get(i-1);
            c2 = streetCoords.get(i);

            //make straight line from 2 parsed points
            Line newGuiLine = new Line(c1.getX(), c1.getY(), c2.getX(), c2.getY());

            //if found vehicle on line or vehicle has been already found
            if (onLine(newGuiLine, guiRep)) {
                c1 = Coordinate.defaultInstance((int) guiRep.getCenterX(), (int) guiRep.getCenterY());
                foundBus = true;
            }

            if (newGuiLine.contains(nextStopCoords.getX(), nextStopCoords.getY())) {
                c2 = nextStopCoords;
                foundStop = true;
            }
            if (foundBus || foundStop) {
                guiLineList.add(new Line(c1.getX(), c1.getY(), c2.getX(), c2.getY()));
            }

            if (foundBus && foundStop) {
                break;
            }
        }
        
        return guiLineList;
    }

    /**
     * spread Polyline street to Lines and returs them as List
     * Trims first and last line so it starts from vehicle and ends on stop
     * parse direction of street is backward
     * @deprecated almost identical as getLinesFoward (Delete in refactoring)
     * @param s Polyline street to spread
     * @param foundBus Flag if bus was found in previous streets
     * @param foundStop Flag if stop was found in previous streets
     * @return List of Lines forming given street
     */
    private List<Line> getLinesBackward(Street s, boolean foundBus, boolean foundStop) {
        List<Line> ll = new ArrayList<Line>();
        List<Coordinate> streetCoords = s.getCoordinates();
        Coordinate nextStopCoords = getNextStop().getKey().getCoordinate();

        for (int i = streetCoords.size() - 1; i >= 1 ; i--) {
            Coordinate c1;
            Coordinate c2;
            c1 = streetCoords.get(i);
            c2 = streetCoords.get(i-1);

            Line l = new Line(c1.getX(), c1.getY(), c2.getX(), c2.getY());

            //If found vehicle on this line, trim line so it starts on vehicle point
            if (onLine(l, guiRep)) {
                c1 = Coordinate.defaultInstance((int) guiRep.getCenterX(), (int) guiRep.getCenterY());
                foundBus = true;
            }

            //If found stop on this line, trim stop so it ends on stop point
            if (l.contains(nextStopCoords.getX(), nextStopCoords.getY())) {
                c2 = nextStopCoords;
                foundStop = true;
            }

            //If found vehicle or stop add this line to list
            if (foundBus || foundStop) {
                ll.add(new Line(c1.getX(), c1.getY(), c2.getX(), c2.getY()));                    
            }

            //if found both
            if (foundBus && foundStop) {
                break;
            }
        }
        
        return ll;
    }

    /**
     * Checks if given circle (vehicle) is on the line
     * @param l line
     * @param c vehicle
     * @return true of vehicle is lying on the line, false otherwise
     */
    private boolean onLine(Line l, Circle c) {
        return l.contains(c.getCenterX(), c.getCenterY());
    }

    /**
     * Compute length of given line
     * @param line
     * @return euclid length between start and end point
     */
    private Double getLengthOfLine(Line line) {
        return Math.hypot( Math.abs(line.getEndX() - line.getStartX()), Math.abs(line.getEndY() - line.getStartY()) );
    }

    /**
     * Returns departure time of last visited stop by vehicle
     * @return departure time
     */
    public LocalTime getDepartureTime() {
        return lastStopWithTime.getValue().getDeparture();
    }

    /**
     * Apply step on vehicle and compute and set new coordinets to vehicle
     * @param step length of how much should vehicle move
     * @param simTime current simulation time
     */
    private void applyStep(double step, LocalTime simTime) {
        // If step is bigger than length to next stop, automaticly trim step and sets vehicle coordinates to stop coordinates
        if ((lengthToNextStop -= step) <= 0) {
            setVehicleX((double) getNextStop().getKey().getCoordinate().getX());
            setVehicleY((double) getNextStop().getKey().getCoordinate().getY());
            lastStopWithTime = getNextStop();
            getLinesFromBusToStop();
            setBoardingTime(simTime);
            return;
        }

        /**
         * Check every line in linesToNextStop property and measure it's length
         * If step is bigger than line length, set vehicle coordinates to end of line, subtrack step by length of line and continue on next line
         * Also deletes line from list
         * should be imposible to be step bigger than sum of line lengths
         */
        for (int i = 0; i < linesToNextStop.size(); i++) {
            Line l = linesToNextStop.get(i);
            Double len = getLengthOfLine(l);
            if (len <= step) {
                setVehicleX(l.getEndX());
                setVehicleY(l.getEndY());
                linesToNextStop.remove(i);
                i--;
                step -= len;
            } else {
                //When line length is bigger than step, compute new coordinates, sets them and trim current line so it starts from vehicle point
                SimpleImmutableEntry<Double, Double> newCoords = getCoordsFromStart(l, step, len);
                setVehicleX(newCoords.getKey());
                setVehicleY(newCoords.getValue());
                l.setStartX(newCoords.getKey());
                l.setStartY(newCoords.getValue());
                linesToNextStop.remove(i);
                linesToNextStop.add(i, l);
                break;
            }
        }
    }

    /**
     * Sets boarding time to time between scheduled arrival to and departure from stop, but if
     * vehicle is ahead of schedule, the boarding time will be set to the time between current
     * simulation time and scheduled departure
     * @param simTime current simulation time
     */
    private void setBoardingTime(LocalTime simTime) {
        if (lastStopWithTime != null) {
            LocalTime lastArrival = lastStopWithTime.getValue().getArrival();
            LocalTime lastDeparture = lastStopWithTime.getValue().getDeparture();
            // arrival null for first stop, departure null for last stop
            // if arrival null, vehicle not waiting there, if departure null, vehicle will be deleted in next iteration
            if (lastArrival != null && lastDeparture != null) {
                if (delay.isNegative()) {
                    // ahead of schedule, wait at stop longer for scheduled departure
                    boardingTimeLeft = Duration.between(simTime, lastStopWithTime.getValue().getDeparture());
                } else {
                    boardingTimeLeft = Duration.between(lastStopWithTime.getValue().getArrival(), lastStopWithTime.getValue().getDeparture());
                }
            }
        }
    }

    /**
     * Takes given line and step and compute X,Y coordinates when applying step to hypotenuse of line triangle
     * @param line given line
     * @param step length to compute from start of line
     * @param lenOfLine length of line
     * @return X,Y coordinates
     */
    private SimpleImmutableEntry<Double, Double> getCoordsFromStart(Line line, Double step, Double lenOfLine) {
        Double newX = 0.0;
        Double newY = 0.0;
        Double ratio = step / lenOfLine;

        if (line.getStartX() < line.getEndX()) {
            newX = line.getStartX() + ( line.getEndX() - line.getStartX() ) * ratio;
        } else {
            newX = line.getStartX() - ( line.getStartX() - line.getEndX() ) * ratio;
        }

        if (line.getStartY() < line.getEndY()) {
            newY = line.getStartY() + ( line.getEndY() - line.getStartY() ) * ratio;
        } else {
            newY = line.getStartY() - ( line.getStartY() - line.getEndY() ) * ratio;
        }
        return new SimpleImmutableEntry<Double, Double>(newX, newY);
    }

    /**
     * assume simulation time is between departure from first stop and arrival to final stop
     * @param simTime current simulation time
     * @param millisSimStep simulation step length
     */
    public void setPosition(LocalTime simTime, long millisSimStep) {
        Log.LOGGER.log(Level.FINEST, String.format("Set vehicle '%d' to position: \n", id));
        lastStopWithTime = lastVisitedStop(simTime);
        // place vehicle on last visited stop
        setVehicleX((double) lastStopWithTime.getKey().getCoordinate().getX());
        setVehicleY((double) lastStopWithTime.getKey().getCoordinate().getY());
        if (simTime.isBefore(lastStopWithTime.getValue().getDeparture())) {
            // vehicle still boarding at last stop
            boardingTimeLeft = Duration.between(simTime, lastStopWithTime.getValue().getDeparture());
            Log.LOGGER.log(Level.FINEST, String.format("[%.0f,%.0f], vehicle still boarding at: '%s'. (simTime=%s, arrived=%s, departure=%s)\n",
                    getVehicleX(), getVehicleY(), lastStopWithTime.getKey().getId(), simTime, lastStopWithTime.getValue().getArrival(), lastStopWithTime.getValue().getDeparture()));
        } else {
            // move vehicle to its spot
            Log.LOGGER.log(Level.FINEST, String.format("originally [%.0f,%.0f], but vehicle is on the move from '%s'. (simTime=%s, departure=%s)\n",
                    getVehicleX(), getVehicleY(), lastStopWithTime.getKey().getId(), simTime, lastStopWithTime.getValue().getDeparture()));
            LocalTime fakeTempSimTime = LocalTime.from(lastStopWithTime.getValue().getDeparture());
            int counter = 0;
            while(fakeTempSimTime.isBefore(simTime)) {
                this.calculateNextStep(fakeTempSimTime, TrafficLevel.NORMAL);
                fakeTempSimTime = fakeTempSimTime.plusNanos(millisSimStep * (long)1e6);
                counter++;
            }
            Log.LOGGER.log(Level.FINEST, String.format("Vehicle '%s' moved %d times, new position: [%.0f,%.0f]\n", id, counter, getVehicleX(), getVehicleY()));
        }

    }

    /**
     * Returns last visited stop according to simulation time and schedule, assuming no delay
     * @param simTime current simulation time
     * @return last visited stop with arrival before current simulation time
     */
    private SimpleImmutableEntry<Stop, StopTime> lastVisitedStop(LocalTime simTime) {
        if (simTime.isAfter(trip.getDeparture()) && simTime.isBefore(trip.getArrival())) {
            List<AbstractMap.SimpleImmutableEntry<Stop, StopTime>> schedule = trip.getSchedule();
            for (int i = 0; i < schedule.size(); i++) {
                LocalTime arrival = schedule.get(i).getValue().getArrival();
                if (arrival == null) arrival = LocalTime.MIN;   // first stop
                if (arrival.isAfter(simTime)) {
                    // the first stop with arrival in the future will stop search and return previous stop as last visited
                    return schedule.get(i-1);
                }
            }
        }
        System.err.println("Asking for last stop of vehicle that should not be on its way right now.");
        return null;
    }

    /**
     * Getter
     * @return vehicle's trip
     */
    public Trip getTrip() {
        return trip;
    }

    /**
     * Getter
     * @return vehicle's graphical representation
     */
    public Circle getGuiRep() {
        return guiRep;
    }

    /**
     * Getter
     * @return vehicle's schedule
     */
    public List<AbstractMap.SimpleImmutableEntry<Stop, StopTime>> getSchedule() {
        return trip.getSchedule();
    }

    /**
     * @return final stop of vehicle's trip
     */
    public Stop getFinalStop() {
        return trip.getSchedule().get(trip.getSchedule().size() - 1).getKey();
    }

    /**
     * @return last visited stop, null if vehicle has finished its trip
     */
    public Stop getLastVisitedStop() {
        if (lastStopWithTime != null) {
            return lastStopWithTime.getKey();
        } else {
            return null;
        }
    }
}
