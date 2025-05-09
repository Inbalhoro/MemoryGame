package com.horovitz.memorygame;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;

public class MainEasyActivity extends AppCompatActivity {
    private long startTime;
    private long elapsedTime;

    private TextView timerTextView;  // TextView להצגת הזמן הרץ
    private final Handler handler = new Handler();  // Handler לעדכון הזמן
    private boolean isGameRunning = false;  // משתנה לבדוק אם המשחק רץ
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            // מחשבים את הזמן הנוכחי (שנמסר מאז התחלת המשחק)
            long elapsedTime = System.currentTimeMillis() - startTime;

            // מעדכנים את ה-TextView עם הזמן החדש
            timerTextView.setText("time: " + (elapsedTime / 1000) );

            // מריצים את הריצה הזו כל 100 מילישניות (0.1 שניה)
            if (isGameRunning) {
                handler.postDelayed(this, 100);  // עדכון כל 100 מילישניות
            }
        }
    };

    private ImageButton[] buttons = new ImageButton[6]; // מערך של כפתורים
    private ArrayList<Integer> images = new ArrayList<>(); // תמונות שנמצאות במשחק
    private int[] imageResources = {R.drawable.image1, R.drawable.image1, R.drawable.image2, R.drawable.image2,
            R.drawable.image3, R.drawable.image3, }; // כאן תוכל להוסיף את התמונות שלך

    private int firstChoice = -1;
    private int secondChoice = -1;
    private int firstChoiceIndex = -1;
    private int secondChoiceIndex = -1;
    private int timeInNumbersS;
    private TextView currentMoneyTextView;
    private int currentMoney = 0;


    private boolean[] isButtonFlipped = new boolean[6]; // מעקב אם כפתור כבר נחשף
    private boolean[] isButtonMatched = new boolean[6]; // מעקב אם הכפתור כבר נמצא בזוג נכון

    private TextView statusText;
    private Button resetButton; // כפתור איפוס

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_easy); // קישור ל-XML שלך

