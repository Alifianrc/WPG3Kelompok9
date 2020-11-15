package com.example.wpg3kelompok9;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class Player {

    // Player Sprite
    private Bitmap playerBitmap;

    // Player sprite sheet
    private boolean isAnimating = true;
    private int currentSprite = 0;
    private int frameLoop = 0;
    private int frameLengthInMilliseconds = 100;
    private long lastFrameChangeTime = 0;
    private int frameCount   = 2;
    private int frameCountY  = 6;
    private int frameWidth   = 150;
    private int frameHeight  = 150;
    private Rect frameToDraw = new Rect(
            frameLoop + 0,
            currentSprite + 0,
            frameWidth + frameLoop,
            frameHeight + currentSprite);

    // Player position
    private float playerXPosition;
    private float playerYPosition;
    // Left, Top, Right, Bottom
    RectF whereToDraw = new RectF(
            playerXPosition + 0,
            playerYPosition + 0,
            playerXPosition + frameWidth,
            playerYPosition + frameHeight);

    // Just some Paint
    Paint paint;

    // Save screen size
    private int screenSizeX;
    private int screenSizeY;

    // IDK
    private double velocityX;
    private double velocityY;
    private static final double SPEED_PIXEL_PER_SECOND = 400;
    private static final double MAX_SPEED = SPEED_PIXEL_PER_SECOND / GameLoop.MAX_UPS;

    // The Counstructor
    public Player(int positionX, int positionY, Bitmap bitmapSource, int screenX, int screenY) {
        // Player start position
        this.playerXPosition = positionX;
        this.playerYPosition = positionY;

        // Initialize
        paint = new Paint();
        paint.setColor(Color.argb(255, 255, 255, 255));

        // Load player Sprite
        playerBitmap = bitmapSource;
        playerBitmap = Bitmap.createScaledBitmap(playerBitmap, frameWidth*frameCount, frameHeight*frameCountY,false);

        // Save screen size
        screenSizeX = screenX;
        screenSizeY = screenY;
    }


    // Anything we need to draw in Game
    public void draw(Canvas canvas) {
        // Get animation
        getCurrentFrame();

        // Draw player
        canvas.drawBitmap(playerBitmap, frameToDraw, whereToDraw, paint);

    }


    // Anything we need to update
    public void update(Joystick joystick) {

        // Update Player positon
        velocityX = joystick.getActuatorX()*MAX_SPEED;
        velocityY = joystick.getActuatorY()*MAX_SPEED;
        // Don't make player move out from screen
        if (playerXPosition < 5) {
            if (velocityX > 0) {
                playerXPosition += velocityX;
            }
        }
        else if ((playerXPosition + frameWidth) > (screenSizeX - 5)){
            if (velocityX < 0) {
                playerXPosition += velocityX;
            }
        }
        else{
            playerXPosition += velocityX;
        }
        // in Y direction
        if (playerYPosition < 5) {
            if (velocityY > 0) {
                playerYPosition += velocityY;
            }
        }
        else if ((playerYPosition + frameHeight) > (screenSizeY - 5)){
            if (velocityY < 0) {
                playerYPosition += velocityY;
            }
        }
        else{
            playerYPosition += velocityY;
        }

        // Change player sprite based on Velocity
        // Mundur
        if (velocityX < 0 && velocityY > -1  && velocityY < 1){
            currentSprite = 0;
            isAnimating = true;
        }
        // Maju
        else if (velocityX > 0 && velocityY > -1 && velocityY < 1){
            currentSprite = 1;
            isAnimating = true;
        }
        // Naik Mundur
        else if (velocityX < 0 && velocityY < 0) {
            currentSprite = 2;
            isAnimating = false;
        }
        // Naik Maju
        else if (velocityX > 0 && velocityY < 0){
            currentSprite = 3;
            isAnimating = false;
        }
        // Turun Mundur
        else if (velocityX < 0 && velocityY > 0){
            currentSprite = 4;
            isAnimating = false;
        }
        // Turun Maju
        else if (velocityX > 0 && velocityY > 0){
            currentSprite = 5;
            isAnimating = false;
        }
        // Diam
        else {
            currentSprite = 0;
            isAnimating = true;
        }

        // Changing sprite to draw
        frameToDraw.top = currentSprite * frameHeight;
        frameToDraw.bottom = frameToDraw.top + frameHeight;

        // Implement player position
        whereToDraw.set(playerXPosition,
                        playerYPosition,
                  playerXPosition + frameWidth,
                playerYPosition + frameHeight);

        // Get player Frame
        frameToDraw.left = frameLoop * frameWidth;
        frameToDraw.right = frameToDraw.left + frameWidth;


    }

    public void setPosition(float x, float y){
        this.playerXPosition = x;
        this.playerYPosition = y;
    }

    public RectF getPosition(){
        // This function called for determine
        // Bullet spawn position
        RectF temp = new RectF(
                playerXPosition + (frameWidth * 3/4),
                playerYPosition + (frameHeight * 1/2),
                playerXPosition + frameWidth + (frameWidth * 3/4),
                playerYPosition + frameHeight + (frameHeight * 1/2)
        );

        return temp;
    }

    public void getCurrentFrame(){
        long time  = System.currentTimeMillis();
        if(isAnimating){
            if ( time > lastFrameChangeTime + frameLengthInMilliseconds) {
                lastFrameChangeTime = time;
                frameLoop++;
                if (frameLoop >= frameCount) {
                    frameLoop = 0;
                }
            }
        }
        else if(!isAnimating){
            frameLoop = 0;
        }

        //update the left and right values of the source of
        //the next frame on the spritesheet
        frameToDraw.left = frameLoop * frameWidth;
        frameToDraw.right = frameToDraw.left + frameWidth;
    }
}
