package vut.fit.ija.main.data.load;

import vut.fit.ija.main.Log;
import vut.fit.ija.main.data.auxobj.*;
import vut.fit.ija.main.data.parse.StringParser;
import vut.fit.ija.main.model.map.Line;
import vut.fit.ija.main.model.map.Stop;
import vut.fit.ija.main.model.map.Street;
import vut.fit.ija.main.model.map.Coordinate;
import vut.fit.ija.main.model.city.City;
import vut.fit.ija.main.model.schedule.LineIntervalScheme;
import vut.fit.ija.main.model.schedule.StopTime;
import vut.fit.ija.main.model.schedule.Trip;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.logging.Level;

/**
 * Loads data from input directory. Each city must have its own subdirectory.
 * @author xkarpi06
 * @version 1.0
 * @since 1.0
 * created: 22-3-2020, xkarpi06
 * updated: 29-3-2020, xkarpi06
 */
public class DataLoader {

    /** Source directory of data */
    private String directory;

    /**
     * Constructor
     * @param directory input
     */
    public DataLoader(String directory) {
        this.directory = directory;
    }

    /**
     * Loads cities from given directory
     * @return List of cities if succesful, null othervise
     */
    public List<City> loadData() {
        Log.LOGGER.log(Level.FINE, "Trying to load data from {0}", this.directory);
        List<City> loadedCities = null;
        File dataDirectory = new File(this.directory);
        if (dataDirectory.isDirectory()) {
            loadedCities = loadCitiesFromDirectory(dataDirectory);
        } else {
            Log.LOGGER.log(Level.SEVERE, "Missing data directory at {0}", dataDirectory);
        }

        return loadedCities;
    }

    /**
     * Creates list of cities from data directory
     * @param dataDirectory directory with cities
     * @return list of cities or null
     */
    private List<City> loadCitiesFromDirectory(File dataDirectory) {
        List<City> loadedCities = new ArrayList<>();
        // returns list of directories and files in dataDirectory
        File[] directoryContent = dataDirectory.listFiles();
        if (directoryContent != null) {
            // search data directory
            for (File directoryItem : directoryContent) {
                if (directoryItem != null && directoryItem.isDirectory()) {
                    // load city from directory
                    Log.LOGGER.log(Level.FINER, "Loading city from {0}/{1}", new Object[] {this.directory, directoryItem.getName()});
                    loadedCities.add(loadCityFromDirectory(directoryItem));
                }
            }
        }
        return loadedCities;
    }

    /**
     * Creates a city from files in directory
     * @param sourceDirectory input directory with one city
     * @return City, or null
     */
    private City loadCityFromDirectory(File sourceDirectory) {
        // name the city after its directory
        String cityName = sourceDirectory.getName();
        City newCity = City.defaultInstance(cityName);
        Map<String, File> sourceFiles = new HashMap<>();
        for (File directoryItem : sourceDirectory.listFiles()) {
            if (ProjectFiles.SRC_FILES.contains(directoryItem.getName())) {
                sourceFiles.put(directoryItem.getName(), directoryItem);
            }
            Log.LOGGER.log(Level.FINEST, "Contents: {0}", directoryItem.getName());
        }

        List<Street> streets = loadStreets(sourceFiles, cityName);
        List<Stop> stops = loadStops(sourceFiles, cityName, streets);
        List<Line> lines = loadLines(sourceFiles, cityName);

        updateLinesByAddingRoutes(sourceFiles, lines, stops, streets, cityName);
        updateLinesByAddingIntervalSchemes(sourceFiles, lines, cityName);
        updateLinesByAddingTrips(sourceFiles, lines, cityName);

        newCity.setStreets(streets);
        newCity.setStops(stops);
        newCity.setLines(lines);

        return newCity;
    }

