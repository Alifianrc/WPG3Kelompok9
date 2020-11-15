package com.example.wpg3kelompok9;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;


public class MainActivity extends Activity {

    Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get a Display object to access screen details
        Display display = getWindowManager().getDefaultDisplay();
        // Load the resolution into a Point object
        Point size = new Point();
        display.getSize(size);

        // Save screen size
        int screenSizeX = size.x;
        int screenSizeY = size.y;

        game = new Game(this, screenSizeX, screenSizeY);

        // Set content view to game, so that object in
        // The game class can be rendered to the screen
        setContentView(game);
    }

























}