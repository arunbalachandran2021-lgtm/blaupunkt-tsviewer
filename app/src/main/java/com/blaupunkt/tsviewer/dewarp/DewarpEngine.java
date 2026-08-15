package com.blaupunkt.tsviewer.dewarp;

import android.graphics.Bitmap;

public class DewarpEngine {

    private final DewarpProcessor processor;

    public DewarpEngine() {
        DewarpConfig config =
                new DewarpConfig(
                        1920,
                        1080,
                        2,
                        2,
                        0.5f,
                        0.5f,
                        0.5f
                );

        processor = new DewarpProcessor(config);
    }

    public int getCameraCount() {
        return processor.getCameraCount();
    }

    public CameraRegion getCameraRegion(int cameraIndex) {
        return processor.getCameraRegion(cameraIndex);
    }

    public float[] mapPoint(
            float normalizedX,
            float normalizedY) {

        return processor.mapPoint(
                normalizedX,
                normalizedY
        );
    }

    public boolean isCalibrated() {
        return true;
    }

    public Bitmap process(Bitmap source) {
        /*
         * The Android bitmap transform will be connected
         * after the camera calibration is tuned.
         */
        return source;
    }
}