    /**
     * Creates list of streets from source file, if present
     * @param sourceFiles source files
     * @param cityName input
     * @return list of streets
     */
    private List<Street> loadStreets(Map<String, File> sourceFiles, String cityName) {
        String sourceFile = ProjectFiles.SRC_STREET_COORDINATES;
        Scanner sc = getScannerInstance(sourceFiles.get(sourceFile));
        List<Street> streets = new ArrayList<>();
        List<Coordinate> coordinates = new ArrayList<>();
        ParsedStreetCoordinate streetCoordinate;
        String tmpStreetId = "";
        if (sc != null) {
            int lineCount = 1;
            while (sc.hasNextLine()) {
                streetCoordinate = (ParsedStreetCoordinate) parseNextLine(sc, cityName, sourceFile, lineCount);
                if (streetCoordinate != null) {
                    if (!tmpStreetId.equals(streetCoordinate.streetId)) {
                        if (lineCount != 1) {
                            createStreet(streets, tmpStreetId, coordinates, cityName);
                        }
                        // reset id & coordinate list
                        tmpStreetId = streetCoordinate.streetId;
                        coordinates = new ArrayList<>();
                    }
                    // gather coordinates to list
                    coordinates.add(Coordinate.defaultInstance(streetCoordinate.x, streetCoordinate.y));
                }
                lineCount++;
            }
            createStreet(streets, tmpStreetId, coordinates, cityName);
        } else {
            Log.LOGGER.log(Level.SEVERE, "Missing file {0} for {1}", new Object[] {sourceFile, cityName});
        }
        return streets;
    }

    /**
     * Tries to add new Street to streets array
     * @param streets input/output
     */
    private void createStreet(List<Street> streets, String tmpStreetId, List<Coordinate> coordinates, String cityName) {
        Street newStreet = Street.defaultInstance(tmpStreetId, coordinates);
        if (newStreet != null) {
            streets.add(newStreet);
        } else {
            Log.LOGGER.log(Level.SEVERE, "Invalid assembly of street {0} in {1}", new Object[] {tmpStreetId, cityName});
        }
    }

    /**
     * Creates list of stops from source file, if present
     * @param sourceFiles source files
     * @param cityName input
     * @return list of stops
     */
    private List<Stop> loadStops(Map<String, File> sourceFiles, String cityName, List<Street> streets) {
        String sourceFile = ProjectFiles.SRC_STOPS;
        Scanner sc = getScannerInstance(sourceFiles.get(sourceFile));
        List<Stop> stops = new ArrayList<>();
        ParsedStop parsedStop;
        if (sc != null) {
            int lineCount = 1;
            while (sc.hasNextLine()) {
                parsedStop = (ParsedStop) parseNextLine(sc, cityName, sourceFile, lineCount);
                if (parsedStop != null) {
                    Stop newStop = Stop.defaultInstance(parsedStop.stopId, Coordinate.defaultInstance(parsedStop.x, parsedStop.y));
                    assignStopToStreet(newStop, parsedStop, streets, cityName, sourceFile, lineCount);
                    stops.add(newStop);
                }
                lineCount++;
            }
        } else {
            Log.LOGGER.log(Level.SEVERE, "Missing file {0} for {1}", new Object[] {sourceFile, cityName});
        }
        return stops;
    }

    /**
     * @param newStop Stop instance
     * @param parsedStop has data about what streetId to match
     * @param streets List of all streets in city
     * @param cityName for logs
     * @param fileName for logs
     * @param lineCount for logs
     */
    private void assignStopToStreet(Stop newStop, ParsedStop parsedStop, List<Street> streets, String cityName, String fileName, int lineCount) {
        boolean assigned = false;
        for (Street s : streets) {
            if (s.getId().equals(parsedStop.streetId)) {
                if (s.addStop(newStop)) {
                    assigned = true;
                    break;
                } else {
                    Log.LOGGER.log(Level.INFO, "Invalid assignment of stop {0} to street {1}. In {2}/{3}, line {4}",
                            new Object[] {parsedStop.stopId, parsedStop.streetId, cityName, fileName, lineCount});
                }
            }
        }
        if (!assigned) {
            Log.LOGGER.log(Level.INFO, "No such street {0}, stop {1} unassigned. In {2}/{3}, line {4}",
                    new Object[] {parsedStop.streetId, parsedStop.stopId, cityName, fileName, lineCount});
        }
    }

