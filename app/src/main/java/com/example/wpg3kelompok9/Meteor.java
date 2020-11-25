package com.example.wpg3kelompok9;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.Random;

public class Meteor {
    Bitmap meteorBitmap;

    // Just some Paint
    Paint paint;

    private boolean isActive = false;

    int screenSizeX;
    int screenSizeY;

    int frameWidth = 60;
    int frameHeight = 60;
    int frameCount = 5;
    int currentFrame = 0;
    private int frameLengthInMilliseconds = 100;
    private long lastFrameChangeTime = 0;
    private Rect frameToDraw = new Rect(
            currentFrame + 0,
            0,
            frameWidth + currentFrame,
            frameHeight + 0);

    boolean isMoving = false;
    boolean isDirected = false;
    int randomYDirection;
    int randomYSpeed;
    int randomXDirection;
    int randomXSpeed;
    int direction = 0;
    int meteorXPosition;
    int meteorYPosition;
    int tempSpeed;
    // Left, Top, Right, Bottom
    RectF whereToDraw = new RectF(
            meteorXPosition + 0,
            meteorYPosition + 0,
            meteorXPosition + frameWidth,
            meteorYPosition + frameHeight);

    private static final double SPEED_PIXEL_PER_SECOND = 300;
    private static final double MAX_SPEED = SPEED_PIXEL_PER_SECOND / GameLoop.MAX_UPS;

    public Meteor(Bitmap bitmapSource, int screenX, int screenY, int frameX, int frameY){
        screenSizeX = screenX;
        screenSizeY = screenY;

        frameWidth = frameX;
        frameHeight = frameY;

        meteorBitmap = bitmapSource;
        meteorBitmap = Bitmap.createScaledBitmap(meteorBitmap, frameWidth*frameCount, frameHeight,false);

        // Initialize
        paint = new Paint();
        paint.setColor(Color.argb(255, 255, 255, 255));

    }

    public void instantiateMeteor(int x, int y){
        meteorXPosition = x;
        if(y <= 0){
            meteorYPosition = y - frameHeight;
        }
        else{
            meteorYPosition = y;
        }

        isActive = true;

        whereToDraw.set(
                meteorXPosition + 0,
                meteorYPosition + 0,
                meteorXPosition + frameWidth,
                meteorYPosition + frameHeight
        );
    }

    public void draw(Canvas canvas){
        // Draw Meteor
        canvas.drawBitmap(meteorBitmap, frameToDraw, whereToDraw, paint);
    }

    public void update(){
        setDirectionSpeed();

        getCurrentFrame();

        // Implement player position
        whereToDraw.set(
                meteorXPosition + 0,
                meteorYPosition + 0,
                meteorXPosition + frameWidth,
                meteorYPosition + frameHeight
        );
    }

    public void setDirectionSpeed(){
        if(meteorXPosition > screenSizeX && !isDirected){
            direction = 1;
            isDirected = true;
        }
        else if(meteorYPosition > screenSizeY && !isDirected){
            direction = 2;
            isDirected = true;
        }
        else if(meteorYPosition < 0 && !isDirected){
            direction = 3;
            isDirected = true;
        }

        if(direction == 1){
            // Randomize Y Speed
            if(!isMoving){
                randomYDirection = new Random().nextInt(2);
                randomYSpeed = new Random().nextInt(3) + 1;

                if(randomYDirection > 0){
                    tempSpeed = (int) (randomYSpeed);
                }
                else{
                    tempSpeed = (int) (randomYSpeed*-1);
                }

                isMoving = true;
            }

            if(meteorXPosition + frameWidth <= 0 || meteorYPosition >= screenSizeY || meteorYPosition + frameHeight <= 0){
                isActive = false;
            }

            meteorXPosition -= MAX_SPEED;
            meteorYPosition += tempSpeed*MAX_SPEED/10;
        }
        else if(direction == 2){
            // Randomize X Speed
            if(!isMoving){
                randomXDirection = new Random().nextInt(2);
                randomXSpeed = new Random().nextInt(5) + 2;
                isMoving = true;

                if(randomXDirection > 0){
                    tempSpeed = (int) (randomXSpeed);
                }
                else{
                    tempSpeed = (int) (randomXSpeed*-1);
                }
            }

            if(meteorXPosition + frameWidth <= 0 || meteorXPosition >= screenSizeX || meteorYPosition + frameHeight <= 0){
                isActive = false;
            }

            meteorYPosition -= MAX_SPEED;
            meteorXPosition += tempSpeed*MAX_SPEED/10;
        }
        else if(direction == 3){
            // Randomize X Speed
            if(!isMoving){
                randomXDirection = new Random().nextInt(2);
                randomXSpeed = new Random().nextInt(5) + 2;
                isMoving = true;

                if(randomXDirection > 0){
                    tempSpeed = (int) (randomXSpeed);
                }
                else{
                    tempSpeed = (int) (randomXSpeed*-1);
                }
            }

            if(meteorXPosition + frameWidth <= 0 || meteorYPosition >= screenSizeY ||  meteorXPosition >= screenSizeX){
                isActive = false;
            }

            meteorYPosition += MAX_SPEED;
            meteorXPosition += tempSpeed*MAX_SPEED/10;
        }


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

    public boolean getActive(){
        return isActive;
    }

    public void getHit(){
        isActive = false;
    }

    public RectF getRectF(){
        return whereToDraw;
    }
}
