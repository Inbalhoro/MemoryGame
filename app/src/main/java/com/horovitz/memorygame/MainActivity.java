package com.horovitz.memorygame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    private Button navigateButton;
    private Button navigateWithButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); ///כפתור הזזה בין מסך למסך בנים בנות

        SharedPreferences sharedPreferences = getSharedPreferences("GameSettings", MODE_PRIVATE);
        String difficulty = sharedPreferences.getString("difficulty", "Regular");  // ברירת מחדל היא "Regular"
        String time = sharedPreferences.getString("selectedTime", "Regular"); // ברירת מחדל: "Regular"
        String theme = sharedPreferences.getString("selectedTheme", "Cartoon Characters"); // ברירת מחדל: "דמויות מצוירות"
        boolean isSoundEnabled = sharedPreferences.getBoolean("isSoundEnabled", true); // ברירת מחדל: true

        Intent serviceIntent = new Intent(MainActivity.this, MusicService.class);
        startService(serviceIntent); // הפעלת מוזיקה ברגע ש-Activity נפתח

        navigateButton = findViewById(R.id.navigateButtonSingle);
        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("Hard".equals(difficulty)) {
                        Intent intent = new Intent(MainActivity.this, MainHardActivity.class);
                        startActivity(intent);
                    }
                    else if("Easy".equals(difficulty)){
                        Intent intent = new Intent(MainActivity.this, MainEasyActivity.class);
                        startActivity(intent);
                    }
                    // דיפולט לעמוד הרגיל MainActivity
                    Intent intent = new Intent(MainActivity.this, MainRegularActivity.class);
                    startActivity(intent);
            }
            // יצירת AlertDialog עם שני כפתורים
//                new AlertDialog.Builder(MainActivity.this)
//                    .setTitle("בחר מסך")
//                        .setMessage("בחר לאן אתה רוצה לעבור:")
//                        .setPositiveButton("למסך בנות", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    if ("Hard".equals(difficulty)) {
//                        Intent intent = new Intent(MainActivity.this, MainHardActivity.class);
//                        startActivity(intent);
//                    }
//                    else if("Easy".equals(difficulty)){
//                        Intent intent = new Intent(MainActivity.this, MainEasyActivity.class);
//                        startActivity(intent);
//                    }
//                    // דיפולט לעמוד הרגיל MainActivity
//                    Intent intent = new Intent(MainActivity.this, MainGirlsActivity.class);
//                    startActivity(intent);
//                }
//            })
//                    .setNegativeButton("למסך בנים", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    // כפתור שני - מעבר למסך אחר
//                    Intent intent = new Intent(MainActivity.this, MainBoysActivity.class);
//                    startActivity(intent);
//                }
//            })
//                    .show();

        });

        navigateWithButton = findViewById(R.id.navigateWithFriendButton);
        navigateWithButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("הזן שמות");

                // יצירת Layout עבור הדיאלוג
                LinearLayout layout = new LinearLayout(MainActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText nameEditText1 = new EditText(MainActivity.this);
                nameEditText1.setHint("הזן את שמך");
                layout.addView(nameEditText1);

                final EditText nameEditText2 = new EditText(MainActivity.this);
                nameEditText2.setHint("הזן את שם החבר");
                layout.addView(nameEditText2);

                builder.setView(layout);

                builder.setMessage("בחר לאן אתה רוצה לעבור:")
                        .setPositiveButton("למסך בנות", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // כפתור אחד - מעבר למסך MainActivity
                                Intent intent = new Intent(MainActivity.this, MainRegularActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("למסך בנים", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // כפתור שני - מעבר למסך אחר
                                Intent intent = new Intent(MainActivity.this, MainBoysActivity.class);
                                startActivity(intent);
                            }
                        });
                builder.show();
            }
        });


    }
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        // עצירת המוזיקה בעת סגירת ה-Activity
//        Intent serviceIntent = new Intent(MainActivity.this, MusicService.class);
//        stopService(serviceIntent);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.action_firstpage){
            Toast.makeText(this, "You selected login", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש
        }
        if (id==R.id.action_settings){
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש
        }
        if (id==R.id.action_start){
            Toast.makeText(this, "You selected start", Toast.LENGTH_SHORT).show();

        }
        return super.onOptionsItemSelected(item);
       }
}
