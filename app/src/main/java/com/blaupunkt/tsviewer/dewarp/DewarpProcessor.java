package com.blaupunkt.tsviewer.dewarp;

public class DewarpProcessor {

    private final DewarpConfig config;
    private final FourCameraLayout layout;
    private final FisheyeMapper mapper;

    public DewarpProcessor(DewarpConfig config) {
        this.config = config;
        this.layout = new FourCameraLayout(config);

        // Initial generic strength.
        // This will be calibrated from the real recording.
        this.mapper = new FisheyeMapper(0.20f);
    }

    public CameraRegion getCameraRegion(int cameraIndex) {
        return layout.getRegion(cameraIndex);
    }

    public float[] mapPoint(
            float normalizedX,
            float normalizedY) {

        return mapper.map(
                normalizedX,
                normalizedY
        );
    }

    public int getCameraCount() {
        return config.columns * config.rows;
    }
}