    /**
     * Creates list of Lines from source file, if present
     * @param sourceFiles map of available source files
     * @param cityName for log
     * @return
     */
    private List<Line> loadLines(Map<String, File> sourceFiles, String cityName) {
        String sourceFile = ProjectFiles.SRC_LINES;
        Scanner sc = getScannerInstance(sourceFiles.get(sourceFile));
        List<Line> lines = new ArrayList<>();
        Line newLine;
        if (sc != null) {
            int lineCount = 1;
            while (sc.hasNextLine()) {
                newLine = (Line) parseNextLine(sc, cityName, sourceFile, lineCount);
                if (newLine != null) {
                    lines.add(newLine);
                }
                lineCount++;
            }
        } else {
            Log.LOGGER.log(Level.SEVERE, "Missing file {0} for {1}", new Object[] {sourceFile, cityName});
        }
        return lines;
    }

    /**
     * Assigns stops and streets to lines, that creates routes
     * @param sourceFiles map of available source files
     * @param lines all lines
     * @param stops all stops
     * @param streets all streets
     * @param cityName for logs
     */
    private void updateLinesByAddingRoutes(Map<String, File> sourceFiles, List<Line> lines, List<Stop> stops, List<Street> streets, String cityName) {
        String sourceFile = ProjectFiles.SRC_LINE_COMPONENTS;
        Scanner sc = getScannerInstance(sourceFiles.get(sourceFile));
        ParsedLineComponent newComponent;
        if (sc != null) {
            int lineCount = 1;
            while (sc.hasNextLine()) {
                newComponent = (ParsedLineComponent) parseNextLine(sc, cityName, sourceFile, lineCount);
                if (newComponent != null) {
                    Line associatedLine = findLine(newComponent.lineId, lines);
                    if (associatedLine != null) {
                        if (newComponent.type == ParsedLineComponent.ComponentType.STOP) {
                            assignStopToLine(newComponent.componentId, stops, associatedLine, cityName, sourceFile, lineCount);
                        } else {
                            assignStreetToLine(newComponent.componentId, streets, associatedLine, cityName, sourceFile, lineCount);
                        }
                    } else {
                        // invalid lineId
                        Log.LOGGER.log(Level.INFO, "Invalid line id {0} in {1}/{2}, line {3}",
                                new Object[] {newComponent.lineId, cityName, sourceFile, lineCount});
                    }
                }
                lineCount++;
            }
        } else {
            Log.LOGGER.log(Level.SEVERE, "Missing file {0} for {1}", new Object[] {sourceFile, cityName});
        }
    }

    /**
     * Assign Stop with stopId = componentId from stops to line
     * @param componentId stop id to look for
     * @param stops list of all stops
     * @param line the line
     * @param cityName for logs
     * @param fileName for logs
     * @param lineCount for logs
     */
    private void assignStopToLine(String componentId, List<Stop> stops, Line line, String cityName, String fileName, int lineCount) {
        Stop associatedStop = findStop(componentId, stops);
        if (associatedStop != null) {
            if (line.addStop(associatedStop)) {
                return;
            } else {
                System.out.println("stop false");
            }
        } else {
            System.out.println("stop not in stops");
        }
        // stop with componentId not in stops
        Log.LOGGER.log(Level.INFO, "Invalid assignment of stop {0} to line {1}. In {2}/{3}, line {4}",
                new Object[] {componentId, line.getId(), cityName, fileName, lineCount});
    }

