package com.blaupunkt.tsviewer.dewarp;

public class FisheyeMapper {

    private final float strength;

    public FisheyeMapper(float strength) {
        this.strength = strength;
    }

    public float[] map(
            float normalizedX,
            float normalizedY) {

        float x = normalizedX * 2.0f - 1.0f;
        float y = normalizedY * 2.0f - 1.0f;

        float radius = (float) Math.sqrt(x * x + y * y);

        if (radius < 0.0001f) {
            return new float[]{0.5f, 0.5f};
        }

        float correctedRadius =
                (float) Math.pow(
                        radius,
                        1.0f + strength
                );

        float scale =
                correctedRadius / radius;

        float correctedX = x * scale;
        float correctedY = y * scale;

        return new float[]{
                correctedX * 0.5f + 0.5f,
                correctedY * 0.5f + 0.5f
        };
    }
}
