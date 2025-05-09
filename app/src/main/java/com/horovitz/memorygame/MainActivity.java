package com.horovitz.memorygame;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.text.style.ImageSpan;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor lightSensor;

    private Button navigateButton,bottomNevigationView;

    private Button navigateWithButton;
    private Button navigateWithCom;
    SharedPreferences sharedPreferences;
    String selectedDifficulty;
    private String selectedImage = ""; // משתנה לשמירת התמונה שנבחרה
    private TextView gameMoneyInDis;
    private int currentgameMoney = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        gameMoneyInDis = findViewById(R.id.gameMoneyFromScores);
        updateScoreDisplay(gameMoneyInDis, currentgameMoney);

//// קבלת הציון המתקבל אם יש
//        Intent intent = getIntent();
//        int newScore = intent.getIntExtra("score", 0);  // אם לא נשלח ציון, ברירת מחדל = 0
//        Log.d("INBA", "NEWmoney " + newScore);
//        Log.d("INBA", "Updated money " + currentgameMoney);
//        // עדכון currentgameMoney עם הציון החדש
//        currentgameMoney += newScore;  // הוספת הציון החדש לציון הקודם
//        Log.d("INBA", "AFTER money " + currentgameMoney);
//
//        // שמירה ב- SharedPreferences
//        SharedPreferences sharedPreferencesM = getSharedPreferences("ScoreToMoney", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferencesM.edit();
//        editor.putInt("currentMoney", currentgameMoney);  // שמירה של הציון המעודכן
//        editor.apply();  // שמירה
//
//        // עדכון ה-TextView עם הציון החדש
//        gameMoneyInDis.setText("" + currentgameMoney);




        // לוגים לעקוב אחרי המידע//        // קריאת הציון שנשמר ב-SharedPreferences
