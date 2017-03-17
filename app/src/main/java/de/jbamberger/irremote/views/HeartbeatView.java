package de.jbamberger.irremote.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

public class HeartbeatView extends View {

    private static Paint paint;
    private int screenW, screenH;
    private float X, Y;
    private Path path;
    private float initialScreenW;
    private float initialX, plusX;
    private float TX;
    private boolean translate;
    private int flash;
    private Context context;


    public HeartbeatView(Context context) {
        super(context);

        this.context = context;

        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(10);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        //paint.setShadowLayer(7, 0, 0, Color.RED);


        path = new Path();
        TX = 0;
        translate = false;

        flash = 0;

    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        screenW = w;
        screenH = h;
        X = 0;
        Y = (screenH / 2) + (screenH / 4) + (screenH / 10);

        initialScreenW = screenW;
        initialX = ((screenW / 2) + (screenW / 4));
        plusX = (screenW / 24);

        path.moveTo(X, Y);

    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //canvas.save();


        flash += 1;
        if (flash < 10 || (flash > 20 && flash < 30)) {
            paint.setStrokeWidth(16);
            paint.setColor(Color.RED);
            //paint.setShadowLayer(12, 0, 0, Color.RED);
        } else {
            paint.setStrokeWidth(10);
            paint.setColor(Color.GREEN);
            //paint.setShadowLayer(7, 0, 0, Color.RED);
        }

        if (flash == 100) {
            flash = 0;
        }

        path.lineTo(X, Y);
        canvas.translate(-TX, 0);
        if (translate == true) {
            TX += 4;
        }

        if (X < initialX) {
            X += 8;
        } else {
            if (X < initialX + plusX) {
                X += 2;
                Y -= 8;
            } else {
                if (X < initialX + (plusX * 2)) {
                    X += 2;
                    Y += 14;
                } else {
                    if (X < initialX + (plusX * 3)) {
                        X += 2;
                        Y -= 12;
                    } else {
                        if (X < initialX + (plusX * 4)) {
                            X += 2;
                            Y += 6;
                        } else {
                            if (X < initialScreenW) {
                                X += 8;
                            } else {
                                translate = true;
                                initialX = initialX + initialScreenW;
                            }
                        }
                    }
                }
            }

        }

        canvas.drawPath(path, paint);


        //canvas.restore();

        invalidate();
    }
}