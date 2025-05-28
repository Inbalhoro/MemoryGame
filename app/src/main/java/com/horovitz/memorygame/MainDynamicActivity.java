package com.horovitz.memorygame;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
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
                handler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dynamic);

        loadPreferences();
        generateButtonsDynamically(gameLevel.getButtonCount());
        loadImageResources();
        initializeViews();
//        initializeGame();
    }

    private void loadPreferences() {
        SharedPreferences prefs = getSharedPreferences("GameData", MODE_PRIVATE);
        int selectedBackground = prefs.getInt("selectedBackground", R.drawable.backgroundsingleplayer);
        findViewById(R.id.dynamicGame).setBackgroundResource(selectedBackground);

        SharedPreferences sharedPreferences = getSharedPreferences("GameSettings", MODE_PRIVATE);
        String difficulty = sharedPreferences.getString("selectedDifficulty", "REGULAR");
        String time = sharedPreferences.getString("selectedTime", "Regular");
        boolean isSoundEnabled = sharedPreferences.getBoolean("isSoundEnabled", true);

        String levelName = getIntent().getStringExtra("GAME_LEVEL");
        gameLevel = GameLevel.valueOf(levelName);

        updateGameSettings(difficulty, time, isSoundEnabled);
    }

    private void loadImageResources() {
        SharedPreferences sharedPreferences = getSharedPreferences("GameSettings", MODE_PRIVATE);
        String theme = sharedPreferences.getString("selectedTheme", "CARTOON_CHARACTERS");

        Theme selectedThemeEnum;
        try {
            selectedThemeEnum = Theme.valueOf(theme.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            selectedThemeEnum = Theme.CARTOON_CHARACTERS;
        }

        imageResources = getImageResourcesForTheme(selectedThemeEnum, gameLevel);
    }

    private void initializeViews() {
        timerTextView = findViewById(R.id.timerTextView);
        statusText = findViewById(R.id.statusText);
        resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewGame(gameLevel);
            }
        });
    }

    private void initializeGame() {
        startNewGame(gameLevel);
    }

    private void generateButtonsDynamically(int totalButtons) {
        LinearLayout parentLayout = findViewById(R.id.llDynamicButtons);
        parentLayout.removeAllViews();

        buttons = new ImageButton[totalButtons];
        isButtonFlipped = new boolean[totalButtons];
        isButtonMatched = new boolean[totalButtons];

        // מקשיבים לרגע שבו ה-layout כבר נמדד (ולכן אפשר לגשת למידותיו)
        ViewTreeObserver vto = parentLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                parentLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int layoutWidth = parentLayout.getWidth();     // רוחב אמיתי של llDynamicButtons
                int layoutHeight = parentLayout.getHeight();   // גובה אמיתי של llDynamicButtons

                int columns = (int) Math.sqrt(totalButtons);
                int rows = (int) Math.ceil((double) totalButtons / columns);

                int marginPx = dpToPx(4);
                int totalHorizontalMargins = marginPx * (columns + 1);
                int totalVerticalMargins = marginPx * (rows + 1);

                int maxButtonWidth = (layoutWidth - totalHorizontalMargins) / columns;
                int maxButtonHeight = (layoutHeight - totalVerticalMargins) / rows;

                int buttonSize = Math.min(maxButtonWidth, maxButtonHeight);

                int index = 0;

                for (int row = 0; row < rows; row++) {
                    LinearLayout rowLayout = new LinearLayout(parentLayout.getContext());
                    rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                    rowLayout.setGravity(Gravity.CENTER);
                    rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));

                    for (int col = 0; col < columns && index < totalButtons; col++) {
                        ImageButton button = new ImageButton(parentLayout.getContext());

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                buttonSize, buttonSize);
                        params.setMargins(marginPx / 2, marginPx / 2, marginPx / 2, marginPx / 2);
                        button.setLayoutParams(params);

                        button.setImageResource(android.R.color.transparent);
                        button.setBackgroundResource(R.drawable.white);
                        button.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        button.setContentDescription("button_" + index);
                        button.setElevation(dpToPx(4));

                        final int finalIndex = index;
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onButtonClick(finalIndex, timeInNumbersS);
                            }
                        });

                        rowLayout.addView(button);
                        buttons[index] = button;
                        index++;
                    }

                    // בסיום הלולאה בתוך onGlobalLayout:
                    parentLayout.addView(rowLayout);

                }
                // אחרי שנוצרו כל הכפתורים - נתחיל את המשחק
                startNewGame(gameLevel);
            }
        });
    }



    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void updateGameSettings(String difficultyStr, String timeStr, boolean isSoundEnabled) {
        if ("HARD".equals(difficultyStr.toUpperCase())) {
            nameOfLevel = "The hard game";
        } else if ("EASY".equals(difficultyStr.toUpperCase())) {
            nameOfLevel = "The easy game";
        } else {
            nameOfLevel = "The regular game";
        }

        TimeSetting timeSetting;
        try {
            timeSetting = TimeSetting.valueOf(timeStr.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            timeSetting = TimeSetting.REGULAR;
        }

        timeInNumbersS = timeSetting.getSeconds();

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

        SpannableString subTitle = new SpannableString("Your time was: " + (elapsedTime / 1000) + " seconds");
        subTitle.setSpan(new AbsoluteSizeSpan(20, true), 0, subTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setMessage(subTitle);

        int baseScore = 500;
        int timePenalty = (int) elapsedTime / 1000;
        int score = baseScore - timePenalty;

        String messageScore = "Score: " + score;

        GameDatabaseHelper dbHelper = new GameDatabaseHelper(this);
        dbHelper.insertGame(nameOfLevel, score, (int) elapsedTime / 1000);

        TextView messageTextView = new TextView(this);
        messageTextView.setText(messageScore);
        messageTextView.setGravity(Gravity.CENTER);
        messageTextView.setTextSize(20);

        builder.setView(messageTextView);

        builder.setPositiveButton("Home page", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                saveScoreToSharedPreferences(score);
                startActivity(new Intent(MainDynamicActivity.this, MainActivity.class));
            }
        });

        builder.setNegativeButton("Play again", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                saveScoreToSharedPreferences(score);
                startNewGame(gameLevel);
            }
        });

        builder.setNeutralButton("Record board", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                saveScoreToSharedPreferences(score);
                startActivity(new Intent(MainDynamicActivity.this, RecordBoardActivity.class));
            }
        });

        builder.setCancelable(false);
        builder.create().show();
    }

    private void startNewGame(GameLevel level) {
        images.clear();
        images.addAll(imageResources);
        Collections.shuffle(images);

        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setImageResource(android.R.color.transparent);
            isButtonFlipped[i] = false;
            isButtonMatched[i] = false;
        }

        int totalButtons = gameLevel.getButtonCount();
        int columns = (int) Math.sqrt(totalButtons);
        int rows = (int) Math.ceil((double) totalButtons / columns);

        Log.d("BOARD", "Logging game board: " + rows + " rows x " + columns + " columns");

        for (int row = 0; row < rows; row++) {
            StringBuilder rowLog = new StringBuilder();
            for (int col = 0; col < columns; col++) {
                int index = row * columns + col;
                if (index < images.size()) {
                    rowLog.append(images.get(index)).append("  ");
                }
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
