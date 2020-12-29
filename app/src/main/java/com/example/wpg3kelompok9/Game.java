package com.example.wpg3kelompok9;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.IOException;
import java.util.Random;

/**
 * Game manages all objects in the game and it responsible for
 * updating all states and render all object to the screen
 */
public class Game extends SurfaceView implements SurfaceHolder.Callback {

    // The Player
    private final Player player;
    private Bitmap bitmapPlayer;

    // Looping Game Class
    private GameLoop gameLoop;

    // Save screen Size
    private int screenSizeX;
    private int screenSizeY;

    // The Joystick
    private Joystick joystick;

    // Player Bullet + Shoot
    private Bullet[] bullet;
    private Bitmap bitmapBullet;
    private static final int bulletValue = 100;
    private int bulletCount = 0;
    public static int fireCoolDownSpeed = 250; // In milisecond
    private long lastFireCoolDownTime = 0;

    // Ui default
    private int textSize;
    private int textPositionX;
    private int textPositionY;

    // Enemy
    private Enemy[] enemy;
    private int enemyCount = 100;
    private Bitmap bitmapEnemy;
    private int spawnEnemyCoolDownSpeed = 2000;
    private long lastEnemyCoolDownSpeed = 0;
    private int enemyAliveCount = 0;
    private int enemySpawnValue = 1;

    // Mothership
    private Mothership mothership;
    private Bitmap bitmapMothership;

    // For Meteor
    private Meteor[] meteor;
    private Bitmap bitmapMeteor;
    private int meteorValue = 100;
    private long lastMeteorCoolDownSpeed = 0;
    private int spawnMeteorCoolDownSpeed = 3000;
    private int meteorActiveCount = 0;
    private int meteorSpawnValue = 1;

    // The powerUp
    // Gatling
    private PowerUp[] gatling;
    private Bitmap bitmapGatling;
    private int gatlingValue = 50;
    private int gatlingCount = 0;
    private boolean gatlingIsActive = false;
    public  int GatlingFireCoolDownTime = 5000; // In milisecond
    private long LastGatlingCoolDownTime = 0;
    // Healing
    private PowerUp[] healing;
    private Bitmap bitmapHealing;
    private int healingValue = 50;
    private int healingCount = 0;
    private long lastPowerUpCoolDownSpeed = 0;
    private int spawnPowerUpCoolDownSpeed = 1000;

    // For sound FX
    SoundPool soundPool;
    int destroyedSoundID = -1;
    int getItemSoundID = -1;
    int hitSoundID = -1;
    int shootID = -1;
    int soundtrackID = -1;

    // Public score for easy scoring
    public static int score = 0;

    // Scene Controller
    private boolean gameIsOver = false;
    private boolean gameIsStarted = false;

    // The Button
    private Button Restartbutton;
    private Button StartButton;

    // For laveling
    private int scoreThreshold = 170;
    private int currentLevel = 1;

    // Background particle
    Particle particle;

    // End off first Declaration

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

                if(StartButton.isPressed((double) event.getX(), (double) event.getY()) && !gameIsStarted){
                   gameIsStarted = true;
                   startGame();
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

        // Load All Bitmap
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
            bitmapHealing = BitmapFactory.decodeResource(this.getResources(), R.drawable.healing_33x33);

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

        // Initialize particle
        particle = new Particle(screenX, screenY);

        // I don't know what is this for
        // This is from Joystick Tutorial
        setFocusable(true);

        // Button
        Restartbutton = new Button(screenSizeX* 1/2,screenSizeY* 3/4,screenSizeY/8, 255, 0, 255);
        StartButton = new Button(screenSizeX* 1/2,screenSizeY* 3/4,screenSizeY/8, 0, 255, 0);

        // Set default text size and position
        textSize = screenY * 5/108;
        textPositionX = screenSizeX * 10/213; // value = 100 in 2130 pixel screenX
        textPositionY = screenSizeY * 5/54; // value = 100 in 1080 pixel screenY

        // Load Sound
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        try{
            // Create objects of the 2 required classes
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            // Load our fx in memory ready for use
            descriptor = assetManager.openFd("destroyed.ogg");
            destroyedSoundID = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("getitem.ogg");
            getItemSoundID = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("hit.ogg");
            hitSoundID = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("shoot.ogg");
            shootID = soundPool.load(descriptor, 0);

            // BGM masih tidak bisa
            //descriptor = assetManager.openFd("soundtrack.ogg");
            //soundtrackID = soundPool.load(descriptor, 0);

        } catch (IOException e) {
            // Print an error message to the console
            Log.e("error", "failed to load sound files");
        }
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

    // Draw everything in here
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        // Draw Particle in background
        particle.draw(canvas);

        // Draw UPS and FPS
        drawUPS(canvas);
        drawFPS(canvas);

        if(gameIsStarted){
            // UI
            drawScore(canvas);
            drawPlayerLivePoint(canvas);
            drawMothershipLivePoint(canvas);

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

            // Only for Debugging
            // debugging(canvas);
            // drawSize(canvas);

            // if Game is Over
            // Draw Game Over Panel
            if(gameIsOver){
                drawGameIsOver(canvas);
                Restartbutton.drawRestartButton(canvas);
            }
        }

        else if(!gameIsStarted){
            drawGameIsStarted(canvas);
            StartButton.drawRestartButton(canvas);
        }
    }

