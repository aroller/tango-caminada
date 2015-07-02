package com.aawhere.tango.jts;

import com.google.atap.tangoservice.TangoPoseData;
import com.vividsolutions.jts.geom.Coordinate;

/**
 * Common functions to translate Tango data structures to JTS.
 * <p/>
 * Created by aroller on 7/2/15.
 */
public class TangoJtsUtil {

    /**
     *
     * @param translation
     * @return
     */
    public static Coordinate coordinate(TangoPoseData tangoPoseData) {
        float[] translation = tangoPoseData.getTranslationAsFloats();
        return new Coordinate(translation[TangoPoseData.INDEX_TRANSLATION_X],
                translation[TangoPoseData.INDEX_TRANSLATION_Y],
                translation[TangoPoseData.INDEX_TRANSLATION_Z]);
    }
}
