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
        private Rect[] particleTail;
        private int particleTailSizeX = 5;
        private int particleTailSizeY = 5;
        private int particleTailValue = 10;
        private int adjustmentPositionX = particleSizeX + 1;
        private int adjustmentPositionY = particleSizeY / 2;

        // Activation
        private boolean isActive = false;

        // The colour of particle
        Paint paint;

        public ParticleObject(){
            // Set color
            paint = new Paint();
            paint.setColor(Color.argb(255,  255, 255, 255));

            particleTail = new Rect[particleTailValue];
            for(int i = 0; i < particleTailValue; i++){
                particleTail[i] = new Rect();
            }
        }

        // Spawning particle
        public void spawnParticle(int spawnX, int spawnY){
            particlePositionX = spawnX;
            particlePositionY = spawnY;
            // Left, Top, Right, Buttom
            theParticle = new Rect(
                    spawnX + 0,
                    spawnY +0,
                    spawnX + particleSizeX,
                    spawnY + particleSizeY
            );

            for(int i = 0; i < particleTailValue; i++){
                // Left, Top, Right, Buttom
                particleTail[i].set(
                        spawnX + adjustmentPositionX,
                        spawnY + 0,
                        spawnX + particleTailSizeX + adjustmentPositionX,
                        spawnY + particleTailSizeY + adjustmentPositionY
                );
            }
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

            for(int i = 0; i < particleTailValue; i++){
                particleTail[i].set(
                        particlePositionX + adjustmentPositionX + (particleTailSizeX * i),
                        particlePositionY + 0,
                        particlePositionX + particleTailSizeX + adjustmentPositionX + (particleTailSizeX * i),
                        particlePositionY + particleTailSizeY + adjustmentPositionY
                );
            }

            if(particlePositionX < -15) {
                isActive = false;
            }
        }

        // Draw particle
        public void draw(Canvas canvas){
            canvas.drawRect(theParticle, paint);

            for(int i = 0; i < particleTailValue; i++){
                canvas.drawRect(particleTail[i], paint);
            }
        }

        // Just some get Method
        public boolean getIsActive(){
            return isActive;
        }
    }
    // End off ParticleObject inner class
}
