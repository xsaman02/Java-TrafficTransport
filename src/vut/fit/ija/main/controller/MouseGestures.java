package vut.fit.ija.main.controller;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;

/**
 * Event handler for mouse gestrues
 * @author xsaman02
 * @version 1.0
 * @since 1.0
 * created: 04-04-2020, xsaman02
 */
public class MouseGestures {

    Controller c;

    public MouseGestures(Controller c) {
        this.c = c;
    }

    //Sets given node (Polyline, Rectangle, Circle) to be possible to click
    public void makeClickable(Node node) {
        node.setOnMouseClicked(onClickEventHandler);
    }

    /**
     * Private class that handles click events
     */
    EventHandler<MouseEvent> onClickEventHandler = new EventHandler<MouseEvent>() {

        /**
         * Event Listener and handler
         * It's called everytime user clicks on Polyline, Rectangle or Circle or map under
         * When clicking on street/stop/vehicle, two events are raised, because of underlaying map pane.
         * We need to throw away map pane clicks if street/stop/vehicle was clicked.
         */
        @Override
        public void handle(MouseEvent t) {

            /*
             * is street
             */
            if (t.getSource() instanceof Polyline) {
                Polyline clicked = (Polyline) t.getSource();
                if (c.selectedStreet != null && c.streets.get(c.selectedStreet).equals(clicked)) {
                    // user clicked on already selected street, so let no street be selected now
                    c.unselectStreet();
                } else {
                    c.selectStreet(clicked);
                }

                /*
                 * is stop
                 */
            } else if (t.getSource() instanceof Rectangle) {
                Rectangle clicked = (Rectangle) t.getSource();
                if (c.selectedStop != null && c.stops.get(c.selectedStop).equals(clicked)) {
                    // user clicked on already selected stop, so let no stop be selected now
                    c.unselectStop();
                } else {
                    c.selectStop(clicked);
                }

                /*
                 * is vehicle
                 */
            } else if (t.getSource() instanceof Circle) {
                Circle clicked = (Circle) t.getSource();
                if (c.selectedVehicle != null  && c.selectedVehicle.getGuiRep().equals(clicked)) {
                    // user clicked on already selected vehicle, so let no vehicle be selected now
                    c.unselectVehicle();
                } else {
                    c.selectVehicle(clicked);
                }

                /*
                 * clicked within map pane, but could be click on sub element, need to find out
                 */
            } else {
                // returns first intersected node, needs to be map (other options are itinerary, lineSchedule, null)
                String clickedNode = t.getPickResult().getIntersectedNode().getId();
                if (clickedNode != null && clickedNode.equals("map")) {
                    // yaay map pane was clicked
                    c.unselectVehicle();  // unselect vehicle will also hide itinerary and line schedule
                }
            }
        }
    };

}
