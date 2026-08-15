package com.blaupunkt.tsviewer.dewarp;

import android.graphics.Bitmap;
import android.graphics.Color;

public class BitmapDewarpProcessor {

    private final float strength;

    public BitmapDewarpProcessor(float strength) {
        this.strength = strength;
    }

    public Bitmap process(Bitmap source) {

        if (source == null) {
            return null;
        }

        int width = source.getWidth();
        int height = source.getHeight();

        Bitmap output = Bitmap.createBitmap(
                width,
                height,
                Bitmap.Config.ARGB_8888
        );

        int[] sourcePixels =
                new int[width * height];

        int[] outputPixels =
                new int[width * height];

        source.getPixels(
                sourcePixels,
                0,
                width,
                0,
                0,
                width,
                height
        );

        for (int y = 0; y < height; y++) {

            float ny =
                    (y + 0.5f) / height;

            float fy =
                    ny * 2.0f - 1.0f;

            for (int x = 0; x < width; x++) {

                float nx =
                        (x + 0.5f) / width;

                float fx =
                        nx * 2.0f - 1.0f;

                float radius =
                        (float) Math.sqrt(
                                fx * fx + fy * fy
                        );

                float sourceX = fx;
                float sourceY = fy;

                if (radius > 0.0001f) {

                    float correctedRadius =
                            (float) Math.pow(
                                    radius,
                                    1.0f + strength
                            );

                    float scale =
                            correctedRadius / radius;

                    sourceX = fx * scale;
                    sourceY = fy * scale;
                }

                float sx =
                        sourceX * 0.5f + 0.5f;

                float sy =
                        sourceY * 0.5f + 0.5f;

                int pixel;

                if (sx < 0.0f ||
                        sx > 1.0f ||
                        sy < 0.0f ||
                        sy > 1.0f) {

                    pixel = Color.TRANSPARENT;

                } else {

                    int ix =
                            Math.min(
                                    width - 1,
                                    Math.max(
                                            0,
                                            (int) (sx * width)
                                    )
                            );

                    int iy =
                            Math.min(
                                    height - 1,
                                    Math.max(
                                            0,
                                            (int) (sy * height)
                                    )
                            );

                    pixel =
                            sourcePixels[
                                    iy * width + ix
                            ];
                }

                outputPixels[
                        y * width + x
                ] = pixel;
            }
        }

        output.setPixels(
                outputPixels,
                0,
                width,
                0,
                0,
                width,
                height
        );

        return output;
    }
}
