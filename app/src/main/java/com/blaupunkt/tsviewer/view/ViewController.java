package com.blaupunkt.tsviewer.view;

public class ViewController {

    private ViewMode currentMode = ViewMode.ORIGINAL;

    private float zoom = 1.0f;
    private float rotationX = 0.0f;
    private float rotationY = 0.0f;

    public ViewMode getCurrentMode() {
        return currentMode;
    }

    public void setMode(ViewMode mode) {
        if (mode != null) {
            currentMode = mode;
        }
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float value) {
        zoom = Math.max(1.0f, Math.min(value, 8.0f));
    }

    public void resetView() {
        zoom = 1.0f;
        rotationX = 0.0f;
        rotationY = 0.0f;
    }

    public float getRotationX() {
        return rotationX;
    }

    public float getRotationY() {
        return rotationY;
    }

    public void rotate(float deltaX, float deltaY) {
        rotationX += deltaX;
        rotationY += deltaY;
    }

    /*
     * Dewarping implementation will be connected here after
     * the real Blaupunkt TS recording is analyzed.
     */
    public boolean isDewarpAvailable() {
        return false;
    }
}
