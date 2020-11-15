package com.example.wpg3kelompok9;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

/**
 * Game manages all objects in the game and it responsible for
 * updating all states and render all object to the screen
 */
public class Game extends SurfaceView implements SurfaceHolder.Callback {
    private final Player player;
    private GameLoop gameLoop;
    private int screenSizeX;
    private int screenSizeY;
    private Bitmap bitmapPlayer;
    private Bitmap bitmapBullet;
    private Joystick joystick;
    private static final int bulletValue = 50;
    private Bullet[] bullet;
    private int bulletCount = 0;
    public static int coolDownSpeed = 1000; // In milisecond
    private Button button;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // Handle Touch Event action
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(joystick.isPressed((double) event.getX(), (double) event.getY())){
                    joystick.setIsPressed(true);
                }

                return true;
            case MotionEvent.ACTION_MOVE:
                if(joystick.getIsPressed()){
                    joystick.setActuator((double) event.getX(), (double) event.getY());
                }
                return true;
            case MotionEvent.ACTION_UP:
                joystick.setIsPressed(false);
                joystick.resetActuator();
                return true;
        }


        return super.onTouchEvent(event);
    }


    // The Constructor
    public Game(Context context, int screenX, int screenY) {
        super(context);

        // Get surface holder and add callback
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        // Game Loop for FPS adn UPS
        gameLoop = new GameLoop(this, surfaceHolder);

        // Load Bitmap
        bitmapPlayer = BitmapFactory.decodeResource(this.getResources(), R.drawable.sprite_pesawat_150x150);
        bitmapBullet = BitmapFactory.decodeResource(this.getResources(), R.drawable.peluru_15x15);

        // Save screen size
        screenSizeX = screenX;
        screenSizeY = screenY;

        // Initialize player
        player = new Player(screenX/2,screenY/2, bitmapPlayer, screenSizeX, screenSizeY);

        // Initialize Joystick
        joystick = new Joystick(275, 850, 120, 60);

        setFocusable(true);

        // Button
        button = new Button(850,850,60);

        // Bullet
        bullet = new Bullet[bulletValue];
        for(int i = 0; i < bulletValue; i++){
            bullet[i] = new Bullet();
        }
    }



    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        gameLoop.startLoop();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        drawUPS(canvas);
        drawFPS(canvas);

        joystick.draw(canvas);
        player.draw(canvas);

        // Looping to draw activated bullet
        for(int i = 0; i < bulletValue; i++){
            if(bullet[i].getActive()){
                bullet[i].draw(canvas);
            }
        }

        // Debugging
        Paint paint = new Paint();
        paint.setColor(Color.argb(255,  255, 0, 0));
        paint.setTextSize(50);
        canvas.drawText("Bullet : " + bulletCount,100,300,paint);
    }

    public void drawUPS(Canvas canvas) {
        String averageUPS = Double.toString(gameLoop.getAverageUPS());
        Paint paint = new Paint();
        paint.setColor(Color.argb(255,  255, 0, 0));
        paint.setTextSize(50);
        canvas.drawText("UPS : " + averageUPS,100,100,paint);
    }

    public void drawFPS(Canvas canvas) {
        String averageFPS = Double.toString(gameLoop.getAverageFPS());
        Paint paint = new Paint();
        paint.setColor(Color.argb(255,  255, 0, 0));
        paint.setTextSize(50);
        canvas.drawText("FPS : " + averageFPS,100,200,paint);
    }

    public void update() {
        //Update game state
        joystick.update();
        player.update(joystick);

        // Looping for update bullet
        // When bullet is Active
        for(int i = 0; i < bulletValue; i++){
            if(bullet[i].getActive()){
                bullet[i].update();
            }
        }
    }

    // This function is for Instantiate bullet
    public void fireBullet(){
        bulletCount++;
        if(bulletCount >= bulletValue){
            bulletCount = 0;
        }
        bullet[bulletCount].instantiateBullet(player.getPosition(), screenSizeX, screenSizeY, bitmapBullet);
    }
}
