package vut.fit.ija.main.controller;

import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import vut.fit.ija.main.Log;
import vut.fit.ija.main.data.load.DataLoader;
import vut.fit.ija.main.data.load.ProjectFiles;
import vut.fit.ija.main.model.city.City;
import vut.fit.ija.main.model.map.Street;
import vut.fit.ija.main.model.schedule.StopTime;
import vut.fit.ija.main.model.schedule.Trip;
import vut.fit.ija.main.model.vehicles.TrafficLevel;
import vut.fit.ija.main.model.vehicles.Vehicle;
import vut.fit.ija.main.model.map.Coordinate;
import vut.fit.ija.main.model.map.Line;
import vut.fit.ija.main.model.map.Stop;

/**
 * Controller of the traffic monitoring simulation application
 * @author xsaman02
 * @version 1.0
 * @since 1.0
 * created: 04-04-2020, xsaman02
 * updated: 22-04-2020, xkarpi06 time slider, schedule itinerary, scheduled departures
 */
public class Controller implements Initializable {

    //Components loaded when parsing xml javafx file
    @FXML private Button flowButton;
    @FXML private Label clock;
    @FXML private Slider simulationSlider;  // time of day in seconds
    @FXML private Label timeSpeedLabel;
    @FXML private ComboBox<TrafficLevel> streetDensityBox;
    @FXML private TextField streetNameField;
    @FXML private TextField stopNameField;
    @FXML private TextField stopStreetNameField;
    @FXML private TextField busLineNameField;
    @FXML private Button scheduleButton;
    @FXML private TextField busLineTypeField;
    @FXML private TextField busNextStopNameField;
    @FXML private TextField busNextStopArrivalTimeField;
    @FXML private TextField busDelay;
    @FXML private ComboBox<String> cityBox;
    @FXML private Pane map;
    @FXML private Pane itinerary;    // vehicle schedule progress info, ADD TO MAP AFTER CLEARING MAP
    @FXML private Label itineraryLineId;
    @FXML private Label itineraryFinalStop;
    @FXML private Label itineraryDelay;
    @FXML private Pane lineSchedule;   // line schedule detailed info, ADD TO MAP AFTER CLEARING MAP
    @FXML private Label lineScheduleLineId;
    @FXML private Label lineScheduleFinalStop;

    //Other properties
    private DataLoader dl;
    private MouseGestures mg;
    private Timeline clockSimulation;
    private LocalTime simTime;
    private double timeSpeed = 1.0;
    private int timeStepInSec = 1;
    private boolean stopSimulation = false;
    private boolean simSliderMovingDuringStop = false;

    private List<City> cities;
    private City selectedCity;
    public Map<Street, Polyline> streets;
    public Map<Stop, Rectangle> stops;
    private List<Vehicle> activeVehicles;

    public Street selectedStreet;
    public Stop selectedStop;
    public Vehicle selectedVehicle;

    // colors for graphic elements
    private final Color COLOR_STREET_DEFAULT = Color.BLACK;
    private final Color COLOR_STREET_SELECTED = Color.WHITE;
    private final Color COLOR_STOP_DEFAULT= Color.RED;
    private final Color COLOR_STOP_SELECTED = Color.WHITE;
//    private final Color COLOR_VEHICLE_DEFAULT = Color.LIME;   // use getVehicleDefaultColor()
    private final Color COLOR_VEHICLE_SELECTED = Color.WHITE;

    // used LocalTime formats
    private DateTimeFormatter timeOfDayFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
    private DateTimeFormatter delayFormat = DateTimeFormatter.ofPattern("+m:ss");
    private DateTimeFormatter negativeDelayFormat = DateTimeFormatter.ofPattern("-m:ss");
    private DateTimeFormatter delayOverHourFormat = DateTimeFormatter.ofPattern("+H:mm:ss");
    private List<Node> beautifulElements = new ArrayList<>();   // elements that create the majestic horizontal line with stops, labels and times
    private List<Node> lineScheduleElements = new ArrayList<>();   // elements that create line schedule

