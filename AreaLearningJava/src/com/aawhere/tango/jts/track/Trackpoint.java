package com.aawhere.tango.jts.track;


import com.aawhere.lang.ObjectBuilder;
import com.vividsolutions.jts.geom.Coordinate;

/**
 * A moment in time with observations made by the tango motion tracking capabilities.  This
 * trackpoint goes beyond just position including heading, speed or any other measurement that may
 * describe the motion of the device.  Additionally  other sensors that provide an instantaneous
 * value (temperature, sound level, light, etc) may be included. Created by aroller on 7/3/15.
 * <p/>
 * It is a programming decision to use concrete types as fields, rather than a more flexible format
 * using a Hash with keys to values.  For this reason all fields may be null except for timestamp
 * which is required.
 */
public class Trackpoint {

    /**
     * @see #coordinate()
     */
    private Coordinate coordinate;

    /**
     * Used to construct all instances of Trackpoint.
     */
    public static class TrackpointBuilder extends ObjectBuilder<Trackpoint> {

        private TrackpointBuilder() {
            super(new Trackpoint());
        }

        /**
         * @see #coordinate()
         */
        public TrackpointBuilder coordinate(Coordinate coordinate) {
            building.coordinate = coordinate;
            return this;
        }



        public Trackpoint build() {
            Trackpoint built = super.build();
            return built;
        }

    }//end Builder

    public static TrackpointBuilder builder() {
        return new TrackpointBuilder();
    }

    /**
     * Use {@link TrackpointBuilder} to construct Trackpoint
     */
    private Trackpoint() {
    }


    /**
     * @return the x,y,z of the device compared to the localized ADF
     */
    public Coordinate coordinate() {
        return this.coordinate;
    }


}
