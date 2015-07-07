package com.aawhere.jts.map.place;

import com.aawhere.lang.ObjectBuilder;
import com.aawhere.tango.TangoEulerAngle;
import com.vividsolutions.jts.algorithm.Angle;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.operation.distance.DistanceOp;

/**
 * Provides instructions that will guide the person or machine that is able to move the tango
 * towards a single Place.
 * <p/>
 * <p/>
 * <p/>
 * Created by aroller on 7/5/15.
 */
public class PlaceNavigator {

    public enum Instruction {
        /**
         * Indicates to go Forward straight ahead.
         */
        FORWARD,
        /**
         * Twist in the right direction without moving forward or reverse
         */
        RIGHT,
        /**
         * Twist in the left direction without moving forward or reverse
         */
        LEFT,
        /**
         * the opposite of Foward this goes straight back.
         */
        REVERSE,
        /**
         * Stop moving.
         */
        STOP,

        /**
         * The destination is reached.
         */
        ARRIVED
    }

    /**
     * @see #destination()
     */
    private Place destination;

    /**
     * The angle to the destination considered to be straight since hitting zero is unlikely
     */
    private Double radiansToConsiderStraight;

    /**
     * The amount of distance, in coordinate relative distance units, to be considered arrived at
     * the destination.
     */
    private Double distanceToConsiderArrived;

    /**
     * The distance away from the destination to be considered arrived if the current position falls
     * within.
     */
    private Integer millimetersToConsiderArrived = 1000;

    /**
     * Used to construct all instances of PlaceNavigator.
     */
    public static class PlaceNavigatorBuilder extends ObjectBuilder<PlaceNavigator> {

        private PlaceNavigatorBuilder() {
            super(new PlaceNavigator());
        }


        /**
         * @see #destination()
         */
        public PlaceNavigatorBuilder destination(Place place) {
            building.destination = place;
            return this;
        }


        public PlaceNavigator build() {
            PlaceNavigator built = super.build();
            if (built.radiansToConsiderStraight == null) {
                //within 5% of a circle
                built.radiansToConsiderStraight = Math.PI / 10;
            }
            if (built.distanceToConsiderArrived == null) {
                //FIXME: this number should be related to #millimetersToConsiderArrived
                built.distanceToConsiderArrived = 0.1;
            }
            return built;
        }

    }//end Builder

    public static PlaceNavigatorBuilder builder() {
        return new PlaceNavigatorBuilder();
    }

    /**
     * Use {@link PlaceNavigatorBuilder} to construct PlaceNavigator
     */
    private PlaceNavigator() {
    }

    /**
     * @return the target we are navigating to
     */
    public Place destination() {
        return this.destination;
    }

    /**
     * Given the current position and movement of the subject this will provide instructions of what
     * must be done to navigate towards the #destination.
     *
     * @param headingInRadians
     * @return
     */
    public NavigationInstructions instruction(Coordinate location, double headingInRadians) {

        //angle computes relative to X axis, heading is on the positive Y axis so subtract a quarter
        double radiansToDestination = Angle.angle(
                location, this.destination.point().getCoordinate()) - Math.PI / 2;
        final double turnInRadians = Angle.normalize(radiansToDestination - headingInRadians);
        final double distance = location.distance(destination.point().getCoordinate());

        Instruction instruction;
        if (Math.abs(turnInRadians) < this.radiansToConsiderStraight) {
            if (distance < this.distanceToConsiderArrived) {
                instruction = Instruction.ARRIVED;
            } else {
                instruction = Instruction.FORWARD;
            }
        } else if (turnInRadians > 0) {
            instruction = Instruction.LEFT;
        } else {
            instruction = Instruction.RIGHT;
        }
        return NavigationInstructions.builder().instruction(instruction).bearing(
                turnInRadians).distance(distance).build();
    }


}
