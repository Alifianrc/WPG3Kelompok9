package com.example.wpg3kelompok9;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Button {

    private int restartCirclePositionX;
    private int restartCirclePositionY;
    private int restartCircleRadius;
    private double restartCenterToTouchDistance;

    Paint paint;
    
    public Button(int reCircleX, int reCircleY, int reRadius, int r, int g, int b){
        restartCirclePositionX = reCircleX;
        restartCirclePositionY = reCircleY;
        restartCircleRadius = reRadius;

        paint = new Paint();
        paint.setColor(Color.argb(255,  r + 0, g + 0, b + 0));
    }

    public boolean isPressed(double touchPositionX, double touchPositionY) {
        // Fucking Calculate value with Pythagorian theorem
        restartCenterToTouchDistance = Math.sqrt(
                Math.pow(restartCirclePositionX - touchPositionX, 2) +
                Math.pow(restartCirclePositionY - touchPositionY, 2)
        );
        return restartCenterToTouchDistance < restartCircleRadius;
    }

    public void drawRestartButton(Canvas canvas){
        canvas.drawCircle(
                restartCirclePositionX,
                restartCirclePositionY,
                restartCircleRadius,
                paint
        );
    }
}
