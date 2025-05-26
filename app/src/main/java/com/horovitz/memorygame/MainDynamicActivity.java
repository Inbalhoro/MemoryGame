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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainDynamicActivity extends AppCompatActivity {
    private GameLevel gameLevel;
    private long startTime;
    private String nameOfLevel;
    private long elapsedTime;
    private final Handler handler = new Handler();
    private boolean isGameRunning = false;
    private TextView timerTextView;
    private Button resetButton;
    private TextView statusText;
    private ImageButton[] buttons;
    private ArrayList<Integer> images = new ArrayList<>();
    private List<Integer> imageResources;
    private boolean[] isButtonFlipped;
    private boolean[] isButtonMatched;
    private int firstChoice = -1, secondChoice = -1;
    private int firstChoiceIndex = -1, secondChoiceIndex = -1;
    private int timeInNumbersS;

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long timeSinceStart = System.currentTimeMillis() - startTime;
            timerTextView.setText("Time: " + (timeSinceStart / 1000));
            if (isGameRunning) {
                handler.postDelayed(this, 1000); // Delay every 1 second
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dynamic);

        loadPreferences();
        initializeViews();
        initializeGame();
    }

    private void loadPreferences() {
        SharedPreferences prefs = getSharedPreferences("GameData", MODE_PRIVATE);
        int selectedBackground = prefs.getInt("selectedBackground", R.drawable.backgroundsingleplayer);
        findViewById(R.id.dynamicGame).setBackgroundResource(selectedBackground);

        SharedPreferences sharedPreferences = getSharedPreferences("GameSettings", MODE_PRIVATE);
        String difficulty = sharedPreferences.getString("difficulty", "REGULAR");
        String time = sharedPreferences.getString("selectedTime", "REGULAR");
        String theme = sharedPreferences.getString("selectedTheme", "CARTOON_CHARACTERS");
        boolean isSoundEnabled = sharedPreferences.getBoolean("isSoundEnabled", true);

        String levelName = getIntent().getStringExtra("GAME_LEVEL");
        gameLevel = GameLevel.valueOf(levelName);

        generateButtonsDynamically(gameLevel.getButtonCount());
        imageResources = getImageResourcesForTheme(Theme.valueOf(theme), gameLevel);
        updateGameSettings(difficulty, time, theme, isSoundEnabled);
    }

    private void initializeViews() {
        timerTextView = findViewById(R.id.timerTextView);
        statusText = findViewById(R.id.statusText);
        resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(v -> startNewGame(gameLevel));
    }

    private void initializeGame() {
        startNewGame(gameLevel);
    }

    private void generateButtonsDynamically(int totalButtons) {
        LinearLayout parentLayout = findViewById(R.id.llDynamicButtons);
        parentLayout.removeAllViews();

        int columns = (int) Math.sqrt(totalButtons);
        int rows = (int) Math.ceil((double) totalButtons / columns);

        buttons = new ImageButton[totalButtons];
        isButtonFlipped = new boolean[totalButtons];
        isButtonMatched = new boolean[totalButtons];

        int index = 0;

        for (int row = 0; row < rows; row++) {
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setGravity(Gravity.CENTER);
            rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            for (int col = 0; col < columns && index < totalButtons; col++) {
                ImageButton button = new ImageButton(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        0, dpToPx(74));
                params.weight = 1;
                params.setMargins(dpToPx(6), dpToPx(6), dpToPx(6), dpToPx(6));
                button.setLayoutParams(params);
                button.setImageResource(android.R.color.transparent);
                button.setBackgroundResource(R.drawable.white);
                button.setScaleType(ImageView.ScaleType.CENTER_CROP);
                button.setContentDescription("button_" + index);
                button.setElevation(dpToPx(4));

                final int finalIndex = index;
                button.setOnClickListener(v -> onButtonClick(finalIndex, timeInNumbersS));

                rowLayout.addView(button);
                buttons[index] = button;
                index++;
            }

            parentLayout.addView(rowLayout);
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
    private void updateGameSettings(String difficultyStr, String timeStr, String themeStr, boolean isSoundEnabled) {
        if(difficultyStr=="HARD"){
            nameOfLevel = "The hard game";
        }
        else if(difficultyStr=="EASY"){
            nameOfLevel = "The easy game";
        }
        else{
            nameOfLevel = "The regular game";
        }
        TimeSetting timeSetting = TimeSetting.valueOf(timeStr.toUpperCase().replace(" ", "_"));
        Theme theme;
        try {
            theme = Theme.valueOf(themeStr.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException | NullPointerException e) {
            theme = Theme.CARTOON_CHARACTERS; // ברירת מחדל
        }
        timeInNumbersS = timeSetting.getSeconds();
        imageResources = getImageResourcesForTheme(theme, gameLevel);
        if (isSoundEnabled) startMusicService();
        else stopMusicService();
    }


    private void startMusicService() {
        startService(new Intent(this, MusicService.class));
    }

    private void stopMusicService() {
        stopService(new Intent(this, MusicService.class));
    }

    private List<Integer> getImageResourcesForTheme(Theme theme, GameLevel level) {
        int pairCount = level.getButtonCount() / 2;
        List<Integer> baseImages = new ArrayList<>();
        for (int i = 0; i < pairCount; i++) {
            int resId = getResources().getIdentifier(theme.getPrefix() + (i + 1), "drawable", getPackageName());
            baseImages.add(resId);
        }
        return generateImageList(baseImages, pairCount);
    }

    public List<Integer> generateImageList(List<Integer> allImages, int pairCount) {
        List<Integer> selectedImages = new ArrayList<>();
        for (int i = 0; i < pairCount; i++) {
            selectedImages.add(allImages.get(i));
            selectedImages.add(allImages.get(i));
        }
        Collections.shuffle(selectedImages);
        return selectedImages;
    }

    private void onButtonClick(int index, int timeInMilliseconds) {
        if (isButtonMatched[index] || index == firstChoiceIndex && firstChoice != -1) return;
        buttons[index].setImageResource(images.get(index));
        isButtonFlipped[index] = true;

        if (firstChoice == -1) {
            firstChoice = images.get(index);
            firstChoiceIndex = index;
        } else {
            setButtonsEnabled(false);
            secondChoice = images.get(index);
            secondChoiceIndex = index;

            if (firstChoice == secondChoice) {
                statusText.setText("It's a match!");
                isButtonMatched[firstChoiceIndex] = true;
                isButtonMatched[secondChoiceIndex] = true;
                resetChoices();
                setButtonsEnabled(true);
            } else {
                statusText.setText("Try again");
                buttons[firstChoiceIndex].postDelayed(() -> {
                    buttons[firstChoiceIndex].setImageResource(android.R.color.transparent);
                    buttons[secondChoiceIndex].setImageResource(android.R.color.transparent);
                    resetChoices();
                    setButtonsEnabled(true);
                }, timeInMilliseconds);
            }
        }
    }

    private void resetChoices() {
        firstChoice = secondChoice = -1;
        firstChoiceIndex = secondChoiceIndex = -1;
        if (allButtonsMatched()) endGame();
    }

    private boolean allButtonsMatched() {
        for (boolean matched : isButtonMatched) {
            if (!matched) return false;
        }
        return true;
    }

    private void endGame() {
        elapsedTime = System.currentTimeMillis() - startTime;
        isGameRunning = false;
        handler.removeCallbacks(timerRunnable);
        statusText.setText("Game over!");
        showTimeDialog();
    }

    private void showTimeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        SpannableString title = new SpannableString("Game over - Well done!");
        title.setSpan(new AbsoluteSizeSpan(24, true), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setTitle(title);

        // הצגת הזמן והניקוד בשתי שורות
        SpannableString subTitle = new SpannableString("Your time was: " + (elapsedTime / 1000) + " seconds");
        subTitle.setSpan(new AbsoluteSizeSpan(20, true), 0, subTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setMessage(subTitle);

        // חישוב הניקוד - נניח ניקוד התחלתי של 1000 נקודות, ונפחית 1 נקודה לכל שנייה

        int baseScore = 500;
        int timePenalty = (int) elapsedTime / 1000;
        int score = baseScore - timePenalty; // 100 נקודות לכל זוג שנמצא

        String messageScore = "Score: " +score;  // הניקוד

        GameDatabaseHelper dbHelper = new GameDatabaseHelper(this);
        dbHelper.insertGame(nameOfLevel,score, (int) elapsedTime / 1000);


// יצירת TextView עם טקסט מותאם אישית
        TextView messageTextView = new TextView(this);
        messageTextView.setText(messageScore);
        messageTextView.setGravity(Gravity.CENTER);  // יישור טקסט למרכז
        messageTextView.setTextSize(20);  // שינוי גודל טקסט לניקוד ולזמן

        builder.setView(messageTextView);  // הגדרת TextView כצפייה בהודעה

        builder.setPositiveButton("Home page", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Save the score using SharedPreferences
                saveScoreToSharedPreferences(score);
                Intent intent = new Intent(MainDynamicActivity.this, MainActivity.class);
                startActivity(intent);  // התחלת ה-Activity החדש (חזרה לדף הבית)
            }
        });
        builder.setNegativeButton("Yeah!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveScoreToSharedPreferences(score);

                // כפתור חזרה לדף הבית
                Intent intent = new Intent(MainDynamicActivity.this, MainDynamicActivity.class);
                startActivity(intent);  // התחלת ה-Activity החדש (חזרה לדף הבית)
            }
        });
        builder.setNeutralButton("Record board", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveScoreToSharedPreferences(score);

                Intent intent = new Intent(MainDynamicActivity.this, RecordBoardActivity.class);
                startActivity(intent);  // התחלת ה-Activity החדש (חזרה לדף הבית)
            }
        });
        builder.setCancelable(false);  // אם אתה רוצה שהשחקן לא יוכל לדלג על ההודעה לפני שלחץ על כפתור
        builder.create().show();
    }

    private void startNewGame(GameLevel level) {
        imageResources = getImageResourcesForTheme(Theme.CARTOON_CHARACTERS, level);
        images.clear();
        images.addAll(imageResources);
        Collections.shuffle(images);
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setImageResource(android.R.color.transparent);
            isButtonFlipped[i] = false;
            isButtonMatched[i] = false;
        }

        // Log each image after shuffle with index info
        for (int row = 0; row < 4; row++) {
            StringBuilder rowLog = new StringBuilder();
            for (int col = 0; col < 4; col++) {
                int index = row * 4 + col;
                rowLog.append(images.get(index)).append("  ");
            }
            Log.d("BOARD", "Row " + row + ": " + rowLog.toString());
        }

        resetChoices();
        startTime = System.currentTimeMillis();
        isGameRunning = true;
        handler.post(timerRunnable);
        statusText.setText("Find the matching pairs!");
    }

    private void setButtonsEnabled(boolean enabled) {
        for (ImageButton button : buttons) {
            button.setEnabled(enabled);
        }
    }

    private void saveScoreToSharedPreferences(int newScore) {
        SharedPreferences prefs = getSharedPreferences("GameData", MODE_PRIVATE);
        int updatedTotalScore = prefs.getInt("totalScore", Constants.INITIAL_SCORE) + newScore;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("totalScore", updatedTotalScore);
        editor.putInt("lastGameScore", newScore);
        editor.apply();
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