//        popupmenu();
// קריאת ההגדרות מ-SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("GameSettings", MODE_PRIVATE);
        String difficulty = sharedPreferences.getString("difficulty", "Regular");  // ברירת מחדל היא "Easy"

        String time = sharedPreferences.getString("selectedTime", "Regular"); // ברירת מחדל: "5 שניות"
        String theme = sharedPreferences.getString("selectedTheme", "Cartoon Characters"); // ברירת מחדל: "דמויות מצוירות"
        boolean isSoundEnabled = sharedPreferences.getBoolean("isSoundEnabled", true); // ברירת מחדל: true

        updateGameSettings(difficulty, time, theme, isSoundEnabled);

        // אתחול כפתורים
        buttons[0] = findViewById(R.id.button_0);
        buttons[1] = findViewById(R.id.button_1);
        buttons[2] = findViewById(R.id.button_2);
        buttons[3] = findViewById(R.id.button_3);
        buttons[4] = findViewById(R.id.button_4);
        buttons[5] = findViewById(R.id.button_5);
        timerTextView = findViewById(R.id.timerTextView);

        // אתחול ה-TextView
        statusText = findViewById(R.id.statusText);

        // אתחול כפתור האיפוס
        resetButton = findViewById(R.id.resetButton);

        // מיקסום התמונות באופן אקראי
        startNewGame();

        // מאזין ללחיצות על כפתורים
        for (int i = 0; i < 6; i++) {
            final int index = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onButtonClick(index,timeInNumbersS);
                }
            });
        }

        // מאזין לכפתור איפוס
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewGame(); // איפוס המשחק
            }
        });
    }


    private void updateGameSettings(String difficulty, String time, String theme, boolean isSoundEnabled) {
        if (time.equals("Short")) {
            timeInNumbersS = 300;  // זמן בשניות
        } else if (time.equals("Regular")) {
            timeInNumbersS = 700;
        } else if (time.equals("Long")) {
            timeInNumbersS = 1000;
        }
        // עדכון נושא
        if (theme.equals("Cartoon Characters")) {
            // להשתמש בתמונות של חיות
            imageResources = new int [] {R.drawable.image1, R.drawable.image1, R.drawable.image2, R.drawable.image2,
                    R.drawable.image3, R.drawable.image3};
        } else if (theme.equals("Animals")) {
            // להשתמש בתמונות של דמויות מצוירות
            imageResources = new int[] {R.drawable.animal1, R.drawable.animal1, R.drawable.animal2, R.drawable.animal2,
                    R.drawable.animal3, R.drawable.animal3};
        }
        else if (theme.equals("Food")) {
            // להשתמש בתמונות של דמויות מצוירות
            imageResources = new int[] {R.drawable.food4, R.drawable.food4, R.drawable.food2, R.drawable.food2,
                    R.drawable.food3, R.drawable.food3};
        }else if (theme.equals("Flags")) {
            // להשתמש בתמונות של דמויות מצוירות
            imageResources = new int[] {R.drawable.flag7, R.drawable.flag7, R.drawable.flag2, R.drawable.flag2,
                    R.drawable.flag3, R.drawable.flag3};
        }

        // אם צלילים מופעלים, תתחיל את המוזיקה, אחרת תפסיק אותה
        if (isSoundEnabled) {
            startMusicService();
        } else {
            stopMusicService();
        }
    }

    private void stopMusicService() {
        Intent serviceIntent = new Intent(MainEasyActivity.this, MusicService.class);
        stopService(serviceIntent); // עוצר את המוזיקה
    }

    private void startMusicService() {
        Intent serviceIntent = new Intent(MainEasyActivity.this, MusicService.class);
        startService(serviceIntent); // מתחיל את המוזיקה
    }

    private void onButtonClick(int index,int timeInNumbersS) {
        // אם הכפתור כבר נמצא בזוג נכון, אל תאפשר ללחוץ עליו
        if (isButtonMatched[index]) {
            return;
        }
        if (index == firstChoiceIndex && firstChoice != -1) {
            return;  // יציאה מהפונקציה אם הכפתור שנבחר כבר נבחר קודם
        }
        // הצגת התמונה בלחיצה
        buttons[index].setImageResource(images.get(index));
        isButtonFlipped[index] = true;

        Log.d("Rinat", "onButtonClick firstChoice = " + firstChoice);
        // אם זו הבחירה הראשונה
        if (firstChoice == -1) {
            firstChoice = images.get(index);
            firstChoiceIndex = index;
        }
        // אם זו הבחירה השנייה
        else {
            setclickable(false);
            secondChoice = images.get(index);
            secondChoiceIndex = index;

            //stop

            // אם התמונות תואמות
            if (firstChoice == secondChoice) {
                statusText.setText("It's a match!");
                isButtonMatched[firstChoiceIndex] = true; // הצבת הכפתור הראשון ככפתור תואם
                isButtonMatched[secondChoiceIndex] = true; // הצבת הכפתור השני ככפתור תואם
                resetChoices();
                setclickable(true);
            }

            // אם התמונות לא תואמות
            else {
                statusText.setText("Try again");
                // השהה את הצגת התמונות למספר שניות, ואז החבא אותן
                buttons[firstChoiceIndex].postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Rinat", "run firstChoice = " + firstChoice);
                        // החבא את התמונה הראשונה אם לא תואמת
                        buttons[firstChoiceIndex].setImageResource(android.R.color.transparent);
                        // החבא את התמונה השנייה אם לא תואמת
                        buttons[secondChoiceIndex].setImageResource(android.R.color.transparent);
                        setclickable(true);
                        resetChoices(); // אתחול הבחירות
                    }
                }, timeInNumbersS); // השהייה של שנייה לפני החבאת התמונות
            }
        }
    }

    private void resetChoices() {
        Log.d("Rinat","resetChoices firstChoice = -1");
        firstChoice = -1;
        secondChoice = -1;
        firstChoiceIndex = -1;
        secondChoiceIndex = -1;

        //start

        // בדוק אם כל הכפתורים נחשפו
        boolean allFlipped = true;
        for (boolean matched : isButtonMatched) {
            if (!matched) {
                allFlipped = false;
                break;
            }
        }

        if (allFlipped) {
            elapsedTime = System.currentTimeMillis() - startTime;  // זמן שלקח לסיים את המשחק
            statusText.setText("Game over!");
            showTimeDialog();
            isGameRunning = false;  // עצור את זמן הריצה
            handler.removeCallbacks(timerRunnable);  // הסר את הריצה של עדכון הזמן
        }
    }

    private void showTimeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        SpannableString title = new SpannableString("Game over - Well done!");
        title.setSpan(new AbsoluteSizeSpan(24, true), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // שינוי גודל לכותרת הראשונה ל-36sp
        builder.setTitle(title);

        SpannableString subTitle = new SpannableString("You succeeded to reveal all couples");
        subTitle.setSpan(new AbsoluteSizeSpan(16, true), 0, subTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // שינוי גודל לכותרת המשנה ל-24sp
        builder.setMessage(subTitle);


        // הצגת הזמן והניקוד בשתי שורות
        String message = "Time: " + (elapsedTime / 1000) + " s\n";  // זמן בשניות

        long elapsedTimeInSeconds = (elapsedTime / 1000);

        // חישוב הניקוד - נניח ניקוד התחלתי של 200 נקודות, ונפחית 1 נקודה לכל שנייה
        int baseScore = 200;
        int timePenalty = (int) elapsedTimeInSeconds;
        int score = baseScore - timePenalty; // 100 נקודות לכל זוג שנמצא

        message += "Score: " +score;  // הניקוד

        Log.d("Rinat", "score " + score);

        GameDatabaseHelper dbHelper = new GameDatabaseHelper(this);
        dbHelper.insertGame("The easy game",score, (int) elapsedTime / 1000);


// יצירת TextView עם טקסט מותאם אישית
        TextView messageTextView = new TextView(this);
        messageTextView.setText(message);
        messageTextView.setGravity(Gravity.CENTER);  // יישור טקסט למרכז
        messageTextView.setTextSize(20);  // שינוי גודל טקסט לניקוד ולזמן

        builder.setView(messageTextView);  // הגדרת TextView כצפייה בהודעה


        builder.setPositiveButton("Home page", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Save the score using SharedPreferences
                saveScoreToSharedPreferences(score);

                // Log for debugging
                Log.d("Rinat", "scoreShowD " + score);

                // Get the updated total score
                int updatedTotalScore = getTotalScore();
                Log.d("Rinat", "currentM " + updatedTotalScore);

                // Return to MainActivity
                Intent intent = new Intent(MainEasyActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });
        builder.setNegativeButton("Yeah!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                saveScoreToSharedPreferences(score);

                // Restart the game activity
                Intent intent = new Intent(MainEasyActivity.this, MainEasyActivity.class);
                startActivity(intent);
            }
        });
        builder.setNeutralButton("Record board", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                saveScoreToSharedPreferences(score);

                Intent intent = new Intent(MainEasyActivity.this, RecordBoardActivity.class);
                startActivity(intent);  // התחלת ה-Activity החדש (חזרה לדף הבית)
            }
        });
        builder.setCancelable(false);  // אם אתה רוצה שהשחקן לא יוכל לדלג על ההודעה לפני שלחץ על כפתור
