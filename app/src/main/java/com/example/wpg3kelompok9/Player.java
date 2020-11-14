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
    private int currentFrame;
    private int frameCount   = 4;
    private int frameWidth   = 150;
    private int frameHeight  = 150;
    private Rect frameToDraw = new Rect(
            0,
            0,
            frameWidth,
            frameHeight);

    // Player position
    private float playerXPosition;
    private float playerYPosition;
    // Left, Top, Right, Bottom
    RectF whereToDraw = new RectF(
            playerXPosition,
            playerYPosition,
            playerXPosition + frameWidth,
            playerYPosition + frameHeight);

    // Just some Paint
    Paint paint;

    // IDK
    private double velocityX;
    private double velocityY;
    private static final double SPEED_PIXEL_PER_SECOND = 400;
    private static final double MAX_SPEED = SPEED_PIXEL_PER_SECOND / GameLoop.MAX_UPS;

    // The Counstructor
    public Player(int positionX, int positionY, Bitmap bitmapSource) {
        // Player start position
        this.playerXPosition = positionX;
        this.playerYPosition = positionY;

        // Start Frame
        currentFrame = 1;

        // Initialize
        paint = new Paint();
        paint.setColor(Color.argb(255, 255, 255, 255));

        // Load player Sprite
        playerBitmap = bitmapSource;
        playerBitmap = Bitmap.createScaledBitmap(playerBitmap, frameWidth*frameCount, frameHeight,false);
    }


    // Anything we need to draw in Game
    public void draw(Canvas canvas) {
        // Draw player
        canvas.drawBitmap(playerBitmap, frameToDraw, whereToDraw, paint);
    }


    // Anything we need to update
    public void update(Joystick joystick) {

        // Update Player positon
        velocityX = joystick.getActuatorX()*MAX_SPEED;
        velocityY = joystick.getActuatorY()*MAX_SPEED;
        playerXPosition += velocityX;
        playerYPosition += velocityY;

        // Change player sprite based on Velocity
        if (velocityY < 0){
           currentFrame = 2;
        } else if ( velocityY > 0){
            currentFrame = 0;
        } else {
            currentFrame = 1;
        }

        // Implement player position
        whereToDraw.set(playerXPosition,
                        playerYPosition,
                  playerXPosition + frameWidth,
                playerYPosition + frameHeight);

        // Get player Frame
        frameToDraw.left = currentFrame * frameWidth;
        frameToDraw.right = frameToDraw.left + frameWidth;


    }

    public void setPosition(float x, float y){
        this.playerXPosition = x;
        this.playerYPosition = y;
    }
}
