package de.jbamberger.ledirremote.views;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

class PathDrawable extends Drawable implements ValueAnimator.AnimatorUpdateListener {
    private Path mPath;
    private Paint mPaint;
    private ValueAnimator mAnimator;

    public PathDrawable() {
        mPath = new Path();
        mPaint = new Paint();
        mPaint.setColor(0xffff0000);
        mPaint.setStrokeWidth(5);
        mPaint.setStyle(Paint.Style.FILL);
    }

    public void startAnimating() {
        Rect b = getBounds();
        mAnimator = ValueAnimator.ofInt(0, Math.abs(b.centerY() - b.top), 0, Math.abs(b.centerX() - b.left), 0);
        mAnimator.setDuration(3000);
        mAnimator.addUpdateListener(this);
        mAnimator.start();
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    public void setAlpha(int alpha) {
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animator) {
        mPath.reset();
        Rect b = getBounds();
        //mPath.moveTo(b.left, b.bottom);
        mPath.addCircle(b.exactCenterX(), b.exactCenterY(), (Integer) animator.getAnimatedValue(), Path.Direction.CW);
        invalidateSelf();
    }
}