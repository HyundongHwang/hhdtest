package com.hhd2002.androidbaselib.UI;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ZeplinLoadingView extends Drawable {

    private static final long DURATION = 500;

    private int size;

    private ValueAnimator backgroundCircleAnimator;
    private ValueAnimator foregroundCircleAnimator;

    private final Paint backgroundCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint foregroundCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public ZeplinLoadingView() {
        backgroundCirclePaint.setColor(0x30999999);
        foregroundCirclePaint.setColor(0x30a0a0a0);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        size = Math.min(bounds.width(), bounds.height());

        float halfRadius = size / 4f;
        backgroundCircleAnimator = ValueAnimator.ofFloat(halfRadius, size / 2f);
        backgroundCircleAnimator.setDuration(DURATION);
        backgroundCircleAnimator.setRepeatMode(ValueAnimator.REVERSE);
        backgroundCircleAnimator.setRepeatCount(ValueAnimator.INFINITE);
        backgroundCircleAnimator.start();

        foregroundCircleAnimator = ValueAnimator.ofFloat(halfRadius, 0f);
        foregroundCircleAnimator.setDuration(DURATION);
        foregroundCircleAnimator.setRepeatMode(ValueAnimator.REVERSE);
        foregroundCircleAnimator.setRepeatCount(ValueAnimator.INFINITE);
        foregroundCircleAnimator.start();

        invalidateSelf();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        float center = size / 2f;
        canvas.drawCircle(center, center, (float) backgroundCircleAnimator.getAnimatedValue(), backgroundCirclePaint);
        canvas.drawCircle(center, center, (float) foregroundCircleAnimator.getAnimatedValue(), foregroundCirclePaint);
        invalidateSelf();
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSPARENT;
    }
}
