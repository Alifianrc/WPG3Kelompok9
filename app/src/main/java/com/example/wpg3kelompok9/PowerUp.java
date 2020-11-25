package com.example.wpg3kelompok9;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class PowerUp {
    private Bitmap bitmapPowerUp;
    private int frameWidth   = 50;
    private int frameHeight  = 50;
    private int frameLengthInMilliseconds = 100;
    private long lastFrameChangeTime = 0;
    private int frameLoop    = 0;
    private int frameCount   = 2;
    private Rect frameToDraw = new Rect(
            frameLoop + 0,
            0,
            frameWidth + frameLoop,
            frameHeight + 0
    );

    // Enemy position
    private float powerUpXPosition;
    private float powerUpYPosition;
    // Left, Top, Right, Bottom
    RectF whereToDraw = new RectF(
            powerUpXPosition + 0,
            powerUpYPosition + 0,
            powerUpXPosition + frameWidth,
            powerUpYPosition + frameHeight);
    private boolean isActive = false;
    private static final double SPEED_PIXEL_PER_SECOND = 400;
    private static final double MAX_SPEED = SPEED_PIXEL_PER_SECOND / GameLoop.MAX_UPS * -1;

    // Just some Paint
    Paint paint;

    // Save screen size
    private int screenSizeX;
    private int screenSizeY;

    public PowerUp(int screenX, int screenY, int frameX, int frameY, Bitmap bitmapSource){
        screenSizeX = screenX;
        screenSizeY = screenY;

        frameWidth = frameX;
        frameHeight = frameY;

        bitmapPowerUp = bitmapSource;
        bitmapPowerUp = Bitmap.createScaledBitmap(bitmapPowerUp, frameWidth*frameCount, frameHeight,false);

        paint = new Paint();
        paint.setColor(Color.argb(255, 255, 255, 255));
    }

    public void draw(Canvas canvas) {
        // Draw PowerUp
        canvas.drawBitmap(bitmapPowerUp, frameToDraw, whereToDraw, paint);
    }

    public void update() {
        // Delete UFO if out of screen
        if(powerUpXPosition + frameWidth <= -2){
            isActive = false;
        }

        // Get UFO speed
        getSpeed();

        // Set where to draw UFO on screen
        whereToDraw.set(
                powerUpXPosition + 0,
                powerUpYPosition + 0,
                powerUpXPosition + frameWidth,
                powerUpYPosition + frameHeight
        );

        // Get UFO sprite
        getCurrentFrame();
    }

    public void getSpeed(){
        powerUpXPosition += MAX_SPEED;
    }

    public void getCurrentFrame(){
        long time = System.currentTimeMillis();
        if ( time > lastFrameChangeTime + frameLengthInMilliseconds) {
            lastFrameChangeTime = time;
            frameLoop++;
            if (frameLoop >= frameCount) {
                frameLoop = 0;
            }
        }

        //update the left and right values of the source of
        //the next frame on the spritesheet
        frameToDraw.left = frameLoop * frameWidth;
        frameToDraw.right = frameToDraw.left + frameWidth;
    }

    public void instantiatePowerUp(int XPosition, int YPosition) {
        isActive = true;

        powerUpXPosition = XPosition;
        powerUpYPosition = YPosition;

        whereToDraw.set(
                powerUpXPosition + 0,
                powerUpYPosition + 0,
                powerUpXPosition + frameWidth,
                powerUpYPosition + frameHeight
        );
    }

    public RectF getRectF(){
        return whereToDraw;
    }

    public void getHit(){
        isActive = false;
    }

    public boolean getActive(){
        return isActive;
    }
}
