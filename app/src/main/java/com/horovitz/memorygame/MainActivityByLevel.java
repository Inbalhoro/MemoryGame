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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivityByLevel extends AppCompatActivity {
    private GameLevel gameLevel;
    private long startTime;
    private long elapsedTime;
    private TextView timerTextView;  // TextView להצגת הזמן הרץ
    private final Handler handler = new Handler();  // Handler לעדכון הזמן
    private boolean isGameRunning = false;  // משתנה לבדוק אם המשחק רץ


    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            // מחשבים את הזמן הנוכחי (שנמסר מאז התחלת המשחק)
            long timeSinceStart = System.currentTimeMillis() - startTime;

            // מעדכנים את ה-TextView עם הזמן החדש
            timerTextView.setText("time: " + (timeSinceStart / 1000));

            // מריצים את הריצה הזו כל 100 מילישניות (0.1 שניה)
            if (isGameRunning) {
                handler.postDelayed(this, 100);  // עדכון כל 100 מילישניות
            }
        }
    };

    private ImageButton[] buttons; // מערך של כפתורים
    private ArrayList<Integer> images = new ArrayList<>(); // תמונות שנמצאות במשחק
    private List<Integer> imageResources; // כאן תוכל להוסיף את התמונות שלך

    private int firstChoice = -1;
    private int secondChoice = -1;
    private int firstChoiceIndex = -1;
    private int secondChoiceIndex = -1;
    private int timeInNumbersS;
    private Button settingsButtonOnlyHere;

    private boolean[] isButtonFlipped; // מעקב אם כפתור כבר נחשף
    private boolean[] isButtonMatched; // מעקב אם הכפתור כבר נמצא בזוג נכון

    private TextView statusText;
    private Button resetButton; // כפתור איפוס

    public List<Integer> generateImageList(List<Integer> allImages, int pairCount) {
        List<Integer> selectedImages = new ArrayList<>();

        for (int i = 0; i < pairCount; i++) {
            int imageRes = allImages.get(i);
            selectedImages.add(imageRes);
            selectedImages.add(imageRes);
        }

        Collections.shuffle(selectedImages);
        return selectedImages;
    }


    private void setupButtons(int count) {
        buttons = new ImageButton[count];
        isButtonFlipped = new boolean[count];
        isButtonMatched = new boolean[count];

        for (int i = 0; i < count; i++) {
            final int index = i; // effectively final copy for use in the inner class

            int resId = getResources().getIdentifier("button_" + i, "id", getPackageName());
            buttons[i] = findViewById(resId);
            buttons[i].setVisibility(View.VISIBLE);
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onButtonClick(index, timeInNumbersS);
                }
            });
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_regular); // קישור ל-XML שלך
        SharedPreferences prefs = getSharedPreferences("GameData", MODE_PRIVATE);
        int selectedBackground = prefs.getInt("selectedBackground", R.drawable.backgroundsingleplayer); // ברירת מחדל אם אין
        // שינוי רקע לפי הבחירה שנשמרה
        View rootLayout = findViewById(R.id.regularGame);
        rootLayout.setBackgroundResource(selectedBackground);


// קריאת ההגדרות מ-SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("GameSettings", MODE_PRIVATE);
        String difficulty = sharedPreferences.getString("difficulty", "Regular");  // ברירת מחדל היא "Regular"
        String time = sharedPreferences.getString("selectedTime", "Regular"); // ברירת מחדל:
        String theme = sharedPreferences.getString("selectedTheme", "Cartoon Characters"); // ברירת מחדל: "דמויות מצוירות"
        boolean isSoundEnabled = sharedPreferences.getBoolean("isSoundEnabled", true); // ברירת מחדל: true


        String levelName = getIntent().getStringExtra("GAME_LEVEL");
        gameLevel = GameLevel.valueOf(levelName); // careful: can throw exception if invalid

        imageResources = getImageResourcesForTheme(Theme.valueOf(theme), gameLevel);  // now returns List<Integer>

        setupButtons(gameLevel.getButtonCount());


        updateGameSettings(difficulty, time, theme, isSoundEnabled);


        timerTextView = findViewById(R.id.timerTextView);

        // אתחול ה-TextView
        statusText = findViewById(R.id.statusText);

        // אתחול כפתור האיפוס
        resetButton = findViewById(R.id.resetButton);
