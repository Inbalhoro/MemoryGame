package com.horovitz.memorygame;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import android.view.Menu;
import android.view.MenuItem;

import android.os.CountDownTimer;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainStart extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView timeLeftText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_start);

        progressBar = findViewById(R.id.progressBar);

        new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // חישוב ההתקדמות והגדרת הערכים
                int progress = (int) ((5000 - millisUntilFinished) / 50); // חישוב התקדמות בפרוגרס בר
                progressBar.setProgress(progress);

            }

            @Override
            public void onFinish() {
                // אחרי שהטיימר נגמר, נעבור למסך השני
                Intent intent = new Intent(MainStart.this, MainActivity.class);
                startActivity(intent);
            }
        }.start();

    }
}