package com.blaupunkt.tsviewer.dewarp;

public class CameraRegion {

    public final int left;
    public final int top;
    public final int width;
    public final int height;

    public CameraRegion(
            int left,
            int top,
            int width,
            int height) {

        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
    }
}
