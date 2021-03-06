package com.example.wpg3kelompok9;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.Random;

public class Enemy {
    // ENemy image adn animation
    private Bitmap bitmapUFO;
    private int frameWidth   = 0;
    private int frameHeight  = 0;
    private int frameLengthInMilliseconds = 100;
    private long lastFrameChangeTime = 0;
    private int frameLoop    = 0;
    private int frameCount   = 4;
    private Rect frameToDraw;

    // Enemy position and movement
    private float ufoXPosition;
    private float ufoYPosition;
    // Left, Top, Right, Bottom
    RectF whereToDraw;
    private boolean isActive = false;
    private static final double SPEED_PIXEL_PER_SECOND = 600;
    private static final double MAX_SPEED = SPEED_PIXEL_PER_SECOND / GameLoop.MAX_UPS * -1;
    private int standByTime = 2000;
    private long lastStandByTime = 0;
    private boolean isStop = false;
    private boolean isStopped = false;
    private boolean isGoingUp = false;

    // Just some Paint
    Paint paint;

    // Save screen size
    private int screenSizeX;
    private int screenSizeY;

    // Live point
    private int live = 3;

    // Random movement
    private int randomMovement = 0;

    // Constructor
    public Enemy(int screenX, int screenY, int frameX, int frameY, Bitmap bitmapSource){
        screenSizeX = screenX;
        screenSizeY = screenY;

        frameWidth = frameX;
        frameHeight = frameY;

        bitmapUFO = bitmapSource;
        bitmapUFO = Bitmap.createScaledBitmap(bitmapUFO, frameWidth*frameCount, frameHeight,false);

        paint = new Paint();
        paint.setColor(Color.argb(255, 255, 255, 255));

        whereToDraw = new RectF(
                ufoXPosition + 0,
                ufoYPosition + 0,
                ufoXPosition + frameWidth,
                ufoYPosition + frameHeight
        );

        frameToDraw = new Rect(
                frameLoop + 0,
                0,
                frameWidth + frameLoop,
                frameHeight + 0
        );
    }

    public void draw(Canvas canvas) {
        // Draw ufo
        canvas.drawBitmap(bitmapUFO, frameToDraw, whereToDraw, paint);

    }

    public void update() {
        // Delete UFO if out of screen
        if(ufoXPosition + frameWidth <= -2){
            isActive = false;
        }

        // Get UFO speed
        getSpeed();

        // Set where to draw UFO on screen
        whereToDraw.set(
                ufoXPosition + 0,
                ufoYPosition + 0,
                ufoXPosition + frameWidth,
                ufoYPosition + frameHeight
        );

        // Get UFO sprite
        getCurrentFrame();


    }

    // Contain UFO Logic Movement
    public void getSpeed(){
        // Enemy will Ramdomize movement pattern
        if(randomMovement == 0){
            // Add movement pattern here @Aas
            if(ufoXPosition <= screenSizeX * 4/5)
            {
                if(ufoYPosition <= screenSizeY * 1/5) {
                    isGoingUp = false;
                }
                else if (ufoYPosition >= screenSizeY * 4/5){
                    isGoingUp = true;
                }

                if(!isGoingUp){
                    ufoYPosition -= MAX_SPEED/3;
                }
                else {
                    ufoYPosition += MAX_SPEED/3;
                }

                ufoXPosition += MAX_SPEED/4;
            }
            else{
                ufoXPosition += MAX_SPEED;
            }
        }
        else{
            // Stop at 4/5 screen position
            if(ufoXPosition <= screenSizeX * 4/5 && !isStopped){
                isStop = true;
                isStopped = true;
                lastStandByTime = System.currentTimeMillis();
            }
            // Then start walking again
            if(isStop){
                long time = System.currentTimeMillis();
                if(time > lastStandByTime + standByTime){
                    isStop = false;
                    ufoXPosition += MAX_SPEED;
                }
            }
            // Just walking
            else if (!isStop){
                ufoXPosition += MAX_SPEED;
            }
        }


    }

    public void getCurrentFrame(){
        long time = System.currentTimeMillis();
        if ( time > lastFrameChangeTime + frameLengthInMilliseconds) {
            lastFrameChangeTime = time;
            frameLoop++;
            if (frameLoop >= 2) {
                frameLoop = 0;
            }
        }

        //update the left and right values of the source of
        //the next frame on the spritesheet
        frameToDraw.left = frameLoop * frameWidth;
        frameToDraw.right = frameToDraw.left + frameWidth;
    }

    // Set start position of UFO
    public void instantiateUFO(int XPosition, int YPosition) {
        isActive = true;

        ufoXPosition = XPosition;
        ufoYPosition = YPosition;

        whereToDraw.set(
                ufoXPosition + 0,
                ufoYPosition + 0,
                ufoXPosition + frameWidth,
                ufoYPosition + frameHeight
        );

        // Randomize movement pattern here
        randomMovement = new Random().nextInt(4); // This will generate number 0 to 2
    }

    public boolean getActive(){
        return isActive;
    }

    public RectF getRectF(){
        return whereToDraw;
    }

    public void getHitByBullet(){
        live--;
        if(live <= 0){
            isActive = false;
            Game.score += 10;
            resetUFO();
        }
    }

    public void getHitByPlayer(){
        isActive = false;
        Game.score += 5;
        resetUFO();
    }

    public void getHitByMothership(){
        isActive = false;
        resetUFO();
    }

    public void resetUFO(){
        // Reset Enemy here
        isStopped = false;
        isActive = false;
    }

    public float  getXPosition(){
        return ufoXPosition;
    }
}
