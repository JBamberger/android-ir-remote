package de.jbamberger.irremote.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

public class PathAnim extends View {

    int framesPerSecond = 60;
    long animationDuration = 10000; // 10 seconds

    Matrix matrix = new Matrix(); // transformation matrix

    Path path = new Path();       // your path
    Paint paint = new Paint();    // your paint


    long startTime;

    public PathAnim(Context context) {
        super(context);

        path.moveTo(0, 0);
        path.lineTo(1000, 1000);
        path.moveTo(0, 1000);
        path.lineTo(0, 1000);
        path.addRect(0,0,1000,1000, Path.Direction.CW);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GREEN);

        // start the animation:
        this.startTime = System.currentTimeMillis();
        this.postInvalidate();
        setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });
    }



    @Override
    protected void onDraw(Canvas canvas) {

        //long elapsedTime = System.currentTimeMillis() - startTime;

        //matrix.postRotate(30);        // rotate 30° every second
        //matrix.postTranslate(100 * elapsedTime/1000, 0); // move 100 pixels to the right
        // other transformations...


        //canvas.concat(matrix);        // call this before drawing on the canvas!!
        //canvas.drawRect(0,0,1000,1000,paint);

        canvas.drawPath(path, paint); // draw on canvas
        //if(elapsedTime < animationDuration)
            //this.postInvalidateDelayed( 1000 / framesPerSecond);
    }



}
