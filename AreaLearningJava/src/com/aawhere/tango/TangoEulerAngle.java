package com.aawhere.tango;

import com.aawhere.lang.Assertion;
import com.aawhere.lang.ObjectBuilder;
import com.google.atap.tangoservice.TangoPoseData;

import static java.lang.Math.*;

import javax.annotation.Nonnull;

/**
 * Converts the TangoPoseData quaternion rotations into Euler Angles.
 * <p/>
 * http://www.euclideanspace.com/maths/geometry/rotations/conversions/quaternionToEuler/
 * <p/>
 * http://www.euclideanspace.com/maths/geometry/rotations/euler/index.htm
 * <p/>
 * Created by aroller on 7/3/15.
 */
public class TangoEulerAngle {

    @Nonnull
    private Double heading;
    @Nonnull
    private Double attitude;
    @Nonnull
    private Double bank;


    /**
     * Used to construct all instances of TangoEulerAngle.
     */
    public static class TangoEulerAngleBuilder extends ObjectBuilder<TangoEulerAngle> {


        double x;
        double y;
        double z;
        double w;


        private TangoEulerAngleBuilder() {
            super(new TangoEulerAngle());
        }


        public TangoEulerAngleBuilder poseData(TangoPoseData poseData) {
            final double[] rotation = poseData.rotation;
            x(rotation[TangoPoseData.INDEX_ROTATION_X]);
            y(rotation[TangoPoseData.INDEX_ROTATION_Y]);
            z(rotation[TangoPoseData.INDEX_ROTATION_Z]);
            w(rotation[TangoPoseData.INDEX_ROTATION_W]);
            return this;
        }

        public TangoEulerAngleBuilder x(double x) {
            this.x = x;
            return this;
        }

        public TangoEulerAngleBuilder y(double y) {
            this.y = y;
            return this;
        }

        public TangoEulerAngleBuilder z(double z) {
            this.z = z;
            return this;
        }

        public TangoEulerAngleBuilder w(double w) {
            this.w = w;
            return this;
        }

        @Override
        protected void validate() {
            super.validate();
            Assertion.assertNotNull("x", this.x);
        }

        public TangoEulerAngle build() {
            TangoEulerAngle built = super.build();

            //this is copied from the non-normalized code found on the website
            double sqw = w * w;
            double sqx = x * x;
            double sqy = y * y;
            double sqz = z * z;
            double unit = sqx + sqy + sqz + sqw; // if normalised is one, otherwise is correction factor
            double test = x * y + z * w;
            if (test > 0.499 * unit) { // singularity at north pole
                built.heading = 2 * atan2(x, w);
                built.attitude = Math.PI / 2;
                built.bank = 0.0;
            } else if (test < -0.499 * unit) { // singularity at south pole
                built.heading = -2 * atan2(x, w);
                built.attitude = -Math.PI / 2;
                built.bank = 0.0;
            } else {
                built.heading = atan2(2 * y * w - 2 * x * z, sqx - sqy - sqz + sqw);
                built.attitude = asin(2 * test / unit);
                built.bank = atan2(2 * x * w - 2 * y * z, -sqx + sqy - sqz + sqw);

            }
            return built;
        }

    }//end Builder

    public static TangoEulerAngleBuilder builder() {
        return new TangoEulerAngleBuilder();
    }

    /**
     * Use {@link TangoEulerAngleBuilder} to construct TangoEulerAngle
     */
    private TangoEulerAngle() {
    }


    @Nonnull
    public Double heading() {
        return heading;
    }

    @Nonnull
    public Double attitude() {
        return attitude;
    }

    @Nonnull
    public Double bank() {
        return bank;
    }
}
