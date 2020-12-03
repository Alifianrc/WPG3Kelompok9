package com.example.wpg3kelompok9;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.Random;

public class Particle {
    // This class will spawn Particle
    private int screenSizeX;
    private int screenSizeY;
    private ParticleObject[] particleObject;
    private long lastTimeParticleSpawn = 0;
    private int particleSpawnCooldownTime = 100;
    private int particleCount = 0;
    private int particleValue = 200;

    public Particle(int screenX, int screenY){
        screenSizeX = screenX;
        screenSizeY = screenY;

        particleObject = new ParticleObject[particleValue];
        for(int i = 0; i < particleValue; i++){
            particleObject[i] = new ParticleObject();
        }
    }

    public void update(){
        // Spawn here
        long time = System.currentTimeMillis();
        if(time > lastTimeParticleSpawn + particleSpawnCooldownTime){
            int randomSpawnParticleValue = new Random().nextInt(9) + 1;
            int randomSpawnY = new Random().nextInt(screenSizeY - 1) + 1;
            for(int i = 0; i < randomSpawnParticleValue; i++){
                particleObject[particleCount].spawnParticle(screenSizeX + 1, randomSpawnY);
                particleCount++;

                // Reset counter
                if(particleCount >= particleValue){
                    particleCount = 0;
                }
            }
            lastTimeParticleSpawn = time;
        }


        for(int i = 0; i < particleValue; i++){
            if(particleObject[i].getIsActive()){
                particleObject[i].particleMove();
            }
        }
    }

    public void draw(Canvas canvas){
        for(int i = 0; i < particleValue; i++){
            if(particleObject[i].getIsActive()){
                particleObject[i].draw(canvas);
            }
        }
    }

    // i Made and Inner class for Particle Obeject
    class ParticleObject{
        // The particle it self
        private Rect theParticle;
        // The particle position
        private int particlePositionX;
        private int particlePositionY;
        // The size of particle
        private int particleSizeX = 10;
        private int particleSizeY = 10;
        // The speed of particle
        private static final double SPEED_PIXEL_PER_SECOND = 1000;
        private static final double MAX_SPEED = SPEED_PIXEL_PER_SECOND / GameLoop.MAX_UPS * -1;

        // The tail of particle
        private Rect particleTail;
        private int particleTailSizeX = 2;
        private int particleTailSizeY = 2;
        private int adjustmentPositionX = particleSizeX + 1;
        private int adjustmentPositionY = particleSizeY / 2;

        // Activation
        private boolean isActive = false;

        // The colour of particle
        Paint paint;

        public ParticleObject(){
            // Resize particle based on screen size
            particleSizeX = screenSizeX/192;
            particleSizeY = screenSizeY/108;
            particleTailSizeY = screenSizeY/540;

            // Initialize color
            paint = new Paint();
            paint.setColor(Color.argb(255,  255, 255, 255));
        }

        public void randomColor(){
            int random = new Random().nextInt(5);
            if(random == 1){
                // Purple
                paint.setColor(Color.argb(255,  240, 62, 246));
            }
            else if (random == 2){
                // Green
                paint.setColor(Color.argb(255,  129, 236, 189));
            }
            else{
                // White
                paint.setColor(Color.argb(255,  255, 255, 255));
            }
        }

        public void randomTailLong(){
            int random = new Random().nextInt(screenSizeX/13) + screenSizeX/38;
            particleTailSizeX = random;
        }

        // Spawning particle
        public void spawnParticle(int spawnX, int spawnY){
            // Randomize color
            //randomColor();

            // Set particle spawn point
            particlePositionX = spawnX;
            particlePositionY = spawnY;
            // Left, Top, Right, Buttom
            theParticle = new Rect(
                    spawnX + 0,
                    spawnY +0,
                    spawnX + particleSizeX,
                    spawnY + particleSizeY
            );

            // Randomize tail long
            randomTailLong();
            // Set the tail
            particleTail = new Rect(
                    spawnX + adjustmentPositionX,
                    spawnY + adjustmentPositionY,
                    spawnX + particleTailSizeX + adjustmentPositionX,
                    spawnY + particleTailSizeY + adjustmentPositionY
            );

            // Set to active
            isActive = true;
        }

        // Same as Update
        public void particleMove() {
            particlePositionX += MAX_SPEED;

            theParticle.set(
                    particlePositionX + 0,
                    particlePositionY + 0,
                    particlePositionX + particleSizeX,
                    particlePositionY + particleSizeY
            );

            particleTail.set(
                    particlePositionX + adjustmentPositionX,
                    particlePositionY + adjustmentPositionY,
                    particlePositionX + particleTailSizeX + adjustmentPositionX,
                    particlePositionY + particleTailSizeY + adjustmentPositionY
            );

            if(particlePositionX < -200) {
                isActive = false;
            }
        }

        // Draw particle
        public void draw(Canvas canvas){
            canvas.drawRect(theParticle, paint);
            canvas.drawRect(particleTail, paint);
        }

        // Just some get Method
        public boolean getIsActive(){
            return isActive;
        }
    }
    // End off ParticleObject inner class
}
