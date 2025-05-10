package com.horovitz.memorygame;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.SensorEvent;
import android.util.Log;
import android.view.View;
import android.content.SharedPreferences;

/**
 * Singleton helper class for handling light sensor changes.
 * It updates the background color of the current Activity based on ambient light.
 */
public class LightSensorHelper {

    //  The one and only instance of this helper
    private static final LightSensorHelper instance = new LightSensorHelper();

    //  Private constructor prevents other classes from creating instances
    private LightSensorHelper() {}

    //  Public method to get the single instance
    public static LightSensorHelper getInstance() {
        return instance;
    }

    /**
     * Handles light sensor data and updates UI based on light level.
     *
     * @param event    The sensor event containing light level data.
     * @param activity The current activity whose background will be changed.
     */
    public void handleLightChange(SensorEvent event, Activity activity) {
        float light = event.values[0];
        View root = activity.findViewById(android.R.id.content);
        
        // Only control music if it's enabled in settings
        SharedPreferences sharedPreferences = activity.getSharedPreferences("GameSettings", MODE_PRIVATE);
        boolean isMusicEnabled = sharedPreferences.getBoolean("isSoundEnabled", true);
        
        if (isMusicEnabled) {
            if (light < 100) {
                MusicService.pauseMusic();
                Log.d("LIGHT", light + " pauseMusic");
            } else {
                MusicService.resumeMusic();
                Log.d("LIGHT", light + " resumeMusic");
            }
        }
    }
}