//        SharedPreferences sharedPreferencesM = getSharedPreferences("ScoreToMoney", MODE_PRIVATE);
//        currentgameMoney = sharedPreferencesM.getInt("currentMoney", 0); // ברירת מחדל 0 אם אין ציון
//
//        // עדכון ה-TextView עם הציון
//        gameMoneyInDis.setText("" + currentgameMoney);

        SharedPreferences sharedPreferences = getSharedPreferences("GameSettings", MODE_PRIVATE);



        String time = sharedPreferences.getString("selectedTime", "Regular"); // ברירת מחדל: "Regular"
        String theme = sharedPreferences.getString("selectedTheme", "Cartoon Characters"); // ברירת מחדל: "דמויות מצוירות"
        boolean isSoundEnabled = sharedPreferences.getBoolean("isSoundEnabled", true); // ברירת מחדל: true

        Intent serviceIntentMusic = new Intent(MainActivity.this, MusicService.class);
        startService(serviceIntentMusic); // הפעלת מוזיקה ברגע ש-Activity נפתח

        navigateButton = findViewById(R.id.navigateButtonSingle);
        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SelectedDifficulty", "SelectedDifficulty = " + selectedDifficulty);
                if ("Hard".equals(selectedDifficulty)) {
                    Intent intent = new Intent(MainActivity.this, MainHardActivity.class);
                    startActivity(intent);
                } else if ("Easy".equals(selectedDifficulty)) {
                    Intent intent = new Intent(MainActivity.this, MainEasyActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, MainRegularActivity.class);
                    startActivity(intent);
                }
            }
        });

        navigateWithButton = findViewById(R.id.navigateWithFriendButton);
        navigateWithButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Enter the players's names:");

                // יצירת Layout עבור הדיאלוג
                LinearLayout layout = new LinearLayout(MainActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setGravity(Gravity.CENTER); // הצבת כל התוכן במרכז


                final EditText nameEditText1 = new EditText(MainActivity.this);
                nameEditText1.setHint("Player 1");
                layout.addView(nameEditText1);

                final EditText nameEditText2 = new EditText(MainActivity.this);
                nameEditText2.setHint("Player 2");
                layout.addView(nameEditText2);

                builder.setView(layout);

                TextView title = new TextView(MainActivity.this);
                title.setText("בחר תמונה:");
                title.setGravity(Gravity.CENTER); // ממרכזים את הכותרת
                layout.addView(title);


                // יצירת שני LinearLayouts אופקיים עבור כל שורה
                LinearLayout row1 = new LinearLayout(MainActivity.this);
                row1.setOrientation(LinearLayout.HORIZONTAL); // שורה אחת
                row1.setGravity(Gravity.CENTER); // הצבת התמונות במרכז של השורה
                LinearLayout row2 = new LinearLayout(MainActivity.this);
                row2.setOrientation(LinearLayout.HORIZONTAL); // שורה שנייה
                row2.setGravity(Gravity.CENTER); // הצבת התמונות במרכז של השורה


                ImageView imageView1 = createImageView("1", R.drawable.image1);
                ImageView imageView2 = createImageView("2", R.drawable.animal1);
                ImageView imageView3 = createImageView("3", R.drawable.flag13);
                ImageView imageView4 = createImageView("4", R.drawable.food9);
                row1.addView(imageView1);
                row1.addView(imageView2);
                row2.addView(imageView3);
                row2.addView(imageView4);

                // הוספת השורות ל-layout הראשי
                layout.addView(row1);
                layout.addView(row2);

                builder.setMessage("")
                        .setPositiveButton("Start!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // כפתור אחד - מעבר למסך MainActivity
                                String player1Name = nameEditText1.getText().toString();
                                String player2Name = nameEditText2.getText().toString();

                                // אפשר לוודא שהשמות לא ריקים, או להגדיר ערך ברירת מחדל אם הם ריקים
                                if (player1Name.isEmpty()) {
                                    player1Name = "Player 1"; // שם ברירת מחדל
                                }
                                if (player2Name.isEmpty()) {
                                    player2Name = "Player 2"; // שם ברירת מחדל
                                }

                                Intent intent = new Intent(MainActivity.this, MainPlayWithFriends.class);
                                intent.putExtra("selectedImage", selectedImage); // שולחים את התמונה שנבחרה
                                intent.putExtra("player1Name", player1Name); // שולחים את שם השחקן הראשון
                                intent.putExtra("player2Name", player2Name); // שולחים את שם השחקן השני
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // כפתור שני - מעבר למסך אחר
                                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        });
                builder.show();
            }


        });

        navigateWithCom = findViewById(R.id.navigateWithCom);
        navigateWithCom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, MainComputerActivity.class);
                    startActivity(intent);
                }
            });


        // Connect to the sensor service
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if (sensorManager != null) {
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            if (lightSensor != null) {
                // Start listening to the light sensor
                sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }
    }



    private void updateScoreDisplay(TextView gameMoneyInDis, int currentgameMoney) {
        // Get SharedPreferences
        SharedPreferences prefs = getSharedPreferences("GameData", MODE_PRIVATE);

        // Get the total score and last game score
        int totalScore = prefs.getInt("totalScore", 0);
        int lastGameScore = prefs.getInt("lastGameScore", 0);

        // Update the UI
        gameMoneyInDis.setText("" + totalScore);
    }

    @Override
    protected void onResume() {
        super.onResume();


        // Update the displayed score whenever returning to this activity
        gameMoneyInDis = findViewById(R.id.gameMoneyFromScores);
        updateScoreDisplay(gameMoneyInDis, currentgameMoney);



//        // קבלת הציון המתקבל אם יש
//        Intent intent = getIntent();
//        int newScore = intent.getIntExtra("score", 0);  // אם לא נשלח ציון, ברירת מחדל = 0
//        Log.d("INBA", "NEWmoney " + newScore);
//        Log.d("INBA", "Updated money " + currentgameMoney);
//        // עדכון currentgameMoney עם הציון החדש
//        currentgameMoney += newScore;  // הוספת הציון החדש לציון הקודם
//        Log.d("INBA", "AFTER money " + currentgameMoney);
//
//        // שמירה ב- SharedPreferences
//        SharedPreferences sharedPreferencesM = getSharedPreferences("ScoreToMoney", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferencesM.edit();
//        editor.putInt("currentMoney", currentgameMoney);  // שמירה של הציון המעודכן
//        editor.apply();  // שמירה
//
//        // עדכון ה-TextView עם הציון החדש
//        gameMoneyInDis.setText("" + currentgameMoney);
//
//        // לוגים לעקוב אחרי המידע

    }

    private ImageView createImageView(final String imageName, int imageRes) {
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(imageRes);

        // קביעת גודל אחיד לתמונה
        int size = getResources().getDimensionPixelSize(R.dimen.image_size); // לדוגמה, 100dp
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        imageView.setLayoutParams(params);

        // הגדרת מסגרת סביב התמונה
        imageView.setPadding(10, 10, 10, 10);
        imageView.setBackgroundResource(R.drawable.border); // border הוא קובץ עיצוב שיצור את המסגרת


        // מאזין ללחיצה על התמונה
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // עדכון התמונה שנבחרה
                selectedImage = imageName;
                System.out.println("בחרת את: " + imageName);
            }
        });

        return imageView;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterLightSensorListener(); //של הLIGHTS
        stopMusicService(); //של הMUSICERVICE
    }

    private void stopMusicService() {
        // עצירת המוזיקה בעת סגירת ה-Activity
        Intent serviceIntentMusic = new Intent(MainActivity.this, MusicService.class);
        stopService(serviceIntentMusic);
    }

    /**
     * Unregisters the light sensor listener.
     * Should be called when the activity or service is destroyed to save resources.
     */
    private void unregisterLightSensorListener() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this); // ✅ Stop receiving light sensor updates
        }
    }



    @Override
    protected void onStart() {
        super.onStart();

        // Reading the settings from SharedPreferences
        sharedPreferences = getSharedPreferences("GameSettings", MODE_PRIVATE);
        selectedDifficulty = sharedPreferences.getString("selectedDifficulty", "Regular");

        String theme = sharedPreferences.getString("selectedTheme", "Cartoon Characters"); // ברירת מחדל: "דמויות מצוירות"
        boolean isSoundEnabled = sharedPreferences.getBoolean("isSoundEnabled", true); // ברירת מחדל: true

        Intent serviceIntentMusic = new Intent(MainActivity.this, MusicService.class);
        startService(serviceIntentMusic); // הפעלת מוזיקה ברגע ש-Activity נפתח

    }
    @Override
    protected void onStop() {
        super.onStop();

        // עצור את שירות המוזיקה כשעוזבים את המסך או האפליקציה
        stopService(new Intent(MainActivity.this, MusicService.class)); // עצירת שירות המוזיקה
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //super.onCreateOptionsMenu(menu);  // קריאה למתודה הבסיסית
        getMenuInflater().inflate(R.menu.popupmenu_main, menu);
        GameDatabaseHelper.setIconInMenu(this,
                menu
                ,R.id.action_settings
                ,R.string.setting
                ,R.drawable.baseline_settings_24);
        GameDatabaseHelper.setIconInMenu(this,
                menu
                ,R.id.action_firstpage
                ,R.string.firstpage
                ,R.drawable.baseline_home);
        GameDatabaseHelper.setIconInMenu(this,
                menu
                ,R.id.action_settings
                ,R.string.setting
                ,R.drawable.baseline_settings_24);
        GameDatabaseHelper.setIconInMenu(this,
                menu
                ,R.id.action_shop
                ,R.string.shop
                ,R.drawable.baseline_shopping_cart);
        GameDatabaseHelper.setIconInMenu(this,
                menu
                ,R.id.action_recordBoard
                ,R.string.recordBoard
                ,R.drawable.baseline_record);
        GameDatabaseHelper.setIconInMenu(this,menu
                ,R.id.action_help
                ,R.string.help
                ,R.drawable.baseline_help);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.action_firstpage){
            Toast.makeText(this, "You are already here", Toast.LENGTH_SHORT).show();
        }
        if (id==R.id.action_settings){
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש
        }
        if (id==R.id.action_recordBoard){
            Intent intent = new Intent(MainActivity.this, RecordBoardActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש
        }
        if (id==R.id.action_help){
            Intent intent = new Intent(MainActivity.this, helpActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש
        }
        if (id==R.id.action_shop){
            Intent intent = new Intent(MainActivity.this, MainShop.class);
            startActivity(intent); // התחלת ה-Activity החדש
        }
        if (id==R.id.action_start){
            Intent intent = new Intent(MainActivity.this, MainStart.class);
            startActivity(intent); // התחלת ה-Activity החדש
            Toast.makeText(this, "You pressed RESTART - Please wait a few seconds", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // 💡 Use the helper to update UI based on light level
        LightSensorHelper.getInstance().handleLightChange(event, this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed for light sensor, but required method
    }
}