//        builder.show();

        builder.create().show();

    }

    private int getTotalScore() {
        SharedPreferences prefs = getSharedPreferences("GameData", MODE_PRIVATE);
        return prefs.getInt("totalScore", 0);
    }

    private void saveScoreToSharedPreferences(int newScore) {
        // Get SharedPreferences instance
        SharedPreferences prefs = getSharedPreferences("GameData", MODE_PRIVATE);

        // Get the current total score
        int currentTotalScore = prefs.getInt("totalScore", 0);

        // Add the new score to the total
        int updatedTotalScore = currentTotalScore + newScore;

        // Save the updated score
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("totalScore", updatedTotalScore);
        editor.putInt("lastGameScore", newScore); // Also save the last game score
        editor.apply();
    }

    private void setclickable(boolean b) {
        for (int i = 0; i < 6; i++) {
            buttons[i].setEnabled(b);
        }
    }

    private void startNewGame() {
        // אתחול מחדש של כפתורים (מסתיר את התמונות)
        for (int i = 0; i < 6; i++) {
            buttons[i].setImageResource(android.R.color.transparent);
            isButtonFlipped[i] = false; // לא נחשף
            isButtonMatched[i] = false; // כפתור לא נמצא בזוג נכון
        }

        startTime = System.currentTimeMillis();  // אתחול זמן ההתחלה (במילישניות)
        isGameRunning = true;  // המשחק רץ

        handler.postDelayed(timerRunnable, 100);  // כל 100 מילישניות

        // אקראי מחדש את התמונות על פי הגדרות
        images.clear();
        for (int i = 0; i < imageResources.length; i++) {
            images.add(imageResources[i]);
        }
        Collections.shuffle(images);

        // איפוס משתנים
        firstChoice = -1;
        secondChoice = -1;
        firstChoiceIndex = -1;
        secondChoiceIndex = -1;

        // עדכון טקסט סטטוס
        statusText.setText("start!");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.popupmenu_main, menu);

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
            Intent intent = new Intent(MainEasyActivity.this, MainActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש
        }
        if (id==R.id.action_settings){
            Intent intent = new Intent(MainEasyActivity.this, SettingsActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש
        }
        if (id==R.id.action_recordBoard){
            Intent intent = new Intent(MainEasyActivity.this, RecordBoardActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש
        }
        if (id==R.id.action_help){
            Intent intent = new Intent(MainEasyActivity.this, helpActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש
        }
        if (id==R.id.action_shop){
            Intent intent = new Intent(MainEasyActivity.this, MainShop.class);
            startActivity(intent); // התחלת ה-Activity החדש
        }
        if (id==R.id.action_start){
            Intent intent = new Intent(MainEasyActivity.this, MainStart.class);
            startActivity(intent); // התחלת ה-Activity החדש
            Toast.makeText(this, "You pressed RESTART - Please wait a few seconds", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}