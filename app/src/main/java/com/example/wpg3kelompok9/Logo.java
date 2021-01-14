package com.example.wpg3kelompok9;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class Logo {
    private Bitmap logoBitmap;
    private Rect partToDraw;
    private RectF whereToDraw;

    private int Width;
    private int Height;

    private Paint paint;

    public Logo(int width, int height, Bitmap theBitmap){

        Width = width;
        Height = height;

        logoBitmap = theBitmap;
        logoBitmap = Bitmap.createScaledBitmap(logoBitmap, width, height,false);

        partToDraw = new Rect(
                0,
                0,
                0 + width,
                0 + height
        );

        whereToDraw = new RectF();

        paint = new Paint();
    }

    public void draw(Canvas canvas, int x, int y){

        whereToDraw.set(
                x + 0,
                y + 0,
                x + Width,
                0 + Height
        );

        canvas.drawBitmap(logoBitmap, partToDraw, whereToDraw, paint);
    }
}
