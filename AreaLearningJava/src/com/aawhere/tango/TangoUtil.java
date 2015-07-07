package com.aawhere.tango;

import com.aawhere.measure.QuaternionEulerAngles;
import com.google.atap.tangoservice.TangoPoseData;

/**
 * Created by aroller on 7/4/15.
 */
public class TangoUtil {


    /**
     * Converts the tango pose data into the QuaternionEulerAngles capable of solving heading only
     * (about the y axis or "yaw").
     *
     * @param tangoPoseData
     * @return
     */
    public static QuaternionEulerAngles headingAngles(TangoPoseData tangoPoseData) {
        return quaternionEulerAnglesBuilder(tangoPoseData).headingOnly().build();
    }

    public static QuaternionEulerAngles.QuaternionEulerAnglesBuilder quaternionEulerAnglesBuilder(TangoPoseData tangoPoseData) {
        return QuaternionEulerAngles.builder().w(
                tangoPoseData.rotation[TangoPoseData.INDEX_ROTATION_W]).x(
                tangoPoseData.rotation[TangoPoseData.INDEX_ROTATION_X]).y(
                tangoPoseData.rotation[TangoPoseData.INDEX_ROTATION_Y]).z(
                tangoPoseData.rotation[TangoPoseData.INDEX_ROTATION_Z]);
    }
}
