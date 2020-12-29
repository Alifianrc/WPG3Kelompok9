package com.example.wpg3kelompok9;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class Mothership {

    Bitmap mothershipBitmap;

    private int live = 10;

    // Just some Paint
    Paint paint;

    int screenSizeX;
    int screenSizeY;

    boolean goRight = true;
    boolean goDown = true;
    int frameWidth = 0;
    int frameHeight = 0;
    int frameCount = 4;
    int currentFrame = 0;
    private int frameLengthInMilliseconds = 200;
    private long lastFrameChangeTime = 0;
    private Rect frameToDraw;

    int mothershipXPosition;
    int mothershipYPosition;
    // Left, Top, Right, Bottom
    RectF whereToDraw;

    private static final double SPEED_PIXEL_PER_SECOND = 100;
    private static final double MAX_SPEED = SPEED_PIXEL_PER_SECOND / GameLoop.MAX_UPS;

    private int frameXSize;
    private int frameYSize;

    public Mothership(int positionX, int positionY, Bitmap bitmapSource, int screenX, int screenY, int frameX, int frameY){
        mothershipXPosition = positionX;
        mothershipYPosition = positionY;

        screenSizeX = screenX;
        screenSizeY = screenY;

        frameWidth = frameX;
        frameHeight = frameY;

        mothershipBitmap = bitmapSource;
        mothershipBitmap = Bitmap.createScaledBitmap(mothershipBitmap, frameWidth*frameCount, frameHeight,false);

        // Initialize
        paint = new Paint();
        paint.setColor(Color.argb(255, 255, 255, 255));

        frameXSize = frameX;
        frameYSize = frameY;

        whereToDraw = new RectF(
                mothershipXPosition + 0,
                mothershipYPosition + 0,
                mothershipXPosition + frameWidth,
                mothershipYPosition + frameHeight
        );

        frameToDraw = new Rect(
            currentFrame + 0,
            0,
            frameWidth + currentFrame,
            frameHeight + 0
        );
    }

    public void draw(Canvas canvas){
        // Draw Mothership
        canvas.drawBitmap(mothershipBitmap, frameToDraw, whereToDraw, paint);

        //paint.setTextSize(50);

        //Just for Debugging
        //canvas.drawText("Mothership " + mothershipXPosition,200,500,paint);
    }

    public void update(){
        setDirectionSpeed();

        getCurrentFrame();

        // Implement player position
        whereToDraw.set(
                mothershipXPosition + 0,
                mothershipYPosition + 0,
                mothershipXPosition + frameWidth,
                mothershipYPosition + frameHeight
        );
    }

    public void setDirectionSpeed(){
        int maxLeft = 5;
        int maxRight = screenSizeX/3;
        int maxTop = screenSizeY/4;
        int maxBottom = screenSizeY/4*3;



        if(mothershipXPosition <= maxLeft){
            goRight = true;
        }
        else if(mothershipXPosition + frameWidth >= maxRight){
            goRight = false;
        }

        if(mothershipYPosition <= maxTop){
            goDown = true;
        }
        else if(mothershipYPosition + frameHeight >= maxBottom){
            goDown = false;
        }

        if(goRight){
            mothershipXPosition += MAX_SPEED;
        }
        else{
            mothershipXPosition -= MAX_SPEED;
        }

        if(goDown){
            mothershipYPosition += MAX_SPEED;
        }
        else{
            mothershipYPosition -= MAX_SPEED;
        }
    }

    public void getHitByUFO(){
        live--;
    }

    public int getLive(){
        return live;
    }

    public void resetGame(){
        live = 10;
        mothershipXPosition = 10;
        mothershipYPosition = screenSizeY/2;
    }

    public void getCurrentFrame(){
        long time = System.currentTimeMillis();
        if ( time > lastFrameChangeTime + frameLengthInMilliseconds) {
            lastFrameChangeTime = time;
            currentFrame++;
            if (currentFrame >= frameCount) {
                currentFrame = 0;
            }
        }

        //update the left and right values of the source of
        //the next frame on the spritesheet
        frameToDraw.left = currentFrame * frameWidth;
        frameToDraw.right = frameToDraw.left + frameWidth;
    }

    public RectF getRectF(){
        return whereToDraw;
    }

    public int getFrameX(){
        return frameXSize;
    }
    public int getFrameY(){
        return frameYSize;
    }
}
