package com.main.lutemon.android;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.main.lutemon.LutemonGame;

public class AndroidLauncher extends AndroidApplication {
    private static final String TAG = "LutemonLauncher";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            // First, ensure native libraries are loaded
            Log.i(TAG, "Loading native libraries...");
            try {
                System.loadLibrary("gdx");
                Log.i(TAG, "Successfully loaded libgdx.so");
            } catch (UnsatisfiedLinkError e) {
                Log.e(TAG, "Failed to load native libraries manually: " + e.getMessage());
                // Let GDX try to load it itself
                try {
                    GdxNativesLoader.load();
                    Log.i(TAG, "Successfully loaded natives via GdxNativesLoader");
                } catch (Exception ex) {
                    Log.e(TAG, "GdxNativesLoader also failed: " + ex.getMessage());
                    // Continue anyway - sometimes the libraries might be found later
                }
            }

            // Configure the application
            AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
            config.useAccelerometer = false;
            config.useCompass = false;
            config.useGyroscope = false;
            config.useRotationVectorSensor = false;
            config.useWakelock = true; // Prevent screen from going to sleep

            // Initialize the game
            Log.i(TAG, "Initializing Lutemon game...");
            initialize(new LutemonGame(), config);
            Log.i(TAG, "Game initialized successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error initializing game: " + e.getMessage(), e);
            if (e.getCause() != null) {
                Log.e(TAG, "Caused by: " + e.getCause().getMessage());
            }

            // Try to show an error message to the user
            runOnUiThread(() -> Toast.makeText(this, "Error starting game", Toast.LENGTH_LONG).show());

            // Continue with a minimal configuration as a last resort
            try {
                AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
                initialize(new LutemonGame(), config);
            } catch (Exception ex) {
                Log.e(TAG, "Fatal error, could not recover: " + ex.getMessage(), ex);
                finish(); // Close the app as a last resort
            }
        }
    }
}
