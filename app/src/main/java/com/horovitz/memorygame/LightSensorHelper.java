package com.horovitz.memorygame;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.SensorEvent;
import android.util.Log;
import android.view.View;

/**
 * Singleton helper class for handling light sensor changes.
 * It updates the background color of the current Activity based on ambient light.
 */
public class LightSensorHelper {

    // 🔁 The one and only instance of this helper
    private static final LightSensorHelper instance = new LightSensorHelper();

    // 🔐 Private constructor prevents other classes from creating instances
    private LightSensorHelper() {}

    // 🌍 Public method to get the single instance
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
        float light = event.values[0]; // 💡 Light level from sensor
        View root = activity.findViewById(android.R.id.content); // 🧱 Root view of the screen

        // 🎨 Update background color based on light intensity
        if (light < 1000) {
            MusicService.pauseMusic();
            Log.d("LIGHT",light+"pauseMusic");
        } else {
            MusicService.resumeMusic();
            Log.d("LIGHT",light+"resumeMusic");
        }
    }
}

