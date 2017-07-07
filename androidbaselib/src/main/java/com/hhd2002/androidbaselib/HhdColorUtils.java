package com.hhd2002.androidbaselib;

public class HhdColorUtils {

    public static int colorWithAlpha(int color, float alpha) {
        final int currentAlpha = (color >> 24) & 0xFF;
        final int newAlpha = (int) (currentAlpha * alpha);
        return color & (0xFFFFFF | newAlpha << 24);
    }

    private HhdColorUtils() {
    }
}
