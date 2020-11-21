package com.example.wpg3kelompok9;

import android.graphics.Canvas;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.util.TimerTask;
import java.util.logging.Handler;

/**
 *
 */

public class GameLoop extends Thread {
    private Game game;
    private boolean isRunning = false;
    private SurfaceHolder surfaceHolder;
    private double averageUPS;
    private double averageFPS;
    public static final double MAX_UPS = 60.0;
    private static final double UPS_PERIOD = 1E+3/MAX_UPS;

    public GameLoop(Game game, SurfaceHolder surfaceHolder) {
        this.game = game;
        this.surfaceHolder = surfaceHolder;

    }

    public double getAverageUPS() {


        return averageUPS;
    }

    public double getAverageFPS() {


        return averageFPS;
    }

    public void startLoop() {
        isRunning = true;
        start();
    }

    @Override
    public void run() {
        super.run();

        // Declare time and cycle count variable
        int updateCount = 0;
        int frameCount = 0;

        long startTime;
        long elapsedTime;
        long sleepTime;

        // Game Loop
        Canvas canvas = null;
        startTime = System.currentTimeMillis();
        while (isRunning){

            // Try to update render game
            try {
                canvas = surfaceHolder.lockCanvas();
                synchronized (surfaceHolder){
                    game.update();
                    updateCount++;

                    game.draw(canvas);
                }

            } catch (IllegalArgumentException e){
                e.printStackTrace();
            } finally {
                if(canvas != null){
                    try{
                        surfaceHolder.unlockCanvasAndPost(canvas);
                        frameCount++;
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            // Pause game Loop to not exceed target UPS
            elapsedTime = System.currentTimeMillis() - startTime;
            sleepTime = (long) (updateCount * UPS_PERIOD - elapsedTime);
            if(sleepTime > 0){
                try {
                    sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Skip frame to keep up with target UPS
            while(sleepTime < 0 && updateCount < MAX_UPS - 1){
                game.update();
                updateCount++;
                elapsedTime = System.currentTimeMillis() - startTime;
                sleepTime = (long) (updateCount * UPS_PERIOD - elapsedTime);
            }

            // Calculate average UPS and FPS
            elapsedTime = System.currentTimeMillis() - startTime;
            if(elapsedTime >= 1000){
                averageUPS = updateCount / (1E-3 * elapsedTime);
                averageFPS = frameCount / (1E-3 * elapsedTime);
                updateCount = 0;
                frameCount = 0;
                startTime = System.currentTimeMillis();
            }

        }
    }
    // End of run method


























}
