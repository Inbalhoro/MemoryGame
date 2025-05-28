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
    private int timeInNumbersS;
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

// קריאת ההגדרות מ-SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("GameSettings", MODE_PRIVATE);
        String difficulty = sharedPreferences.getString("difficulty", "Regular");  // ברירת מחדל היא "Easy"

        String time = sharedPreferences.getString("selectedTime", "Regular"); // ברירת מחדל:
        String theme = sharedPreferences.getString("selectedTheme", "Cartoon Characters"); // ברירת מחדל: "דמויות מצוירות"
        boolean isSoundEnabled = sharedPreferences.getBoolean("isSoundEnabled", true); // ברירת מחדל: true

        updateGameSettings(difficulty, time, theme, isSoundEnabled);

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
                    R.drawable.image3, R.drawable.image3, R.drawable.image4, R.drawable.image4,
                    R.drawable.image10, R.drawable.image10, R.drawable.image6, R.drawable.image6,
                    R.drawable.image7, R.drawable.image7, R.drawable.image8, R.drawable.image8};
        } else if (theme.equals("Animals")) {
            // להשתמש בתמונות של דמויות מצוירות
            imageResources = new int[] {R.drawable.animal1, R.drawable.animal1, R.drawable.animal2, R.drawable.animal2,
                    R.drawable.animal3, R.drawable.animal3, R.drawable.animal4, R.drawable.animal4,
                    R.drawable.animal5, R.drawable.animal5, R.drawable.animal6, R.drawable.animal6,
                    R.drawable.animal7, R.drawable.animal7, R.drawable.animal8, R.drawable.animal8};
        }
        else if (theme.equals("Food")) {
            // להשתמש בתמונות של דמויות מצוירות
            imageResources = new int[] {R.drawable.food1, R.drawable.food1, R.drawable.food2, R.drawable.food2,
                    R.drawable.food3, R.drawable.food3, R.drawable.food15, R.drawable.food15,
                    R.drawable.food5, R.drawable.food5, R.drawable.food6, R.drawable.food6,
                    R.drawable.food7, R.drawable.food7, R.drawable.food8, R.drawable.food8};
        }else if (theme.equals("Flags")) {
            // להשתמש בתמונות של דמויות מצוירות
            imageResources = new int[] {R.drawable.flag1, R.drawable.flag1, R.drawable.flag2, R.drawable.flag2,
                    R.drawable.flag13, R.drawable.flag13, R.drawable.flag12, R.drawable.flag12,
                    R.drawable.flag5, R.drawable.flag5, R.drawable.flag6, R.drawable.flag6,
                    R.drawable.flag7, R.drawable.flag7, R.drawable.flag10, R.drawable.flag10};
        }

        // אם צלילים מופעלים, תתחיל את המוזיקה, אחרת תפסיק אותה
        if (isSoundEnabled) {
            startMusicService();
        } else {
            stopMusicService();
        }
    }

    private void stopMusicService() {
        Intent serviceIntent = new Intent(MainComputerActivity.this, MusicService.class);
        stopService(serviceIntent); // עוצר את המוזיקה
    }

    private void startMusicService() {
        Intent serviceIntent = new Intent(MainComputerActivity.this, MusicService.class);
        startService(serviceIntent); // מתחיל את המוזיקה
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


        // Log each image after shuffle with index info
        for (int row = 0; row < 4; row++) {
            StringBuilder rowLog = new StringBuilder();
            for (int col = 0; col < 4; col++) {
                int index = row * 4 + col;
                rowLog.append(images.get(index)).append("  ");
            }
            Log.d("BOARD", "Row " + row + ": " + rowLog.toString());
        }

        firstChoice = -1;
        secondChoice = -1;
        firstChoiceIndex = -1;
        secondChoiceIndex = -1;

        currentPlayer = 1;  // Start with player
        statusText.setText("Your turn!");
    }

    private void onButtonClick(int index) {
        if (isButtonMatched[index] || isComputerTurn || index == firstChoiceIndex) { //כל האופציות שיכולות לגמור למשחק לא נכון
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
                statusText.setText("Computer found a match!");
            }
            isButtonMatched[firstChoiceIndex] = true;
            isButtonMatched[secondChoiceIndex] = true;
            resetChoices();

            // שחקן ממשיך – אם זה המחשב, קורא לעצמו שוב
            if (currentPlayer == 2) {
                new Handler().postDelayed(() -> computerMove(), 1000);
            }

        } else {
            buttons[firstChoiceIndex].postDelayed(new Runnable() {//אם אין זוג
                @Override
                public void run() {
                    buttons[firstChoiceIndex].setImageResource(android.R.color.transparent);
                    buttons[secondChoiceIndex].setImageResource(android.R.color.transparent);
                    resetChoices();
                    switchPlayer(); //  רק כאן יש החלפת תור
                }
            }, timeInNumbersS);
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
            SpannableString subTitle = new SpannableString( playerMatches +" matches You found and the computer found "+computerMatches+ " - you won!");
            subTitle.setSpan(new AbsoluteSizeSpan(16, true), 0, subTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // שינוי גודל לכותרת המשנה ל-24sp
            builder.setMessage(subTitle);

        }
        else if (playerMatches<computerMatches){
            SpannableString subTitle = new SpannableString(computerMatches +" matches the computer found and you found "+playerMatches+ " - computer wons!");
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


        GameDatabaseHelper dbHelper = new GameDatabaseHelper(this);
        dbHelper.insertGame("playing with the computer game",score, (int) elapsedTime / 1000);


        TextView messageTextView = new TextView(this);
        messageTextView.setText(message);
        messageTextView.setGravity(Gravity.CENTER);  // יישור טקסט למרכז
        messageTextView.setTextSize(20);  // שינוי גודל טקסט לניקוד ולזמן

        builder.setView(messageTextView);  // הגדרת TextView כצפייה בהודעה


        builder.setPositiveButton("Home page", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                saveScoreToSharedPreferences(score);
                startActivity(new Intent(MainComputerActivity.this, MainActivity.class));
            }
        });

        builder.setNegativeButton("Play again", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                saveScoreToSharedPreferences(score);
                startNewGame();
            }
        });

        builder.setNeutralButton("Record board", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                saveScoreToSharedPreferences(score);
                startActivity(new Intent(MainComputerActivity.this, RecordBoardActivity.class));
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
            statusText.setText("Computer's turn!");
            isComputerTurn = true;
            computerMove();
        } else {
            currentPlayer = 1;  // Switch to player
            statusText.setText("Your turn!");
            isComputerTurn = false;
        }
    }

    private void computerMove() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isGameRunning) return;

                //  בדיקה אם כל הכפתורים נחשפו - עצור!
                boolean allMatched = true;
                for (boolean matched : isButtonMatched) {
                    if (!matched) {
                        allMatched = false;
                        break;
                    }
                }
                if (allMatched) {//   כל הזוגות נמצאו - אל תמשיך אם המשחק נגמר
                    elapsedTime = System.currentTimeMillis() - startTime;  // זמן שלקח לסיים את המשחק
                    statusText.setText("Game over!");
                    showTimeDialog();
                    isGameRunning = false;  // עצור את זמן הריצה
                    handler.removeCallbacks(timerRunnable);  // הסר את הריצה של עדכון הזמן
                }

                // המשך מהלך רגיל של המחשב
                int index1 = random.nextInt(16);
                int index2 = random.nextInt(16);
                while (index2 == index1 || isButtonMatched[index1] || isButtonMatched[index2]) { //עד שהוא לא מגריל כפתור שלא נלחץ הוא לא מסיים את הלולאה
                    index1 = random.nextInt(16);
                    index2 = random.nextInt(16);
                }

                buttons[index1].setImageResource(images.get(index1));
                buttons[index2].setImageResource(images.get(index2));

                if (images.get(index1).equals(images.get(index2))) {
                    isButtonMatched[index1] = true;
                    isButtonMatched[index2] = true;

                    computerMatches++;
                    statusText.setText("Computer found a match! - The computer has another turn");

                    // ✅ נקרא לעצמו רק אם לא נגמר המשחק
                    resetChoices();
                    if (isGameRunning) {
                        computerMove();  // המשך רק אם המשחק לא הסתיים
                    }

                } else {
                    int finalIndex1 = index1;
                    int finalIndex2 = index2;
                    buttons[index1].postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            buttons[finalIndex1].setImageResource(android.R.color.transparent);
                            buttons[finalIndex2].setImageResource(android.R.color.transparent);
                            statusText.setText("Computer did not find a match!");
                            switchPlayer();
                        }
                    }, timeInNumbersS);
                }
            }
        }, 2000);//זמן שלוקח למחשב להגיב ולשחק
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
        if (id==R.id.action_settings) {
            Intent intent = new Intent(MainComputerActivity.this, SettingsActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש        }
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
