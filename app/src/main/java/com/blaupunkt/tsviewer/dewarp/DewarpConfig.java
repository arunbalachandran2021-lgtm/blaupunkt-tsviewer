package com.blaupunkt.tsviewer.dewarp;

public class DewarpConfig {

    public final int sourceWidth;
    public final int sourceHeight;

    public final int columns;
    public final int rows;

    public final float lensCenterX;
    public final float lensCenterY;
    public final float lensRadius;

    public DewarpConfig(
            int sourceWidth,
            int sourceHeight,
            int columns,
            int rows,
            float lensCenterX,
            float lensCenterY,
            float lensRadius) {

        this.sourceWidth = sourceWidth;
        this.sourceHeight = sourceHeight;
        this.columns = columns;
        this.rows = rows;

        this.lensCenterX = lensCenterX;
        this.lensCenterY = lensCenterY;
        this.lensRadius = lensRadius;
    }

    public int cameraWidth() {
        return sourceWidth / columns;
    }

    public int cameraHeight() {
        return sourceHeight / rows;
    }
}