    /**
     * Assign Street with streetId = componentId from streets to line
     * @param componentId stop id to look for
     * @param streets list of all stops
     * @param line the line
     * @param cityName for logs
     * @param fileName for logs
     * @param lineCount for logs
     */
    private void assignStreetToLine(String componentId, List<Street> streets, Line line, String cityName, String fileName, int lineCount) {
        Street associatedStreet = findStreet(componentId, streets);
        if (associatedStreet != null) {
            if (line.addStreet(associatedStreet)) {
                return;
            }
        }
        // street with componentId not in streets
        Log.LOGGER.log(Level.INFO, "Invalid assignment of street {0} to line {1}. In {2}/{3}, line {4}",
                new Object[] {componentId, line.getId(), cityName, fileName, lineCount});
    }

    /**
     * Find line in list by id
     * @return Line if successful
     */
    private Line findLine(String lineId, List<Line> lines) {
        for (Line line : lines) {
            if (line.getId().equals(lineId)) {
                return line;
            }
        }
        return null;
    }

    /**
     * Find Stop in list by id
     * @return Stop if successful
     */
    private Stop findStop(String componentId, List<Stop> stops) {
        for (Stop s : stops) {
            if (s.getId().equals(componentId)) {
                return s;
            }
        }
        return null;
    }

    /**
     * Find Street in list by id
     * @return Street if successful
     */
    private Street findStreet(String componentId, List<Street> streets) {
        for (Street s : streets) {
            if (s.getId().equals(componentId)) {
                return s;
            }
        }
        return null;
    }

    /**
     * Load LineIntervalSchemes and assign them to lines
     * @param sourceFiles provided source files
     * @param lines list of all lines
     * @param cityName for logs
     */
    private void updateLinesByAddingIntervalSchemes(Map<String, File> sourceFiles, List<Line> lines, String cityName) {
        String sourceFile = ProjectFiles.SRC_LINE_INTERVAL_SCHEMES;
        Scanner sc = getScannerInstance(sourceFiles.get(sourceFile));
        List<StopTime> stopTimes = new ArrayList<>();
        ParsedLineIntervalScheme newScheme;
        String tmpLineId = "";
        if (sc != null) {
            int lineCount = 1;
            while (sc.hasNextLine()) {
                newScheme = (ParsedLineIntervalScheme) parseNextLine(sc, cityName, sourceFile, lineCount);
                if (newScheme != null) {
                    if (!tmpLineId.equals(newScheme.lineId)) {
                        if (lineCount != 1) {
                            assignIntervalSchemeToLine(lines, tmpLineId, stopTimes, String.format("%s/%s, line %d", cityName, sourceFile, lineCount));
                        }
                        // reset id & scheme list
                        tmpLineId = newScheme.lineId;
                        stopTimes = new ArrayList<>();
                    }
                    // gather schemes to list
                    stopTimes.add(new StopTime(newScheme.arrival, newScheme.departure));
                }
                lineCount++;
            }
            assignIntervalSchemeToLine(lines, tmpLineId, stopTimes, String.format("%s/%s, line %d", cityName, sourceFile, lineCount));
        } else {
            Log.LOGGER.log(Level.SEVERE, "Missing file {0} for {1}", new Object[] {sourceFile, cityName});
        }
    }

    /**
     * Create LineIntervalScheme and try to assign it to Line
     * @param lines list of all lines
     * @param tmpLineId id of the line we are assigning scheme to
     * @param stopTimes list of stop times we will use for creating scheme
     * @param locationMsg info about issue source for logs
     */
    private void assignIntervalSchemeToLine(List<Line> lines, String tmpLineId, List<StopTime> stopTimes, String locationMsg) {
        boolean found = false;
        for (Line line : lines) {
            if (line.getId().equals(tmpLineId)) {
                found = true;
                LineIntervalScheme newScheme = LineIntervalScheme.create(
                        stopTimes.get(0).getDeparture(),                    // departure
                        stopTimes.subList(1, stopTimes.size() - 1),         // transit stops
                        stopTimes.get(stopTimes.size() - 1).getArrival()    // arrival
                );
                if (newScheme != null) {
                    if (!line.setIntervalScheme(newScheme)) {
                        Log.LOGGER.log(Level.INFO, "Incompatible line interval scheme for line {0} in {1}", new Object[] {tmpLineId, locationMsg});
                    }
                } else {
                    Log.LOGGER.log(Level.INFO, "Invalid scheme in {0}", locationMsg);
                }
            }
        }
        if (!found) {
            Log.LOGGER.log(Level.INFO, "No such line {0} in {1}", new Object[] {tmpLineId, locationMsg});
        }
    }

