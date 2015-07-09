package com.aawhere.tango;

import com.aawhere.lang.ObjectBuilder;
import com.google.atap.tangoservice.TangoXyzIjData;

import java.nio.FloatBuffer;

/**
 * Created by aroller on 7/8/15.
 */
public class TangoDepthCalculator {


    private Double distanceInMeters;
    private Integer numberOfPoints;


    /**
     * Used to construct all instances of TangoDepthCalculator.
     */
    public static class TangoDepthCalculatorBuilder extends ObjectBuilder<TangoDepthCalculator> {

        private Double centerCoordinateMax = 0.020;
        private TangoXyzIjData xyzIjData;

        private TangoDepthCalculatorBuilder() {
            super(new TangoDepthCalculator());
        }


        public TangoDepthCalculatorBuilder data(TangoXyzIjData data) {
            this.xyzIjData = data;
            return this;
        }

        public TangoDepthCalculator build() {
            TangoDepthCalculator built = super.build();
            final FloatBuffer xyz = xyzIjData.xyz;
            double cumulativeZ = 0.0;
            int numberOfPoints = 0;
            for (int i = 0; i < xyzIjData.xyzCount; i += 3) {
                float x = xyz.get(i);
                float y = xyz.get(i + 1);
                if (Math.abs(x) < centerCoordinateMax && Math.abs(y) < centerCoordinateMax) {
                    float z = xyz.get(i + 2);
                    cumulativeZ += z;
                    numberOfPoints++;
                }
            }

            Double distanceInMeters;
            if (numberOfPoints > 0) {
                distanceInMeters = cumulativeZ / numberOfPoints;
            } else {
                distanceInMeters = null;
            }
            built.distanceInMeters = distanceInMeters;
            built.numberOfPoints = numberOfPoints;
            return built;
        }

    }//end Builder

    public static TangoDepthCalculatorBuilder builder() {
        return new TangoDepthCalculatorBuilder();
    }

    /**
     * Use {@link TangoDepthCalculatorBuilder} to construct TangoDepthCalculator
     */
    private TangoDepthCalculator() {
    }

    public Double distanceInMeters() {
        return distanceInMeters;
    }

    public Integer numberOfPoints() {
        return numberOfPoints;
    }

    /**distance is null if points are zero.
     *
     * @return
     */
    public boolean hasDistance() {
        return distanceInMeters != null;
    }

}
