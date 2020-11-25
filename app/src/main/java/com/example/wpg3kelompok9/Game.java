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
    private Button Restartbutton;
    private int textSize;
    private int textPositionX;
    private int textPositionY;
    private Enemy[] enemy;
    private int enemyCount = 20;
    private Bitmap bitmapEnemy;
    private int spawnEnemyCoolDownSpeed = 2000;
    private long lastEnemyCoolDownSpeed = 0;
    private int enemyAliveCount = 0;
    private Mothership mothership;
    private Bitmap bitmapMothership;
    private Meteor[] meteor;
    private Bitmap bitmapMeteor;
    private int meteorValue = 20;
    private long lastMeteorCoolDownSpeed = 0;
    private int spawnMeteorCoolDownSpeed = 3000;
    private int meteorActiveCount = 0;
    private PowerUp[] gatling;
    private Bitmap bitmapGatling;
    private int gatlingValue = 20;
    private int gatlingCount = 0;
    private boolean gatlingIsActive = false;
    public  int GatlingFireCoolDownTime = 5000; // In milisecond
    private long LastGatlingCoolDownTime = 0;
    private PowerUp[] healing;
    private Bitmap bitmapHealing;
    private int healingValue = 20;
    private int healingCount = 0;
    private long lastPowerUpCoolDownSpeed = 0;
    private int spawnPowerUpCoolDownSpeed = 3000;

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

                if(Restartbutton.isPressed((double) event.getX(), (double) event.getY()) && gameIsOver){
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
            bitmapMothership = BitmapFactory.decodeResource(this.getResources(), R.drawable.mothership_500x230);
            bitmapMeteor = BitmapFactory.decodeResource(this.getResources(), R.drawable.asteroid_60x60);
            bitmapGatling = BitmapFactory.decodeResource(this.getResources(), R.drawable.gatling_50x50);
            bitmapHealing = BitmapFactory.decodeResource(this.getResources(), R.drawable.healing_50x50);

            // Initialize player
            player = new Player(screenX/2,screenY/2, bitmapPlayer, screenSizeX, screenSizeY, 225, 105);

            // Initialize enemy
            enemy = new Enemy[enemyCount];
            for(int i = 0; i < enemyCount; i++){
                enemy[i] = new Enemy(screenSizeX,screenSizeY,150,75,bitmapEnemy);
            }

            // Initialize Bullet
            bullet = new Bullet[bulletValue];
            for(int i = 0; i < bulletValue; i++){
                bullet[i] = new Bullet(50, 20, bitmapBullet);
            }

            // Initialize Mothership
            mothership = new Mothership(10,screenY/2,bitmapMothership,screenSizeX,screenSizeY,500,230);

            // Initialize Meteor
            meteor = new Meteor[meteorValue];
            for(int i = 0; i < meteorValue; i++){
                meteor[i] = new Meteor(bitmapMeteor,screenSizeX,screenSizeY,60,60);
            }

            // Initialize Gatling
            gatling = new PowerUp[gatlingValue];
            for(int i = 0; i < gatlingValue; i++){
                gatling[i] = new PowerUp(screenSizeX,screenSizeY,50,50,bitmapGatling);
            }

            // Initialize Healing
            healing = new PowerUp[healingValue];
            for(int i = 0; i < healingValue; i++){
                healing[i] = new PowerUp(screenSizeX,screenSizeY,50,50,bitmapHealing);
            }
        }
        // If mobile screen size is less than 1920x1080 pixel
        else{
            bitmapPlayer = BitmapFactory.decodeResource(this.getResources(), R.drawable.sprite_pesawat_150x70);
            bitmapBullet = BitmapFactory.decodeResource(this.getResources(), R.drawable.peluru_34x14_pixel);
            bitmapEnemy = BitmapFactory.decodeResource(this.getResources(), R.drawable.ufo_100x50);
            bitmapMothership = BitmapFactory.decodeResource(this.getResources(), R.drawable.mothership_333x153);
            bitmapMeteor = BitmapFactory.decodeResource(this.getResources(), R.drawable.asteroid_40x40);
            bitmapGatling = BitmapFactory.decodeResource(this.getResources(), R.drawable.gatling_33x33);
            bitmapHealing = BitmapFactory.decodeResource(this.getResources(), R.drawable.gatling_33x33);

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

            // Initialize Mothership
            mothership = new Mothership(10,screenY/2,bitmapMothership,screenSizeX,screenSizeY,333,153);

            // Initialize Meteor
            meteor = new Meteor[meteorValue];
            for(int i = 0; i < meteorValue; i++){
                meteor[i] = new Meteor(bitmapMeteor,screenSizeX,screenSizeY,40,40);
            }

            // Initialize Gatling
            gatling = new PowerUp[gatlingValue];
            for(int i = 0; i < gatlingValue; i++){
                gatling[i] = new PowerUp(screenSizeX,screenSizeY,33,33,bitmapGatling);
            }

            // Initialize Healing
            healing = new PowerUp[healingValue];
            for(int i = 0; i < healingValue; i++){
                healing[i] = new PowerUp(screenSizeX,screenSizeY,33,33,bitmapHealing);
            }
        }

        // Initialize Joystick
        joystick = new Joystick(screenSizeX/8, screenSizeY* 10/13, screenSizeY/8, screenSizeY/16);

        // I don't know what is this for
        setFocusable(true);

        // Button
        Restartbutton = new Button(screenSizeX* 1/2,screenSizeY* 3/4,screenSizeY/8, 0, 255, 0);

        // Set text size
        textSize = screenY * 5/108;
        textPositionX = screenSizeX * 10/213; // value = 100 in 2130 pixel screenX
        textPositionY = screenSizeY * 5/54; // value = 100 in 1080 pixel screenY
    }



    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Mulai looping game disini
        gameLoop.startLoop();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Liat tutoral akhirnya nggak kepakek
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Liat tutoral akhirnya nggak kepakek
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

        mothership.draw(canvas);
        player.draw(canvas);
        joystick.draw(canvas);

        // Looping to draw activated bullet
        for(int i = 0; i < bulletValue; i++){
            if(bullet[i].getActive()){
                bullet[i].draw(canvas);
            }
        }

        // Looping to draw activated meteor
        for(int i = 0; i < meteorValue; i++){
            if(meteor[i].getActive()){
                meteor[i].draw(canvas);
            }
        }

        // Looping to draw activated gatling
        for(int i = 0; i < gatlingValue; i++){
            if(gatling[i].getActive()){
                gatling[i].draw(canvas);
            }
        }

        // Looping to draw activated healing
        for(int i = 0; i < healingValue; i++){
            if(healing[i].getActive()){
                healing[i].draw(canvas);
            }
        }

        // UI
        drawScore(canvas);
        drawPlayerLivePoint(canvas);
        drawMothershipLivePoint(canvas);

        // Only for Debugging
        debugging(canvas);

        // if Game is Over
        // Draw Game Over Panel
        if(gameIsOver){
            drawGameIsOver(canvas);
            Restartbutton.drawRestartButton(canvas);
        }
    }

    public void debugging(Canvas canvas){
        // Debugging
        Paint paint = new Paint();
        paint.setColor(Color.argb(255,  255, 0, 255));
        paint.setTextSize(textSize);
        canvas.drawText("Bullet  : " + bulletCount,textPositionX*18,textPositionY,paint);
        canvas.drawText("Enemy   : " + enemyAliveCount,textPositionX*18,textPositionY*2,paint);
        canvas.drawText("Meteor  : " + meteorActiveCount,textPositionX*18,textPositionY*3,paint);
        canvas.drawText("Gatling : " + gatlingCount,textPositionX*18,textPositionY*4,paint);
        canvas.drawText("Healing : " + healingCount,textPositionX*18,textPositionY*5,paint);
        drawScreenSize(canvas);
    }

    public void drawUPS(Canvas canvas) {
        String averageUPS = String.format("%.3f",gameLoop.getAverageUPS());
        Paint paint = new Paint();
        paint.setColor(Color.argb(255,  255, 0, 255));
        paint.setTextSize(textSize);
        canvas.drawText("UPS : " + averageUPS,textPositionX*13,textPositionY,paint);
    }

    public void drawFPS(Canvas canvas) {
        String averageFPS = String.format("%.3f",gameLoop.getAverageFPS());
        Paint paint = new Paint();
        paint.setColor(Color.argb(255,  255, 0, 255));
        paint.setTextSize(textSize);
        canvas.drawText("FPS : " + averageFPS,textPositionX*13,textPositionY*2,paint);
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
        canvas.drawText("Score : " + score,textPositionX,textPositionY,paint);
    }

    public void drawPlayerLivePoint(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(Color.argb(255,  255, 0, 255));
        paint.setTextSize(textSize);
        canvas.drawText("Player Live : " + player.getLivePoint(),textPositionX,textPositionY*2,paint);
    }

    public void drawMothershipLivePoint(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(Color.argb(255,  255, 0, 255));
        paint.setTextSize(textSize);
        canvas.drawText("Mothership Live : " + mothership.getLive(),textPositionX,textPositionY*3,paint);
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
            spawnMeteor();
            spawnPowerUp();
            mothership.update();

            //Looping for enemy
            for(int i = 0; i < enemyCount; i++){
                if(enemy[i].getActive()){
                    enemy[i].update();
                }
            }

            // Looping for update bullet
            for(int i = 0; i < bulletValue; i++){
                if(bullet[i].getActive()){
                    bullet[i].update();
                }
            }

            // Looping for update meteor
            for(int i = 0; i < meteorValue; i++){
                if(meteor[i].getActive()){
                    meteor[i].update();
                }
            }

            // Looping for update gatling
            for(int i = 0; i < gatlingValue; i++){
                if(gatling[i].getActive()){
                    gatling[i].update();
                }
            }

            // Looping for update healing
            for(int i = 0; i < healingValue; i++){
                if(healing[i].getActive()){
                    healing[i].update();
                }
            }

            // Collision Check
            collsionCheck();

            // Live Point Check
            if(player.getLivePoint() <= 0 || mothership.getLive() <= 0){
                gameIsOver = true;
            }
        }
    }

    // This function is for reset game
    public void resetTheGame(){
        score = 0;
        player.resetGame();
        mothership.resetGame();
        gameIsOver = false;
    }

    // This function is for Instantiate bullet
    public void fireBullet(){
        long time = System.currentTimeMillis();
        int coolDownTemp;
        if(gatlingIsActive){
            coolDownTemp = fireCoolDownSpeed/5;
            if(time > LastGatlingCoolDownTime + GatlingFireCoolDownTime){
                gatlingIsActive = false;
            }
        }
        else{
            coolDownTemp = fireCoolDownSpeed;
        }
        if (time > lastFireCoolDownTime + coolDownTemp) {
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

    public void spawnMeteor(){
        long time = System.currentTimeMillis();
        boolean meteorCreated = false;
        if(time > lastMeteorCoolDownSpeed + spawnMeteorCoolDownSpeed){
            // Instantiate Meteor
            int randomSpawn = new Random().nextInt(5);

            if(randomSpawn == 0){
                int randomY = new Random().nextInt(screenSizeY * 8/12) + (screenSizeY * 2/12);
                meteor[meteorActiveCount].instantiateMeteor( screenSizeX + 5, randomY);
                meteorCreated = true;
            }
            else if(randomSpawn == 1){
                int randomX = new Random().nextInt(screenSizeX * 7/12) + (screenSizeX * 4/12);
                meteor[meteorActiveCount].instantiateMeteor( randomX + 0, 0-5);
                meteorCreated = true;
            }
            else if(randomSpawn == 2){
                int randomX = new Random().nextInt(screenSizeX * 7/12) + (screenSizeX * 4/12);
                meteor[meteorActiveCount].instantiateMeteor( randomX + 0, screenSizeY + 5);
                meteorCreated = true;
            }

            if(meteorCreated){
                meteorActiveCount++;
                if(meteorActiveCount >= meteorValue){
                    meteorActiveCount = 0;
                }
            }
            lastMeteorCoolDownSpeed = time;
        }
    }

    public void spawnPowerUp(){
        long time = System.currentTimeMillis();
        if(time > lastPowerUpCoolDownSpeed + spawnPowerUpCoolDownSpeed){
            lastPowerUpCoolDownSpeed = time;
            int randomTemp = new Random().nextInt(5);

            if(randomTemp == 2){
                // Spawn Gatling
                int randomY = new Random().nextInt(screenSizeY * 8/12) + (screenSizeY * 2/12);
                gatling[gatlingCount].instantiatePowerUp(screenSizeX + 5, randomY + 0);

                gatlingCount++;
                if(gatlingCount >= gatlingValue){
                    gatlingCount = 0;
                }
            }
            else if(randomTemp == 3){
                // Spawn Healing
                int randomY = new Random().nextInt(screenSizeY * 8/12) + (screenSizeY * 2/12);
                healing[healingCount].instantiatePowerUp(screenSizeX + 5, randomY + 0);

                healingCount++;
                if(healingCount >= healingValue){
                    healingCount = 0;
                }
            }
        }
    }

    public void levelUp(){
        // Increasing level by Play Time
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

        // Check if UFO hit Mothership
        for(int i = 0; i < enemyCount; i++){
            if(enemy[i].getActive()){
                if(RectF.intersects(enemy[i].getRectF(), mothership.getRectF())){
                    enemy[i].getHitByMothership();
                    mothership.getHitByUFO();
                }
            }
        }

        // Check if Meteor hit player
        for(int i = 0; i < meteorActiveCount; i++){
            if(meteor[i].getActive()){
                if(RectF.intersects(meteor[i].getRectF(), player.getRectF())){
                    meteor[i].getHit();
                    player.getHitByUFO();
                }
            }
        }

        // Check if Meteor hit Mothership
        for(int i = 0; i < meteorActiveCount; i++){
            if(meteor[i].getActive()){
                if(RectF.intersects(meteor[i].getRectF(), mothership.getRectF())){
                    meteor[i].getHit();
                }
            }
        }

        // Check if Player Bullet hit Meteor
        for(int i = 0; i < bulletValue; i++){
            for(int j = 0; j < meteorActiveCount; j++){
                if(bullet[i].getActive() && meteor[j].getActive()){
                    if(RectF.intersects(bullet[i].getRectF(), meteor[j].getRectF())){
                        bullet[i].setActive(false);
                    }
                }
            }
        }

        // Check if Player Get Gatling PowerUp
        for(int i = 0; i < gatlingValue; i++){
            if(gatling[i].getActive()){
                if(RectF.intersects(gatling[i].getRectF(), player.getRectF())){
                    gatlingIsActive = true;
                    LastGatlingCoolDownTime = System.currentTimeMillis();
                    gatling[i].getHit();
                }
            }
        }

        // Check if Player Get Healing PowerUp
        for(int i = 0; i < healingValue; i++){
            if(healing[i].getActive()){
                if(RectF.intersects(healing[i].getRectF(), player.getRectF())){
                    player.addLive();
                    healing[i].getHit();
                }
            }
        }
    }
    // Tail Function
}
// End off class