    /**
     * Loads trips and assigns them to specified lines
     * @param sourceFiles provided source files
     * @param lines all of the lines
     * @param cityName for logs
     */
    private void updateLinesByAddingTrips(Map<String, File> sourceFiles, List<Line> lines, String cityName) {
        String sourceFile = ProjectFiles.SRC_TRIPS;
        Scanner sc = getScannerInstance(sourceFiles.get(sourceFile));
        ParsedTrip parsedTrip;
        if (sc != null) {
            int lineCount = 1;
            while (sc.hasNextLine()) {
                parsedTrip = (ParsedTrip) parseNextLine(sc, cityName, sourceFile, lineCount);
                if (parsedTrip != null) {
                    createTrip(parsedTrip, lines, String.format("%s/%s, line %d", cityName, sourceFile, lineCount));
                }
                lineCount++;
            }
        } else {
            Log.LOGGER.log(Level.SEVERE, "Missing file {0} for {1}", new Object[] {sourceFile, cityName});
        }
    }

    /**
     * Tries to create new Trip from parsedTrip
     * @param parsedTrip aux object with parsed Trip info
     * @param lines all lines to find one that will be related with new Trip
     * @param locationMsg for logs
     */
    private void createTrip(ParsedTrip parsedTrip, List<Line> lines, String locationMsg) {
        boolean found = false;
        for (Line line : lines) {
            if (line.getId().equals(parsedTrip.lineId)) {
                found = true;
                if (line.getIntervalScheme() != null) {
                    Trip newTrip = Trip.defaultInstance(line, parsedTrip.direction, parsedTrip.departure);
                } else {
                    Log.LOGGER.log(Level.INFO, "Line {0} is missing interval scheme! Couldn't assign trip. {1}", new Object[] {line.getId(), locationMsg});
                }
            }
        }
        if (!found) {
            Log.LOGGER.log(Level.INFO, "No such line {0}. {1}", new Object[] {parsedTrip.lineId, locationMsg});
        }
    }

    /**
     * Creates scanner instance if possible
     * @param file input
     * @return Scanner or null
     */
    private Scanner getScannerInstance(File file) {
        Scanner sc = null;
        if (file != null) {
            try {
                sc = new Scanner(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return sc;
    }

    /**
     * Reads next line from scanner and returns result as object
     *
     * Object type is based on fileName, each fileName has different
     * strategy for parsing line into variables. This strategy is chosen
     * in creator of StringParser
     * @param sc scanner, assumed existing and hasNextLine
     * @param cityName city for logs
     * @param fileName name of source file for logs and strategy
     * @param lineCount counter for logs
     * @return null if line is invalid, instance if everything is correct.
     */
    private Object parseNextLine(Scanner sc, String cityName, String fileName, int lineCount) {
        Object result = null;
        StringParser sp = new StringParser(fileName);
        try {
            String line = sc.nextLine();
            if (!line.trim().isEmpty()) {
                result = sp.exectueStrategy(line);
                if (result == null) {
                    Log.LOGGER.log(Level.INFO, "Invalid line in {0}/{1}, line {2}", new Object[]{cityName, fileName, lineCount});
                }
            }
        } catch (NoSuchElementException|IllegalStateException e) {
            e.printStackTrace();
        }
        return result;
    }

}
