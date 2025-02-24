package com.horovitz.memorygame;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class MusicService extends Service {

    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize media player when service is created
        mediaPlayer = MediaPlayer.create(this, R.raw.memory_game_bg); // You can use your own audio file here (e.g., in res/raw folder)
        mediaPlayer.setLooping(true); // Loop the music
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start(); // Start playing music
        }
        return START_STICKY; // The service will restart if it's killed
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