    // Just for debugging
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
        canvas.drawText("Level   : " + currentLevel,textPositionX*18,textPositionY*6,paint);
        drawScreenSize(canvas);
    }
    public void drawScreenSize(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(Color.argb(255,  255, 0, 255));
        paint.setTextSize(textSize);
        canvas.drawText("Screen Size X : " + screenSizeX,textPositionX*6,textPositionY,paint);
        canvas.drawText("Screen Size Y : " + screenSizeY,textPositionX*6,textPositionY*2,paint);
    }
    public void drawSize(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(Color.argb(255,  255, 0, 255));
        paint.setTextSize(textSize);
        canvas.drawText("Mothership X : " + mothership.getFrameX(),textPositionX * 5,textPositionY * 1,paint);
        canvas.drawText("Mothership Y : " + mothership.getFrameY(),textPositionX * 5,textPositionY * 2,paint);
    }

    // Ui
    public void drawUPS(Canvas canvas) {
        String averageUPS = String.format("%.3f",gameLoop.getAverageUPS());
        Paint paint = new Paint();
        paint.setColor(Color.argb(255,  255, 0, 255));
        paint.setTextSize(textSize);
        canvas.drawText("UPS : " + averageUPS,textPositionX*18,textPositionY,paint);
    }
    public void drawFPS(Canvas canvas) {
        String averageFPS = String.format("%.3f",gameLoop.getAverageFPS());
        Paint paint = new Paint();
        paint.setColor(Color.argb(255,  255, 0, 255));
        paint.setTextSize(textSize);
        canvas.drawText("FPS : " + averageFPS,textPositionX*18,textPositionY*2,paint);
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
    public void drawGameIsStarted(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(Color.argb(255,  255, 0, 255));
        paint.setTextSize(textSize * 3);
        canvas.drawText("Push The Button to Start",screenSizeX/10,screenSizeY/2,paint);
    }

    public void startGame(){
        lastEnemyCoolDownSpeed = System.currentTimeMillis();
        lastFireCoolDownTime = System.currentTimeMillis();
        lastMeteorCoolDownSpeed = System.currentTimeMillis();
        lastPowerUpCoolDownSpeed = System.currentTimeMillis();
    }

    // Updating Game in here
    public void update() {
        // Particle will update before game started
        if(!gameIsOver){
            particle.update();
        }

        // Only update is game is not over
        if(!gameIsOver && gameIsStarted){

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

            // Increasing level by time
            increaseLevel();
        }
    }

    // This function is for reset the game
    public void resetTheGame(){
        score = 0;
        player.resetGame();
        mothership.resetGame();

        // Set several object count to 0
        bulletCount = 0;
        enemyAliveCount = 0;
        meteorActiveCount = 0;
        healingCount = 0;
        gatlingCount = 0;

        // Make them InActive
        for(int i = 0; i < bulletValue; i++){
            bullet[i].setActive(false);
        }
        for(int i = 0; i < enemyCount; i++){
            enemy[i].resetUFO();
        }
        for(int i = 0; i < meteorValue; i++){
            meteor[i].getHit();
        }
        for(int i = 0; i < healingValue; i++){
            healing[i].getHit();
        }
        for(int i = 0; i < gatlingValue; i++){
            gatling[i].getHit();
        }

        // Resume The Game
        gameIsOver = false;

        // Reset Game Level
        resetLevel();
    }

    // Make seperate methoc for resetting game level
    public void resetLevel(){
        // Reset all of this value
        spawnEnemyCoolDownSpeed = 2000;
        spawnMeteorCoolDownSpeed = 3000;
        currentLevel = 1;
        enemySpawnValue = 3;
        meteorSpawnValue = 4;
    }

    // Increasing level by Play Time
    public void increaseLevel(){
        long time = System.currentTimeMillis();
        boolean isLevelUp = false;
        if(score >= scoreThreshold){
            scoreThreshold += score;
            // Make Enemy spawn quicker
            if(spawnEnemyCoolDownSpeed > 200){
                spawnEnemyCoolDownSpeed -= 100;
                isLevelUp = true;
            }
            // Make more Enemy spawned
            if(enemySpawnValue < 6){
                enemySpawnValue++;
                isLevelUp = true;
            }
            // Make Meteor spawn quicker
            if(spawnMeteorCoolDownSpeed > 500){
                spawnMeteorCoolDownSpeed -= 250;
                isLevelUp = true;
            }
            // Make more meteor spawned
            if(meteorSpawnValue < 10){
                meteorSpawnValue++;
                isLevelUp = true;
            }
            if(isLevelUp){
                currentLevel++;
            }
        }
    }

    // This function is for Instantiate bullet
    public void fireBullet(){
        long time = System.currentTimeMillis();
        int coolDownTemp;
        if(gatlingIsActive){
            coolDownTemp = fireCoolDownSpeed/4;
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
            soundPool.play(shootID,1,1,0,0,1);
        }
    }

    // This function is for spawning enemy
    public void spawnEnemy(){
        long time = System.currentTimeMillis();
        if(time > lastEnemyCoolDownSpeed + spawnEnemyCoolDownSpeed){
            int randomEnemyValue = new Random().nextInt(enemySpawnValue) + 1;
            for(int i = 0; i< randomEnemyValue; i++) {
                // Enemy loop count increase
                enemyAliveCount++;
                if (enemyAliveCount >= enemyCount) {
                    // Reset loop
                    enemyAliveCount = 0;
                }
                // Instantiate UFO
                int randomY = new Random().nextInt(screenSizeY * 8 / 12) + (screenSizeY * 2 / 12);
                enemy[enemyAliveCount].instantiateUFO(screenSizeX + 5, randomY);
            }
            lastEnemyCoolDownSpeed = System.currentTimeMillis();
        }
    }

    // This function is for spawning meteor
    public void spawnMeteor(){
        long time = System.currentTimeMillis();
        boolean meteorCreated = false;
        if(time > lastMeteorCoolDownSpeed + spawnMeteorCoolDownSpeed){
            int meteorRandomValue = new Random().nextInt(meteorSpawnValue) + 1;
            for(int i = 0; i < meteorRandomValue; i++){
                // Randomize meteor spawn point
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
                // If randomize created meteor
                if(meteorCreated){
                    meteorActiveCount++;
                    if(meteorActiveCount >= meteorValue){
                        meteorActiveCount = 0;
                    }
                }
            }
            lastMeteorCoolDownSpeed = System.currentTimeMillis();
        }
    }

    // This function is for spawning PowerUp
    public void spawnPowerUp(){
        long time = System.currentTimeMillis();
        if(time > lastPowerUpCoolDownSpeed + spawnPowerUpCoolDownSpeed){
            lastPowerUpCoolDownSpeed = time;
            int randomTemp = new Random().nextInt(6);

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

    // This Function is for Checking any collision
    public void collsionCheck(){

        // Check if Player Bullet hit Enemy UFO
        for(int i = 0; i < bulletValue; i++){
            for(int j = 0; j < enemyCount; j++){
                if(bullet[i].getActive() && enemy[j].getActive()){
                    if(RectF.intersects(bullet[i].getRectF(), enemy[j].getRectF()) && enemy[j].getXPosition() < screenSizeX){
                        soundPool.play(hitSoundID,2,2,0,0,1);
                        bullet[i].setActive(false);
                        enemy[j].getHitByBullet();
                        if(!enemy[j].getActive()){
                            explosiveSound();
                        }
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
                    explosiveSound();
                }
            }
        }

        // Check if UFO hit Mothership
        for(int i = 0; i < enemyCount; i++){
            if(enemy[i].getActive()){
                if(RectF.intersects(enemy[i].getRectF(), mothership.getRectF())){
                    enemy[i].getHitByMothership();
                    mothership.getHitByUFO();
                    explosiveSound();
                }
            }
        }

        // Check if Meteor hit player
        for(int i = 0; i < meteorActiveCount; i++){
            if(meteor[i].getActive()){
                if(RectF.intersects(meteor[i].getRectF(), player.getRectF())){
                    meteor[i].getHit();
                    player.getHitByUFO();
                    explosiveSound();
                }
            }
        }

        // Check if Meteor hit Mothership
        for(int i = 0; i < meteorActiveCount; i++){
            if(meteor[i].getActive()){
                if(RectF.intersects(meteor[i].getRectF(), mothership.getRectF())){
                    meteor[i].getHit();
                    explosiveSound();
                }
            }
        }

        // Check if Player Bullet hit Meteor
        for(int i = 0; i < bulletValue; i++){
            for(int j = 0; j < meteorActiveCount; j++){
                if(bullet[i].getActive() && meteor[j].getActive()){
                    if(RectF.intersects(bullet[i].getRectF(), meteor[j].getRectF())){
                        soundPool.play(hitSoundID,1,1,0,0,1);
                        bullet[i].setActive(false);
                    }
                }
            }
        }

        // Check if Player Get Gatling PowerUp
        for(int i = 0; i < gatlingValue; i++){
            if(gatling[i].getActive()){
                if(RectF.intersects(gatling[i].getRectF(), player.getRectF())){
                    soundPool.play(getItemSoundID,1,1,0,0,1);
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
                    soundPool.play(getItemSoundID,1,1,0,0,1);
                    player.addLive();
                    healing[i].getHit();
                }
            }
        }
    }

    // Explosive sound called here
    public void explosiveSound(){
        soundPool.play(destroyedSoundID, 1, 1, 0, 0, 1);
    }

    // Tail Function
}
// End off class
