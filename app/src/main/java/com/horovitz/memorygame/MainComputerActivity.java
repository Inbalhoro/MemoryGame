package com.horovitz.memorygame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MainComputerActivity extends AppCompatActivity {
    Button navigateButton;
    private long startTime;
    private long elapsedTime;
    private TextView timerTextView;
    private Handler handler = new Handler();
    private boolean isGameRunning = false;
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long elapsedTime = System.currentTimeMillis() - startTime;
            timerTextView.setText("time: " + (elapsedTime / 1000));
            if (isGameRunning) {
                handler.postDelayed(this, 100);
            }
        }
    };

    private ImageButton[] buttons = new ImageButton[16];
    private ArrayList<Integer> images = new ArrayList<>();
    private int[] imageResources;

    private int firstChoice = -1;
    private int secondChoice = -1;
    private int firstChoiceIndex = -1;
    private int secondChoiceIndex = -1;
    private boolean[] isButtonFlipped = new boolean[16];
    private boolean[] isButtonMatched = new boolean[16];

    private TextView statusText;
    private Button resetButton;

    private int playerMatches = 0;
    private int computerMatches = 0;
    private int currentPlayer = 1; // 1 for player, 2 for computer
    private boolean computerPlaying = false;
    private boolean isComputerTurn = false;
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playwithcomputer);

            imageResources = new int[] {R.drawable.animal1, R.drawable.animal1, R.drawable.animal2, R.drawable.animal2,
                    R.drawable.animal13, R.drawable.animal13, R.drawable.animal4, R.drawable.animal4,
                    R.drawable.animal5, R.drawable.animal5, R.drawable.animal6, R.drawable.animal6,
                    R.drawable.animal7, R.drawable.animal7, R.drawable.animal8, R.drawable.animal8};


        buttons[0] = findViewById(R.id.button_1);
        buttons[1] = findViewById(R.id.button_2);
        buttons[2] = findViewById(R.id.button_3);
        buttons[3] = findViewById(R.id.button_4);
        buttons[4] = findViewById(R.id.button_5);
        buttons[5] = findViewById(R.id.button_6);
        buttons[6] = findViewById(R.id.button_7);
        buttons[7] = findViewById(R.id.button_8);
        buttons[8] = findViewById(R.id.button_9);
        buttons[9] = findViewById(R.id.button_10);
        buttons[10] = findViewById(R.id.button_11);
        buttons[11] = findViewById(R.id.button_12);
        buttons[12] = findViewById(R.id.button_13);
        buttons[13] = findViewById(R.id.button_14);
        buttons[14] = findViewById(R.id.button_15);
        buttons[15] = findViewById(R.id.button_16);
        timerTextView = findViewById(R.id.timerTextView);
        statusText = findViewById(R.id.statusText);
        resetButton = findViewById(R.id.resetButton);

        startNewGame();

        for (int i = 0; i < 16; i++) {
            final int index = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onButtonClick(index);
                }
            });
        }

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewGame();
            }
        });
    }

    private void startNewGame() {
        for (int i = 0; i < 16; i++) {
            buttons[i].setImageResource(android.R.color.transparent);
            isButtonFlipped[i] = false;
            isButtonMatched[i] = false;
        }

        startTime = System.currentTimeMillis();
        isGameRunning = true;
        handler.postDelayed(timerRunnable, 100);

        images.clear();
        for (int i = 0; i < imageResources.length; i++) {
            images.add(imageResources[i]);
        }
        Collections.shuffle(images);

        firstChoice = -1;
        secondChoice = -1;
        firstChoiceIndex = -1;
        secondChoiceIndex = -1;

        currentPlayer = 1;  // Start with player
        statusText.setText("Your turn!");
    }

    private void onButtonClick(int index) {
        if (isButtonMatched[index] || isComputerTurn || index == firstChoiceIndex) {
            return;
        }

        buttons[index].setImageResource(images.get(index));
        isButtonFlipped[index] = true;

        if (firstChoice == -1) {
            firstChoice = images.get(index);
            firstChoiceIndex = index;
        } else {
            secondChoice = images.get(index);
            secondChoiceIndex = index;
            checkMatch();
        }
    }

    private void checkMatch() {
        if (firstChoice == secondChoice) {
            if (currentPlayer == 1) {
                playerMatches++;
                statusText.setText("You found a match! Play again.");
            } else {
                computerMatches++;
                statusText.setText("Computer found a match! Playing again...");
            }
            isButtonMatched[firstChoiceIndex] = true;
            isButtonMatched[secondChoiceIndex] = true;
            resetChoices();

            // שחקן ממשיך – אם זה המחשב, קורא לעצמו שוב
            if (currentPlayer == 2) {
                new Handler().postDelayed(() -> computerMove(), 1000);
            }

        } else {
            statusText.setText("Try again later,");
            buttons[firstChoiceIndex].postDelayed(new Runnable() {
                @Override
                public void run() {
                    buttons[firstChoiceIndex].setImageResource(android.R.color.transparent);
                    buttons[secondChoiceIndex].setImageResource(android.R.color.transparent);
                    resetChoices();
                    switchPlayer(); //  רק כאן יש החלפת תור
                }
            }, 700);
        }
    }


    private void resetChoices() {

        firstChoice = -1;
        secondChoice = -1;
        firstChoiceIndex = -1;
        secondChoiceIndex = -1;

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

        if (computerMatches<playerMatches){
            SpannableString subTitle = new SpannableString( playerMatches +"You Won!!");
            subTitle.setSpan(new AbsoluteSizeSpan(16, true), 0, subTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // שינוי גודל לכותרת המשנה ל-24sp
            builder.setMessage(subTitle);

        }
        else if (playerMatches<computerMatches){
            SpannableString subTitle = new SpannableString(computerMatches +" The computer Wons!");
            subTitle.setSpan(new AbsoluteSizeSpan(16, true), 0, subTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // שינוי גודל לכותרת המשנה ל-24sp
            builder.setMessage(subTitle);
        }
        else{
            SpannableString subTitle = new SpannableString("You BOTH won - you succeeded to reveal the same amount of couples");
            subTitle.setSpan(new AbsoluteSizeSpan(16, true), 0, subTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // שינוי גודל לכותרת המשנה ל-24sp
            builder.setMessage(subTitle);
        }

        String message = "Time: " + (elapsedTime / 1000) + " s\n";  // זמן בשניות

        // זמן משחק (בשניות)
        long elapsedTimeInSeconds = (elapsedTime / 1000);

        // חישוב הניקוד - נניח ניקוד התחלתי של 1000 נקודות, ונפחית 1 נקודה לכל שנייה
        int baseScore = 400;
        int timePenalty = (int) elapsedTimeInSeconds;
        int score = baseScore - timePenalty ; // 100 נקודות לכל זוג שנמצא


        message += "Score: " +score;  // הניקוד

        Log.d("Rinat", "score " + score);

        GameDatabaseHelper dbHelper = new GameDatabaseHelper(this);
        dbHelper.insertGame("playing with the computer game",score, (int) elapsedTime / 1000);


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
                Intent intent = new Intent(MainComputerActivity.this, MainActivity.class);
                startActivity(intent);  // התחלת ה-Activity החדש (חזרה לדף הבית)
            }
        });
        builder.setCancelable(false);  // אם אתה רוצה שהשחקן לא יוכל לדלג על ההודעה לפני שלחץ על כפתור
        builder.create().show();
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

    private int getTotalScore() {
        SharedPreferences prefs = getSharedPreferences("GameData", MODE_PRIVATE);
        return prefs.getInt("totalScore", Constants.INITIAL_SCORE);
    }

    private void switchPlayer() {
        if (currentPlayer == 1) {
            currentPlayer = 2;  // Switch to computer
            statusText.setText(statusText.getText().toString()+" Computer's turn!");
            isComputerTurn = true;
            computerMove();
        } else {
            currentPlayer = 1;  // Switch to player
            statusText.setText(statusText.getText().toString()+" Your turn!");
            isComputerTurn = false;
        }
    }

    private void computerMove() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isGameRunning) return;

                // Simulate computer turn with random moves (basic AI)
                int index1 = random.nextInt(16);
                int index2 = random.nextInt(16);
                while (index2 == index1 || isButtonMatched[index1] || isButtonMatched[index2]) {
                    index1 = random.nextInt(16);
                    index2 = random.nextInt(16);
                }

                buttons[index1].setImageResource(images.get(index1));
                buttons[index2].setImageResource(images.get(index2));

                if (images.get(index1).equals(images.get(index2))) {

                    isButtonMatched[index1] = true;
                    isButtonMatched[index2] = true;

                    computerMatches++;
                    statusText.setText("Computer found a match!");
                    switchPlayer();

                } else {
                    int finalIndex = index2;
                    int finalIndex1 = index1;
                    buttons[index1].postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            buttons[finalIndex1].setImageResource(android.R.color.transparent);
                            buttons[finalIndex].setImageResource(android.R.color.transparent);
                            statusText.setText("Computer did not find a match!");
                            switchPlayer();
                        }
                    }, 700);
                }
            }
        }, 1000);
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
            Intent intent = new Intent(MainComputerActivity.this, MainActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש
        }
        if (id==R.id.action_settings){
            Toast.makeText(this, "You can change the settings ONLY from the single player game!", Toast.LENGTH_SHORT).show();
        }
        if (id==R.id.action_recordBoard){
            Intent intent = new Intent(MainComputerActivity.this, RecordBoardActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש
        }
        if (id==R.id.action_shop) {
            Intent intent = new Intent(MainComputerActivity.this, MainShop.class);
            startActivity(intent); // התחלת ה-Activity החדש        }
        }
        if (id==R.id.action_help){
            Intent intent = new Intent(MainComputerActivity.this, helpActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש
        }
        if (id==R.id.action_start){
            Intent intent = new Intent(MainComputerActivity.this, MainStart.class);
            startActivity(intent); // התחלת ה-Activity החדש
            Toast.makeText(this, "You pressed RESTART -  Please wait a few seconds", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