//        settingsButtonOnlyHere = findViewById(R.id.settingsButton);


        // מיקסום התמונות באופן אקראי
        startNewGame(gameLevel);


        // מאזין ללחיצות על כפתורים


        // מאזין לכפתור איפוס
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewGame(gameLevel); // איפוס המשחק
            }
        });
    }

    //TODO
    private List<Integer> getImageResourcesForTheme(Theme theme, GameLevel level) {
        int pairCount = level.getButtonCount() / 2;
        List<Integer> baseImages = new ArrayList<>();

        for (int i = 0; i < pairCount; i++) {
            String prefix = "";

            switch (theme) {
                case CARTOON_CHARACTERS:
                    prefix = "image";
                    break;
                case ANIMALS:
                    prefix = "animal";
                    break;
                case FOOD:
                    prefix = "food";
                    break;
                case FLAGS:
                    prefix = "flag";
                    break;
                default:
                    prefix = "image";
            }

            int resId = getResources().getIdentifier(prefix + (i + 1), "drawable", getPackageName());
            baseImages.add(resId);
        }

        return generateImageList(baseImages, pairCount);
    }


    private void updateGameSettings(String difficultyStr, String timeStr, String themeStr, boolean isSoundEnabled) {
        // Parse enum values from strings (handle casing carefully)
        TimeSetting timeSetting = TimeSetting.valueOf(timeStr.toUpperCase().replace(" ", "_"));
        Theme theme = Theme.valueOf(themeStr.toUpperCase().replace(" ", "_"));

        // Set time
        timeInNumbersS = timeSetting.getSeconds();

        // Set images
        imageResources = getImageResourcesForTheme(theme, gameLevel);  //TODO

        // Handle sound
        if (isSoundEnabled) {
            startMusicService();
        } else {
            stopMusicService();
        }
    }


    private void stopMusicService() {
        Intent serviceIntent = new Intent(this, MusicService.class);
        stopService(serviceIntent); // עוצר את המוזיקה
    }

    private void startMusicService() {
        Intent serviceIntent = new Intent(this, MusicService.class);
        startService(serviceIntent); // מתחיל את המוזיקה
    }

    private void onButtonClick(int index, int timeInNumbersS) {
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
                        //TODO
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

//        for (int i = 0; i < isButtonMatched.length; i++) {
//            if (!isButtonMatched[i]) {
//                allFlipped = false;
//                break;
//            }
//        }


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

        // חישוב הניקוד - נניח ניקוד התחלתי של 1000 נקודות, ונפחית 1 נקודה לכל שנייה
        int baseScore = 500;
        int timePenalty = (int) elapsedTimeInSeconds;
        int score = baseScore - timePenalty; // 100 נקודות לכל זוג שנמצא

        message += "Score: " + score;  // הניקוד

        GameDatabaseHelper dbHelper = new GameDatabaseHelper(this);
        dbHelper.insertGame("The regular game", score, (int) elapsedTime / 1000);


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
//                Log.d("Rinat", "scoreShowD " + score);

                // Get the updated total score
                int updatedTotalScore = getTotalScore();
//                Log.d("Rinat", "currentM " + updatedTotalScore);

                // כפתור חזרה לדף הבית
                Intent intent = new Intent(MainActivityByLevel.this, MainActivity.class);
                startActivity(intent);  // התחלת ה-Activity החדש (חזרה לדף הבית)
            }
        });
        builder.setNegativeButton("Yeah!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveScoreToSharedPreferences(score);

                // כפתור חזרה לדף הבית
                Intent intent = new Intent(MainActivityByLevel.this, MainActivityByLevel.class);  //TODO
                startActivity(intent);  // התחלת ה-Activity החדש (חזרה לדף הבית)
            }
        });
        builder.setNeutralButton("Record board", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveScoreToSharedPreferences(score);

                Intent intent = new Intent(MainActivityByLevel.this, RecordBoardActivity.class);
                startActivity(intent);  // התחלת ה-Activity החדש (חזרה לדף הבית)
            }
        });
        builder.setCancelable(false);  // אם אתה רוצה שהשחקן לא יוכל לדלג על ההודעה לפני שלחץ על כפתור
        builder.create().show();
    }

    private int getTotalScore() {
        SharedPreferences prefs = getSharedPreferences("GameData", MODE_PRIVATE);
        return prefs.getInt("totalScore", Constants.INITIAL_SCORE);
    }

    private void saveScoreToSharedPreferences(int newScore) {
        // Get SharedPreferences instance
        SharedPreferences prefs = getSharedPreferences("GameData", MODE_PRIVATE);

        // Get the current total score
        int currentTotalScore = prefs.getInt("totalScore", Constants.INITIAL_SCORE);

        // Add the new score to the total
        int updatedTotalScore = currentTotalScore + newScore;

        // Save the updated score
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("totalScore", updatedTotalScore);
        editor.putInt("lastGameScore", newScore); // Also save the last game score
        editor.apply();
    }

    private void setclickable(boolean b) {
        for (int i = 0; i < gameLevel.getButtonCount(); i++) {
            buttons[i].setEnabled(b);
        }
    }

    private void startNewGame(GameLevel gameLevel) {
        // אתחול מחדש של כפתורים (מסתיר את התמונות)
        for (int i = 0; i < gameLevel.getButtonCount(); i++) {
            buttons[i].setImageResource(android.R.color.transparent);
            isButtonFlipped[i] = false; // לא נחשף
            isButtonMatched[i] = false; // כפתור לא נמצא בזוג נכון
        }

        startTime = System.currentTimeMillis();  // אתחול זמן ההתחלה (במילישניות)
        isGameRunning = true;  // המשחק רץ

        handler.postDelayed(timerRunnable, 100);  // כל 100 מילישניות

        // אקראי מחדש את התמונות על פי הגדרות
        images.clear();
        images.addAll(imageResources);
        Collections.shuffle(images);

        // Log each image after shuffle with index info
        int tmp = (int)Math.sqrt(gameLevel.getButtonCount());
        for (int row = 0; row < tmp ; row++) {
            StringBuilder rowLog = new StringBuilder();
            for (int col = 0; col < tmp; col++) {
                int index = row * tmp + col;
                rowLog.append(images.get(index)).append("  ");
            }
            Log.d("BOARD", "Row " + row + ": " + rowLog.toString());
        }


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
                , R.id.action_firstpage
                , R.string.firstpage
                , R.drawable.baseline_home);
        GameDatabaseHelper.setIconInMenu(this,
                menu
                , R.id.action_settings
                , R.string.setting
                , R.drawable.baseline_settings_24);
        GameDatabaseHelper.setIconInMenu(this,
                menu
                , R.id.action_shop
                , R.string.shop
                , R.drawable.baseline_shopping_cart);
        GameDatabaseHelper.setIconInMenu(this,
                menu
                , R.id.action_recordBoard
                , R.string.recordBoard
                , R.drawable.baseline_record);
        GameDatabaseHelper.setIconInMenu(this, menu
                , R.id.action_help
                , R.string.help
                , R.drawable.baseline_help);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_firstpage) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש
        }
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש
        }
        if (id == R.id.action_recordBoard) {
            Intent intent = new Intent(this, RecordBoardActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש
        }
        if (id == R.id.action_help) {
            Intent intent = new Intent(this, helpActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש
        }
        if (id == R.id.action_shop) {
            Intent intent = new Intent(this, MainShop.class);
            startActivity(intent); // התחלת ה-Activity החדש
        }
        if (id == R.id.action_start) {
            Intent intent = new Intent(this, MainStart.class);
            startActivity(intent); // התחלת ה-Activity החדש
            Toast.makeText(this, "You pressed RESTART - Please wait a few seconds", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
