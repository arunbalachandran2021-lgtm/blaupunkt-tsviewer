package com.blaupunkt.tsviewer.view;

import android.graphics.Bitmap;

public class DewarpEngine {

    private boolean calibrated = false;

    public void setCalibrationAvailable(boolean available) {
        calibrated = available;
    }

    public boolean isCalibrated() {
        return calibrated;
    }

    /*
     * Placeholder for the real fisheye-to-perspective
     * transformation.

     * This must NOT guess the camera geometry.
     * The real mapping will be added after analyzing
     * a genuine Blaupunkt TS sample.
     */
    public Bitmap process(Bitmap source) {

        if (source == null) {
            return null;
        }

        if (!calibrated) {
            return source;
        }

        // Real GPU/OpenGL dewarping will be implemented here.
        return source;
    }
}