    /**
     * Called automaticly when launching app
     * Initializes properties
     */
    @Override
    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        dl = new DataLoader(ProjectFiles.DATA_DIRECTORY);
        mg = new MouseGestures(this);
        streets = new HashMap<>();
        stops = new HashMap<>();
        simTime = LocalTime.MIN;

        simulationSlider.setMin(0);
        simulationSlider.setMax(LocalTime.MAX.toSecondOfDay());
        activeVehicles = new ArrayList<>();

        mg.makeClickable(map);

        //loads options for city and street density combos
        loadComboOptions();
        //starts time simulation
        initializeTime();
    }

    /**
     * Function called by javafx
     * on button click, resumes or stops simulation
     */
    @FXML
    private void changeFlow() {
        stopSimulation = !stopSimulation;
        if (stopSimulation) {
            flowButton.setText("Play");
        } else {
            flowButton.setText("Pause");
        }
    }

    /**
     * called by javafx
     * stop simulation and change simulation time
     */
    @FXML
    private void simulationSliderMousePressed() {
        simSliderMovingDuringStop = stopSimulation;
        stopSimulation = true;
        setSimulationTime((long)(simulationSlider.getValue() * 1e9));
    }

    /**
     * Sets simulation time and updates GUI elements time label and slider
     * @param nanosOfDay time of day in nanoseconds
     */
    private void setSimulationTime(long nanosOfDay) {
        simTime = LocalTime.ofNanoOfDay(nanosOfDay);
        simulationSlider.setValue(nanosOfDay / 1e9);
        clock.setText(simTime.format(timeOfDayFormat));
    }

    /**
     * called by javafx
     * change simulation time
     */
    @FXML
    private void simulationSliderMouseDragged() {
        setSimulationTime((long)(simulationSlider.getValue() * 1e9));
    }

    /**
     * called by javafx
     * compute simulation state (position of all vehicles) and start simulation
     */
    @FXML
    private void simulationSliderMouseReleased() {
        setSimulationTime((long)(simulationSlider.getValue() * 1e9));
        stopSimulation = simSliderMovingDuringStop;
        computeSimulationStateAfterTimeChange();
    }

    /**
     * According to current simulation time finds out which trips are being executed,
     * creates vehicles and finds where on map to place them, and places them there
     * simulation than continues with step increments in clockSimulation.handle()
     */
    private void computeSimulationStateAfterTimeChange() {
        Log.LOGGER.log(Level.FINE, String.format("New time = %s, establish current state.\n", simTime.format(timeOfDayFormat)));
        clearVehiclesFromMap();
        findNewActiveVehicles();
    }

    /**
     * Put vehicle in GUI map and also store in active vehicles
     * @param vehicle the vehicle
     */
    private void putVehicleOnMap(Vehicle vehicle) {
        vehicle.setPosition(simTime, timeStepInSec * 1000);
        mg.makeClickable(vehicle.getGuiRep());
        map.getChildren().addAll(vehicle.getGuiRep());
        activeVehicles.add(vehicle);
        Log.LOGGER.log(Level.FINE, String.format("Vehicle added to active vehicles, new amount: %d", activeVehicles.size()));
    }

    /**
     * removes one vehicle from map. if the vehicle was selected, it unselects the vehicle which
     * will change the textfields showing info about the vehicle
     * @param vehicle the vehicle to be removed
     */
    private void clearVehicleFromMap(Vehicle vehicle) {
        activeVehicles.remove(vehicle);
        map.getChildren().remove(vehicle.getGuiRep());
        if (vehicle.equals(selectedVehicle))
            unselectVehicle();
    }

    /**
     * Removes all vehicles currently represented in GUI
     */
    private void clearVehiclesFromMap() {
        unselectVehicle();
        int children = map.getChildren().size();
        int active = activeVehicles.size();
        for (Vehicle vehicle : activeVehicles) {
            map.getChildren().remove(vehicle.getGuiRep());
        }
        activeVehicles.clear();
        unselectVehicle();
    }

    /**
     * Function called by javafx
     * On combo value changes sets choosen traffic level to clicked street
     */
    @FXML
    private void densityChanged() {
        if (selectedStreet != null) {
            selectedStreet.setTrafficLevel(streetDensityBox.getSelectionModel().getSelectedItem());
        }
    }

    /**
     * Function called by javafx
     * Decreases simulation time speed
     * Minimal speed is 0.1
     */
    @FXML
    private void decreaseTimeSpeed() {
        if (timeSpeed <= 0.1)
            return;
        else if (timeSpeed <= 1)
            timeSpeed -= 0.2;
        else if (timeSpeed <= 5)
            timeSpeed -= 1;
        else if (timeSpeed <= 30)
            timeSpeed -= 5;
        else if (timeSpeed <= 80)
            timeSpeed -= 10;
        else
            timeSpeed -= 20;

        timeSpeedLabel.setText(String.format("%.1f ", timeSpeed));
        initializeTime();
    }

    /**
     * Function called by javafx
     * Increases simulation time speed
     */
    @FXML
    private void increaseTimeSpeed() {
        if (timeSpeed < 1)
            timeSpeed += 0.2;
        else if (timeSpeed < 5)
            timeSpeed += 1;
        else if (timeSpeed < 30)
            timeSpeed += 5;
        else if (timeSpeed < 80)
            timeSpeed += 10;
        else
            timeSpeed += 20;
            timeSpeedLabel.setText(String.format("%.1f ", timeSpeed));
        initializeTime();
    }

    /**
     * Function called by javafx
     * On Combo value change, restart time and redraw map for new city
     */
    @FXML
    private void cityChanged() {
        unselectVehicle();
        unselectStop();
        unselectStreet();
        drawMap();
    }

    /**
     * Function called by javafx
     * Display schedule detail information
     */
    @FXML
    private void scheduleButtonClicked() {
        if (lineSchedule.isVisible()) {
            hideLineSchedule();
        } else {
            setAndDisplayLineSchedule(selectedVehicle);
        }
    }


    /**
     * Draws new city from comboBox
     * If city not found, do nothing
     */
    private void drawMap() {
        map.getChildren().clear();
        map.getChildren().add(itinerary);
        map.getChildren().add(lineSchedule);

        String currentCity = cityBox.getSelectionModel().getSelectedItem().toString();
        for (City c : cities) {
            if (c.getId().equals(currentCity)) {
                selectedCity = c;
                break;
            }
        }

        if (selectedCity == null) {
            return;
        }

        drawStreets();
        drawStops();
        updateBuses();
    }

    /**
     * Draw streets of the city and save Streets and Polylines of streets to HashMap
     * Also make Polyline clickable
     */
    private void drawStreets() {
        final double STREET_WIDTH = 3;
        for (Street street : selectedCity.getStreets()) {
            Polyline p = new Polyline();
            List<Double> points = new ArrayList<Double>();
            for (Coordinate c : street.getCoordinates()) {
                points.add((double) c.getX());
                points.add((double) c.getY());
            }


            p.getPoints().addAll(points);
            p.setStrokeWidth(STREET_WIDTH);
            mg.makeClickable(p);
            streets.put(street, p);
            street.setTrafficLevel(TrafficLevel.NORMAL);
            map.getChildren().addAll(p);
        }
    }

    /**
     * Draw stops of the city and save Stops and Rectangles of stops to HashMap
     * Also make Rectangle clickable
     */
    private void drawStops() {
        final double STOP_SIZE = 5;
        for (Stop stop : selectedCity.getStops()) {
            Coordinate point = stop.getCoordinate();
            Rectangle rectangle = new Rectangle(point.getX() - STOP_SIZE/2, point.getY() - STOP_SIZE/2, STOP_SIZE, STOP_SIZE);
            rectangle.setFill(COLOR_STOP_DEFAULT);
            mg.makeClickable(rectangle);
            stops.put(stop, rectangle);
            map.getChildren().addAll(rectangle);
        }
    }

    /**
     * Finds out traffic level of current street vehicle is on
     * @param v Vehicle representation on map
     * @return Traffic level of current street
     */
    private TrafficLevel getTrafficLevel(Circle v) {
        for (Entry<Street, Polyline> s : streets.entrySet()) {
            if (s.getValue().contains(v.getCenterX(), v.getCenterY())) {
                return s.getKey().getTrafficLevel();
            }
        }
        return null;
    }

    /**
     * Checks every active bus and updates it's coordinates
     */
    private void updateBuses() {
        findNewActiveVehicles();

        for (int i = 0; i < activeVehicles.size(); i++) {
            Vehicle vehicle = activeVehicles.get(i);
            if (vehicle.waitingToDepart(simTime, (long)(timeStepInSec * 1000))) {
                continue;
            }
            boolean notAtFinalStop = vehicle.calculateNextStep(simTime, getTrafficLevel(vehicle.getGuiRep()));
            if (!notAtFinalStop)
                clearVehicleFromMap(vehicle);
            if (vehicle.equals(selectedVehicle))
                updateSelectedVehicleDisplayedInfo(vehicle);

        }
    }

    /**
     * Checks current simulation time and if the trip is "active" according to time,
     * creates vehicle, puts it to GUI and to activeVehicles
     */
    private void findNewActiveVehicles() {
        Log.LOGGER.log(Level.FINE,"Find new active vehicles");
        for(Line line : selectedCity.getLines()) {
            for (Trip trip : line.getTrips()) {
                if (simTime.isAfter(trip.getDeparture()) && simTime.isBefore(trip.getArrival())) {
                    if (!isAlreadyActive(trip)) {
                        Vehicle vehicle = new Vehicle(trip, getLineDefaultColor(line.getId()));
                        putVehicleOnMap(vehicle);
                    }
                }
            }
        }
    }

    /**
     * Tells if the trip is already represented as vehicle in activeVehicles
     * @param trip the trip
     * @return true if yes, false if no
     */
    private boolean isAlreadyActive(Trip trip) {
        for (Vehicle active : activeVehicles) {
            if (active.getTrip().equals(trip))
                return true;
        }
        return false;
    }

    /**
     * loads city and traffic level combos with options
     */
    private void loadComboOptions() {
        cities = dl.loadData();
        ObservableList<String> cityNames = FXCollections.observableArrayList();
        for (City c : cities) {
            cityNames.add(c.getId());
        }
        if (cityNames.size() > 0) {
            cityBox.setItems(cityNames);
            cityBox.getSelectionModel().select(0);
            drawMap();
        }

        streetDensityBox.getItems().addAll(TrafficLevel.values());
    }

    /**
     * Sets new TimeLine which will be called periodicly
     */
    private void initializeTime() {
        if (clockSimulation != null) {
            clockSimulation.stop();
        }
        clockSimulation = new Timeline(new KeyFrame(Duration.millis(1000 / timeSpeed), new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if (!stopSimulation) {
                    setSimulationTime(simTime.plusSeconds(timeStepInSec).toNanoOfDay());
                    updateBuses();
                }
            }
        }));
        clockSimulation.setCycleCount(Timeline.INDEFINITE);
        clockSimulation.play();
    }

    /**
     * Update textfields containing vehicle info, that may change as the vehicle progresses on its line
     * @param v vehicle containing data to display
     */
    private void updateSelectedVehicleDisplayedInfo(Vehicle v) {
        if (v.getNextStop() != null) {
            busNextStopNameField.setText(v.getNextStop().getKey().getId());
            busNextStopArrivalTimeField.setText(v.getNextStop().getValue().getArrival().format(timeOfDayFormat));
            setBusDelayTextField(v);
        }
        hideItinerary();
        setAndDisplayItinerary(v);
        if (lineSchedule.isVisible()) {
            hideLineSchedule();
            setAndDisplayLineSchedule(v);
        }
    }

    private void setBusDelayTextField(Vehicle v) {
        LocalTime delayAsTime;
        DateTimeFormatter dtf;
        if (v.getDelay().isNegative()) {
            busDelay.setStyle("-fx-text-inner-color: green;");
            delayAsTime = LocalTime.MIN.minus(v.getDelay());
            dtf = negativeDelayFormat;
        } else if (v.getDelay().isZero()) {
            busDelay.setStyle("-fx-text-inner-color: green;");
            delayAsTime = LocalTime.MIN;
            dtf = delayFormat;
        } else {
            busDelay.setStyle("-fx-text-inner-color: red;");
            delayAsTime = LocalTime.MIN.plus(v.getDelay());
            if (v.getDelay().toHours() < 1) {
                dtf = delayFormat;
            } else {
                dtf = delayOverHourFormat;
            }
        }

        busDelay.setText(delayAsTime.format(dtf));
    }

    /**
     * Change GUI so no street is selected and no street info is displayed anymore
     */
    public void unselectStreet() {
        if (selectedStreet != null) {
            streets.get(selectedStreet).setStroke(COLOR_STREET_DEFAULT);
            selectedStreet = null;
        }
        streetDensityBox.setDisable(true);
        streetDensityBox.getSelectionModel().select(null);
        streetNameField.setText(null);
    }
    /**
     * Change GUI so the clicked street appears as the only selected and display info about it
     * @param clicked polyline clicked in GUI representing street
     */
    public void selectStreet(Polyline clicked) {
        if (selectedStreet != null) {
            unselectStreet();
        }
        for (Entry<Street, Polyline> line : streets.entrySet()) {
            if (line.getValue().equals(clicked)) {
                streetDensityBox.getSelectionModel().select(line.getKey().getTrafficLevel());
                streetNameField.setText(line.getKey().getId());
                clicked.setStroke(COLOR_STREET_SELECTED);
                streetDensityBox.setDisable(false);
                selectedStreet = line.getKey();
                break;  // skip rest of for loop
            }
        }
    }

    /**
     * Change GUI so no stop is selected and no stop info is displayed anymore
     */
    public void unselectStop() {
        if (selectedStop != null) {
            stops.get(selectedStop).setFill(COLOR_STOP_DEFAULT);
            selectedStop = null;
        }
        stopNameField.setText(null);
        stopStreetNameField.setText(null);
    }

    /**
     * Change GUI so the clicked stop appears as the only selected and display info about it
     * @param clicked rectangle clicked in GUI representing stop
     */
    public void selectStop(Rectangle clicked) {
        if (selectedStop != null) {
            unselectStop();
        }
        for (Entry<Stop, Rectangle> r : stops.entrySet()) {
            if (r.getValue().equals(clicked)) {
                clicked.setFill(COLOR_STOP_SELECTED);
                stopNameField.setText(r.getKey().getId());
                stopStreetNameField.setText(r.getKey().getStreet().getId());
                selectedStop = r.getKey();
                break;  // skip the rest of for loop
            }
        }
    }

    /**
     * Change GUI so no vehicle is selected and no vehicle info is displayed anymore
     */
    public void unselectVehicle() {
        if (selectedVehicle != null) {
            selectedVehicle.getGuiRep().setFill(getLineDefaultColor(selectedVehicle.getLine().getId()));
            unhighlightLineStreets(selectedVehicle.getLine());
            selectedVehicle = null;
            hideItinerary();
            hideLineSchedule();
        }
        busLineNameField.setText(null);
        scheduleButton.setDisable(true);
        busLineTypeField.setText(null);
        busNextStopNameField.setText(null);
        busNextStopArrivalTimeField.setText(null);
        busDelay.setText(null);
    }

    /**
     * Change GUI so the clicked vehicle appears as the only selected and display info about it
     * @param clicked circle clicked in GUI representing vehicle
     */
    public void selectVehicle(Circle clicked) {
        if (selectedVehicle != null) {
            unselectVehicle();
        }
        for (Vehicle vehicle : activeVehicles) {
            if (vehicle.getGuiRep().equals(clicked)) {
                clicked.setFill(COLOR_VEHICLE_SELECTED);
                busLineNameField.setText(vehicle.getLine().getId());
                scheduleButton.setDisable(false);
                busLineTypeField.setText(vehicle.getLine().getTransitType().toString());
                busNextStopNameField.setText(vehicle.getNextStop().getKey().getId());
                busNextStopArrivalTimeField.setText(vehicle.getNextStop().getValue().getArrival().toString());
                setBusDelayTextField(vehicle);
                selectedVehicle = vehicle;
                setAndDisplayItinerary(vehicle);
                highlightLineStreets(vehicle.getLine());
                break;  // skip the rest of for loop
            }
        }
    }

    /**
     *  Hides itinerary and clears drawn elements of itinerary
     */
    private void hideItinerary() {
        itinerary.setVisible(false);    // hide itinerary showing vehicle schedule info
        itinerary.getChildren().removeAll(beautifulElements);
        beautifulElements.clear();
    }

    /**
     * Hides line schedule and clears drawn elements to lineSchedule
     */
    private void hideLineSchedule() {
        lineSchedule.setVisible(false);    // hide itinerary showing vehicle schedule info
        lineSchedule.getChildren().removeAll(lineScheduleElements);
        lineScheduleElements.clear();
    }

    /**
     * Sets itinerary content (Vehicle schedule info) to match selected vehicle and makes itinerary visible in GUI
     * @param vehicle selected vehicle
     */
    private void setAndDisplayItinerary(Vehicle vehicle) {
        itineraryLineId.setText(vehicle.getLine().getId());
        itineraryFinalStop.setText(vehicle.getFinalStop().getId());
        if (vehicle.getDelay().toMinutes() >= 1) {
            itineraryDelay.setText(String.format("+%dmin", vehicle.getDelay().toMinutes()));
        } else {
            itineraryDelay.setText(null);
        }
        drawBeautifulItineraryScheduleScheme(vehicle);
        itinerary.toFront();
        itinerary.setVisible(true);
    }

    /**
     * Sets line schedule content (Line scheduled departures) to match selected vehicle and makes it visible in GUI
     * @param vehicle
     */
    private void setAndDisplayLineSchedule(Vehicle vehicle) {
        lineScheduleLineId.setText(vehicle.getLine().getId());
        lineScheduleFinalStop.setText(vehicle.getFinalStop().getId());
        drawLineScheduledDepartures(vehicle);
        lineSchedule.toFront();
        lineSchedule.setVisible(true);
    }

    /**
     * Draws visual representation of current vehicle progress on its line to the itinerary pane
     * draws line with circles as stops with stop names above and departure times under
     * @param vehicle the vehicle to get data from
     */
    private void drawBeautifulItineraryScheduleScheme(Vehicle vehicle) {
        double lineWidth = 4;
        Color colorOfVisited = Color.GRAY;
        Color colorOfFuture = Color.AQUAMARINE;
        DateTimeFormatter hhmmFormat = DateTimeFormatter.ofPattern("HH:mm");

        double availableWidth = itinerary.getWidth() - 30;
        double lineY = itinerary.getHeight() - 40;
        double lineXstart = 15;
        double lineXend = lineXstart + availableWidth;

        double nameOffsetX = 0;
        double nameOffsetY = -10;
        double timeOffsetX = 5;
        double timeOffsetY = 45;
        double delayOffsetX = timeOffsetX + 10;
        double delayOffsetY = timeOffsetY - 1.5;

        List<AbstractMap.SimpleImmutableEntry<Stop, StopTime>> schedule = vehicle.getSchedule();
        AbstractMap.SimpleImmutableEntry<Stop, StopTime> previousStop = vehicle.getLastStopWithTime();

        if (previousStop == null) return;

        // draw horizontal line
        double segmentLength = availableWidth / (schedule.size() - 1);
        int indexOfPreviousStop = schedule.indexOf(previousStop);
        double visitedLength = segmentLength * indexOfPreviousStop;
        javafx.scene.shape.Line lineVisited = new javafx.scene.shape.Line(lineXstart, lineY, lineXstart + visitedLength, lineY);
        javafx.scene.shape.Line lineFuture = new javafx.scene.shape.Line(lineXstart + visitedLength, lineY, lineXend, lineY);
        lineVisited.setStrokeWidth(lineWidth);
        lineFuture.setStrokeWidth(lineWidth);
        lineVisited.setStroke(colorOfVisited);
        lineFuture.setStroke(colorOfFuture);
        beautifulElements.add(lineVisited);
        beautifulElements.add(lineFuture);

        // draw everything else
        for (int i = 0; i < schedule.size(); i++) {
            // draw stops
            double stopX = lineXstart + i * segmentLength;
            Circle stopCircle = new Circle(stopX, lineY, lineWidth + 1);
            Color stopColor = (i <= indexOfPreviousStop) ? colorOfVisited : colorOfFuture;
            stopCircle.setFill(stopColor);
            beautifulElements.add(stopCircle);

            // draw stop names
            Text stopLabel = new Text(schedule.get(i).getKey().getId());
            Bounds bounds = stopLabel.getLayoutBounds();
            stopLabel.setLayoutX(stopX + nameOffsetX - bounds.getWidth() / 2);
            stopLabel.setLayoutY(lineY + nameOffsetY - bounds.getWidth() / 2);
            stopLabel.setRotate(-90);
            beautifulElements.add(stopLabel);

            // draw stop times
            LocalTime timeToPrint;
            if (i == schedule.size() - 1) {
                timeToPrint = schedule.get(i).getValue().getArrival();
            } else {
                timeToPrint = schedule.get(i).getValue().getDeparture();
            }
            Text timeLabel = new Text(timeToPrint.format(hhmmFormat));
            timeLabel.setStyle("-fx-font-size:10");
            bounds = timeLabel.getLayoutBounds();
            timeLabel.setLayoutX(stopX + timeOffsetX - bounds.getWidth() / 2);
            timeLabel.setLayoutY(lineY + timeOffsetY - bounds.getWidth() / 2);
            timeLabel.setRotate(-90);
            beautifulElements.add(timeLabel);

            // draw delay
            if (vehicle.getDelay().toMinutes() >= 1 && i > indexOfPreviousStop) {
                Text delayLabel = new Text(timeToPrint.plus(vehicle.getDelay()).format(hhmmFormat));
                delayLabel.setStyle("-fx-font-size:9");
//                delayLabel.setStroke(Color.RED);
                delayLabel.setFill(Color.RED);
                bounds = delayLabel.getLayoutBounds();
                delayLabel.setLayoutX(stopX + delayOffsetX - bounds.getWidth() / 2);
                delayLabel.setLayoutY(lineY + delayOffsetY - bounds.getWidth() / 2);
                delayLabel.setRotate(-90);
                beautifulElements.add(delayLabel);
            }
        }

        itinerary.getChildren().addAll(beautifulElements);
    }

    /**
     * Draws departure times for particular direction on that line
     * @param vehicle the vehicle to get data from
     */
    private void drawLineScheduledDepartures(Vehicle vehicle) {
        double lineWidth = 4;
        Color colorOfVisited = Color.GRAY;
        Color colorOfFuture = Color.AQUAMARINE;

        double availableHeight = lineSchedule.getHeight() - 45;
        double lineX = 20;
        double lineYstart = 30;
        double lineYend = lineYstart + availableHeight;

        double hourOffsetX = 12;
        double hourOffsetY = 5;
        double minuteOffsetX = hourOffsetX + 23;
        double minuteOffsetY = hourOffsetY - 2;
        double minuteXSpan = 17;
//        double timeOffsetX = 5;
//        double timeOffsetY = 45;
//        double delayOffsetX = timeOffsetX + 10;
//        double delayOffsetY = timeOffsetY - 1.5;

        // fetch data
        List<Trip> tripsInVehiclesDirection = vehicle.getLine().getTripsInDirection(vehicle.getTrip().getDirection());
        int hourOfFirstTrip = tripsInVehiclesDirection.get(0).getDeparture().getHour();
        int hourOfLastTrip = tripsInVehiclesDirection.get(tripsInVehiclesDirection.size()-1).getDeparture().getHour();
        int rowsNeeded = hourOfLastTrip - hourOfFirstTrip + 1;
        double rowHeight = availableHeight / (rowsNeeded - 1);

        // draw vertical line
        double visitedLength = rowHeight * (simTime.getHour() - hourOfFirstTrip);
        if (visitedLength < 0) visitedLength = 0;
        javafx.scene.shape.Line lineVisited = new javafx.scene.shape.Line(lineX, lineYstart, lineX, lineYstart + visitedLength);
        javafx.scene.shape.Line lineFuture = new javafx.scene.shape.Line(lineX, lineYstart + visitedLength, lineX, lineYend);
        lineVisited.setStrokeWidth(lineWidth);
        lineFuture.setStrokeWidth(lineWidth);
        lineVisited.setStroke(colorOfVisited);
        lineFuture.setStroke(colorOfFuture);
        lineScheduleElements.add(lineVisited);
        lineScheduleElements.add(lineFuture);

        // draw points and hours
        for (int i = 0; i < rowsNeeded; i++) {
            // draw points
            double rowY = lineYstart + i * rowHeight;
            Circle hourPoint = new Circle(lineX, rowY, lineWidth + 1);
            Color pointColor = (i <= (simTime.getHour() - hourOfFirstTrip)) ? colorOfVisited : colorOfFuture;
            hourPoint.setFill(pointColor);
            lineScheduleElements.add(hourPoint);

            // draw hours
            Text hourLabel = new Text(String.format("%02d", hourOfFirstTrip + i));
            hourLabel.setStyle("-fx-font-weight: bold");
//            hourLabel.setFill(pointColor);
            hourLabel.setLayoutX(lineX + hourOffsetX);
            hourLabel.setLayoutY(rowY + hourOffsetY);
            lineScheduleElements.add(hourLabel);
        }

        int rowIndex = -1;
        int depThisHour = 0;
        // draw minutes (single departures) in each row (hour)
        for (Trip trip : tripsInVehiclesDirection) {
            if (rowIndex != trip.getDeparture().getHour() - hourOfFirstTrip) {
                rowIndex = trip.getDeparture().getHour() - hourOfFirstTrip;
                depThisHour = 0;
            }
            double rowY = lineYstart + (rowHeight * rowIndex);

            Text minuteLabel = new Text(String.format("%02d", trip.getDeparture().getMinute()));
            minuteLabel.setStyle("-fx-font-size: 9");
            minuteLabel.setLayoutX(lineX + minuteOffsetX + (depThisHour * minuteXSpan));
            minuteLabel.setLayoutY(rowY + minuteOffsetY);
            lineScheduleElements.add(minuteLabel);
            depThisHour++;
        }

        lineSchedule.getChildren().addAll(lineScheduleElements);
    }

    /**
     * Highlights all streets belonging to given line with different color
     * @param line the line
     */
    private void highlightLineStreets(Line line) {
        for (Entry<Street, Polyline> pair : streets.entrySet()) {
            if (line.goesViaStreet(pair.getKey())) {
                pair.getValue().setStroke(getLineDefaultColor(line.getId()));
            }
        }
    }

    /**
     * Removes highlighting of all streets belonging to given line with different color
     * @param line the line
     */
    private void unhighlightLineStreets(Line line) {
        for (Entry<Street, Polyline> pair : streets.entrySet()) {
            if (line.goesViaStreet(pair.getKey())) {
                pair.getValue().setStroke(COLOR_STREET_DEFAULT);
                if (pair.getKey().equals(selectedStreet)) {
                    unselectStreet();
                }
            }
        }
    }

    static List<String> lineIdsWithAssignedColor = new ArrayList<>();
    static Color[] vehicleColorPool = new Color[]{
            Color.LIME,
            Color.MAGENTA,
            Color.GOLD,
            Color.CYAN
    };

    /**
     * Return different color for different lines, but same color for same line each time
     * @param lineId the line id
     * @return Color
     */
    private Color getLineDefaultColor(String lineId) {
        if (!lineIdsWithAssignedColor.contains(lineId)) {
            lineIdsWithAssignedColor.add(lineId);
        }
        int index = lineIdsWithAssignedColor.indexOf(lineId) % vehicleColorPool.length;
        return vehicleColorPool[index];
    }

}