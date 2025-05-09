package com.horovitz.memorygame;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.content.SharedPreferences;

public class MusicService extends Service {

    private static MusicService instance;
    private MediaPlayer mediaPlayer;
    private boolean isMusicEnabled = true;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        
        // Initialize media player when service is created
        mediaPlayer = MediaPlayer.create(this, R.raw.gamemusic);
        mediaPlayer.setLooping(true);
        
        // Load the saved music state
        SharedPreferences sharedPreferences = getSharedPreferences("GameSettings", MODE_PRIVATE);
        isMusicEnabled = sharedPreferences.getBoolean("isSoundEnabled", true);
        
        if (isMusicEnabled) {
            mediaPlayer.start();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Always check the current state from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("GameSettings", MODE_PRIVATE);
        isMusicEnabled = sharedPreferences.getBoolean("isSoundEnabled", true);
        
        if (mediaPlayer != null && !mediaPlayer.isPlaying() && isMusicEnabled) {
            mediaPlayer.start();
        }
        return START_STICKY;
    }

    public static void setMusicEnabled(boolean enabled) {
        if (instance != null) {
            instance.isMusicEnabled = enabled;
            if (enabled) {
                if (instance.mediaPlayer != null && !instance.mediaPlayer.isPlaying()) {
                    instance.mediaPlayer.start();
                }
            } else {
                if (instance.mediaPlayer != null && instance.mediaPlayer.isPlaying()) {
                    instance.mediaPlayer.pause();
                }
            }
        }
    }

    public static void pauseMusic() {
        if (instance != null && instance.mediaPlayer != null && instance.isMusicEnabled) {
            try {
                if (instance.mediaPlayer.isPlaying()) {
                    instance.mediaPlayer.pause();
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    public static void resumeMusic() {
        if (instance != null && instance.mediaPlayer != null && instance.isMusicEnabled) {
            try {
                if (!instance.mediaPlayer.isPlaying()) {
                    instance.mediaPlayer.start();
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        instance = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
