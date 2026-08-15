package com.blaupunkt.tsviewer.dewarp;

public class FourCameraLayout {

    private final DewarpConfig config;

    public FourCameraLayout(DewarpConfig config) {
        this.config = config;
    }

    public CameraRegion getRegion(int cameraIndex) {

        int cameraWidth = config.cameraWidth();
        int cameraHeight = config.cameraHeight();

        int column = cameraIndex % config.columns;
        int row = cameraIndex / config.columns;

        return new CameraRegion(
                column * cameraWidth,
                row * cameraHeight,
                cameraWidth,
                cameraHeight
        );
    }
}
