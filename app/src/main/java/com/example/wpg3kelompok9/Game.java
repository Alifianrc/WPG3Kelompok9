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

import java.util.Random;

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
    public static int fireCoolDownSpeed = 250; // In milisecond
    private long lastFireCoolDownTime = -3;
    private Button button;
    private int textSize;
    private int textPositionX;
    private int textPositionY;
    private Enemy[] enemy;
    private int enemyCount = 20;
    Bitmap bitmapEnemy;
    private int spawnEnemyCoolDownSpeed = 2000;
    private long lastEnemyCoolDownSpeed = 0;
    private int enemyAliveCount = 0;

    // Public score for easy scoring
    public static int score = 0;

    // Scene Controller
    private boolean gameIsOver = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // Handle Touch Event action
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(joystick.isPressed((double) event.getX(), (double) event.getY())){
                    joystick.setIsPressed(true);
                }

                if(button.isPressed((double) event.getX(), (double) event.getY()) && gameIsOver){
                    resetTheGame();
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

        // Save screen size
        screenSizeX = screenX;
        screenSizeY = screenY;

        // Load Bitmap
        // If mobile screen size is 1920x1080 pixel or more
        if(screenSizeX >= 1920 && screenSizeY >= 1080){
            bitmapPlayer = BitmapFactory.decodeResource(this.getResources(), R.drawable.sprite_pesawat_225x105);
            bitmapBullet = BitmapFactory.decodeResource(this.getResources(), R.drawable.peluru_50x20);
            bitmapEnemy = BitmapFactory.decodeResource(this.getResources(), R.drawable.ufo_150x75);

            // Initialize player
            player = new Player(screenX/2,screenY/2, bitmapPlayer, screenSizeX, screenSizeY, 225, 105);

            // Initialize enemy
            enemy = new Enemy[enemyCount];
            for(int i = 0; i < enemyCount; i++){
                enemy[i] = new Enemy(screenSizeX,screenSizeY,150,75,bitmapEnemy);
            }

            // Bullet
            bullet = new Bullet[bulletValue];
            for(int i = 0; i < bulletValue; i++){
                bullet[i] = new Bullet(50, 20, bitmapBullet);
            }
        }
        // If mobile screen size is less than 1920x1080 pixel
        else{
            bitmapPlayer = BitmapFactory.decodeResource(this.getResources(), R.drawable.sprite_pesawat_150x70);
            bitmapBullet = BitmapFactory.decodeResource(this.getResources(), R.drawable.peluru_34x14_pixel);
            bitmapEnemy = BitmapFactory.decodeResource(this.getResources(), R.drawable.ufo_100x50);

            // Initialize player
            player = new Player(screenX/2,screenY/2, bitmapPlayer, screenSizeX, screenSizeY, 150,70);

            // Initialize enemy
            enemy = new Enemy[enemyCount];
            for(int i = 0; i < enemyCount; i++){
                enemy[i] = new Enemy(screenSizeX,screenSizeY,100,50,bitmapEnemy);
            }

            // Bullet
            bullet = new Bullet[bulletValue];
            for(int i = 0; i < bulletValue; i++){
                bullet[i] = new Bullet(34, 14, bitmapBullet);
            }
        }

        // Initialize Joystick
        joystick = new Joystick(screenSizeX/8, screenSizeY* 10/13, screenSizeY/8, screenSizeY/16);

        // I don't know what is this for
        setFocusable(true);

        // Button
        button = new Button(screenSizeX* 1/2,screenSizeY* 3/4,screenSizeY/8);

        // Set text size
        textSize = screenY * 5/108;
        textPositionX = screenSizeX * 10/213; // value = 100 in 2130 pixel screenX
        textPositionY = screenSizeY * 5/54; // value = 100 in 1080 pixel screenY
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

        //Looping for enemy
        for(int i = 0; i < enemyCount; i++){
            if(enemy[i].getActive()){
                enemy[i].draw(canvas);
            }
        }

        player.draw(canvas);
        joystick.draw(canvas);


        // Looping to draw activated bullet
        for(int i = 0; i < bulletValue; i++){
            if(bullet[i].getActive()){
                bullet[i].draw(canvas);
            }
        }

        // UI
        drawScore(canvas);
        drawPlayerLivePoint(canvas);

        // Only for Debugging
        debugging(canvas);

        // if Game is Over
        // Draw Game Over Panel
        if(gameIsOver){
            drawGameIsOver(canvas);
            button.drawRestartButton(canvas);
        }
    }

    public void debugging(Canvas canvas){
        // Debugging
        Paint paint = new Paint();
        paint.setColor(Color.argb(255,  255, 0, 255));
        paint.setTextSize(textSize);
        canvas.drawText("Bullet : " + bulletCount,textPositionX,textPositionY*3,paint);
        canvas.drawText("Enemy  : " + enemyAliveCount,textPositionX,textPositionY*4,paint);
        drawScreenSize(canvas);
    }

    public void drawUPS(Canvas canvas) {
        String averageUPS = String.format("%.3f",gameLoop.getAverageUPS());
        Paint paint = new Paint();
        paint.setColor(Color.argb(255,  255, 0, 255));
        paint.setTextSize(textSize);
        canvas.drawText("UPS : " + averageUPS,textPositionX,textPositionY,paint);
    }

    public void drawFPS(Canvas canvas) {
        String averageFPS = String.format("%.3f",gameLoop.getAverageFPS());
        Paint paint = new Paint();
        paint.setColor(Color.argb(255,  255, 0, 255));
        paint.setTextSize(textSize);
        canvas.drawText("FPS : " + averageFPS,textPositionX,textPositionY*2,paint);
    }

    public void drawScreenSize(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(Color.argb(255,  255, 0, 255));
        paint.setTextSize(textSize);
        canvas.drawText("Screen Size X : " + screenSizeX,textPositionX*6,textPositionY,paint);
        canvas.drawText("Screen Size Y : " + screenSizeY,textPositionX*6,textPositionY*2,paint);
    }

    public void drawScore(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(Color.argb(255,  255, 0, 255));
        paint.setTextSize(textSize);
        canvas.drawText("Score : " + score,textPositionX*13,textPositionY,paint);
    }

    public void drawPlayerLivePoint(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(Color.argb(255,  255, 0, 255));
        paint.setTextSize(textSize);
        canvas.drawText("Live : " + player.getLivePoint(),textPositionX*13,textPositionY*2,paint);
    }

    public void drawGameIsOver(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(Color.argb(255,  255, 0, 255));
        paint.setTextSize(textSize * 5);
        canvas.drawText("Game Over",screenSizeX/6,screenSizeY/2,paint);
    }

    public void update() {
        // Only update is game is not over
        if(!gameIsOver){

            //Update game state
            joystick.update();
            player.update(joystick);
            fireBullet();
            spawnEnemy();

            //Looping for enemy
            for(int i = 0; i < enemyCount; i++){
                if(enemy[i].getActive()){
                    enemy[i].update();
                }
            }

            // Looping for update bullet
            // When bullet is Active
            for(int i = 0; i < bulletValue; i++){
                if(bullet[i].getActive()){
                    bullet[i].update();
                }
            }

            // Collision Check
            collsionCheck();

            // Player Live Point Check
            if(player.getLivePoint() <= 0){
                gameIsOver = true;
            }
        }
    }

    // This function is for reset game
    public void resetTheGame(){
        score = 0;
        player.resetGame();
        gameIsOver = false;
    }

    // This function is for Instantiate bullet
    public void fireBullet(){
        long time = System.currentTimeMillis();
        if (time > lastFireCoolDownTime + fireCoolDownSpeed) {
            lastFireCoolDownTime = time;
            bulletCount++;
            if(bulletCount >= bulletValue){
                bulletCount = 0;
            }

            // Instantiate Bullet
            bullet[bulletCount].instantiateBullet(player.getPosition(), screenSizeX, screenSizeY);
        }
    }

    // This fundtion is for spawning enemy
    public void spawnEnemy(){
        long time = System.currentTimeMillis();
        if(time > lastEnemyCoolDownSpeed + spawnEnemyCoolDownSpeed){
            lastEnemyCoolDownSpeed = time;
            enemyAliveCount++;
            if(enemyAliveCount >= enemyCount){
                enemyAliveCount = 0;
            }

            // Instantiate UFO
            int randomY = new Random().nextInt(screenSizeY * 8/12) + (screenSizeY * 2/12);
            enemy[enemyAliveCount].instantiateUFO(screenSizeX + 5, randomY);
        }
    }

    // This Function is for Checking any collision
    public void collsionCheck(){

        // Check if Player Bullet hit Enemy UFO
        for(int i = 0; i < bulletValue; i++){
            for(int j = 0; j < enemyCount; j++){
                if(bullet[i].getActive() && enemy[j].getActive()){
                    if(RectF.intersects(bullet[i].getRectF(), enemy[j].getRectF())){
                        bullet[i].setActive(false);
                        enemy[j].getHitByBullet();
                    }
                }
            }
        }

        // Check if Player hit UFO
        for(int i = 0; i < enemyCount; i++){
            if(enemy[i].getActive()){
                if(RectF.intersects(enemy[i].getRectF(), player.getRectF())){
                    enemy[i].getHitByPlayer();
                    player.getHitByUFO();
                }
            }
        }
    }
}
