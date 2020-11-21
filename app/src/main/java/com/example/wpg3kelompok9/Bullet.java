package com.example.wpg3kelompok9;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class Bullet {

    private Boolean isActive = false;
    private int frameWidth = 50;
    private int frameHeight = 20;
    private int frameCount = 1;
    private Paint paint;
    private float bulletXPosition;
    private float bulletYPosition;
    private static final double SPEED_PIXEL_PER_SECOND = 1400;
    private static final double MAX_SPEED = SPEED_PIXEL_PER_SECOND / GameLoop.MAX_UPS;
    private int screenSizeX;
    private int screenSizeY;
    private Bitmap bulletBitmap;
    private RectF whereToDraw = new RectF(
            bulletXPosition + 0,
            bulletYPosition + 0,
            bulletXPosition + frameWidth,
            bulletYPosition + frameHeight);
    private int currentFrame = 0;
    private Rect frameToDraw = new Rect(
            0,
            0,
            frameWidth,
            frameHeight);

    public Bullet(int frameX, int frameY, Bitmap bitmapSource){
        // Change frame size
        frameWidth = frameX;
        frameHeight = frameY;

        // Take the bullet Bitmap
        bulletBitmap = bitmapSource;
        bulletBitmap = Bitmap.createScaledBitmap(bulletBitmap, frameWidth * frameCount, frameHeight,false);
    }

    public boolean getActive(){
        return isActive;
    }

    public void draw(Canvas canvas) {
        // Draw bullet
        canvas.drawBitmap(bulletBitmap, frameToDraw, whereToDraw, paint);
    }

    public void update() {
        bulletXPosition += MAX_SPEED;

        whereToDraw.set(
            bulletXPosition + 0,
            bulletYPosition + 0,
            bulletXPosition + frameWidth,
         bulletYPosition + frameHeight);

        // Delete bullet if bullet out of screen
        if((bulletXPosition + 2) > screenSizeX){
            isActive = false;
        }
    }

    // Self made constructor
    // Called if this object needed
    public void instantiateBullet(RectF position, int screenX, int screenY) {
        // Bullet spawn point
        bulletXPosition = position.left;
        bulletYPosition = position.top;
        // Implement bullet position
        whereToDraw.set(
                bulletXPosition + 0,
                bulletYPosition + 0,
                bulletXPosition + frameWidth,
                bulletYPosition + frameHeight);

        // Set bullet to active
        isActive = true;

        // Save screen size
        screenSizeX = screenX;
        screenSizeY = screenY;

        // Just some paint
        paint = new Paint();
        paint.setColor(Color.argb(255, 255, 255, 255));
    }

    public void setActive(boolean a){
        isActive = false;
    }

    public RectF getRectF(){
        return whereToDraw;
    }
}
