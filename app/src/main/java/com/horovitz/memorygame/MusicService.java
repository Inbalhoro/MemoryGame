package com.horovitz.memorygame;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class MusicService extends Service {

    private static MusicService instance;
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        // Initialize media player when service is created
        mediaPlayer = MediaPlayer.create(this, R.raw.gamemusic); // You can use your own audio file here (e.g., in res/raw folder)
        mediaPlayer.setLooping(true); // Loop the music
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start(); // Start playing music
        }
        return START_STICKY; // The service will restart if it's killed
    }

    public static void pauseMusic() {
        if (instance != null && instance.mediaPlayer != null) {
            try {
                if (instance.mediaPlayer.isPlaying()) {
                    instance.mediaPlayer.pause();
                }
            } catch (IllegalStateException e) {
                e.printStackTrace(); // אפשר גם לדווח ללוג
            }
        }
    }

    public static void resumeMusic() {
        if (instance != null && instance.mediaPlayer != null) {
            try {
                if (!instance.mediaPlayer.isPlaying()) {
                    instance.mediaPlayer.start();
                }
            } catch (IllegalStateException e) {
                e.printStackTrace(); // אפשר גם לדווח ללוג
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop(); // Stop the music when service is destroyed
            mediaPlayer.release(); // Release the media player resources
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // No binding, as this is a simple background service
    }
}
