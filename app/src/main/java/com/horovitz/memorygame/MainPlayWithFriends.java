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

public class MainPlayWithFriends extends AppCompatActivity {
    Button navigateButton; // הכפתור שיעביר אותנו לדף החדש
    private long startTime;
    private long elapsedTime;
    private TextView timerTextView;  // TextView להצגת הזמן הרץ
    private Handler handler = new Handler();  // Handler לעדכון הזמן
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

    private ImageButton[] buttons = new ImageButton[16]; // מערך של כפתורים
    private ArrayList<Integer> images = new ArrayList<>(); // תמונות שנמצאות במשחק
    private int[] imageResources;
//            = {R.drawable.image1, R.drawable.image1, R.drawable.image2, R.drawable.image2,
//            R.drawable.image3, R.drawable.image3, R.drawable.image4, R.drawable.image4,
//            R.drawable.image5, R.drawable.image5, R.drawable.image6, R.drawable.image6,
//            R.drawable.image7, R.drawable.image7, R.drawable.image8, R.drawable.image8}; // כאן תוכל להוסיף את התמונות שלך

    private int firstChoice = -1;
    private int secondChoice = -1;
    private int firstChoiceIndex = -1;
    private int secondChoiceIndex = -1;
    private boolean[] isButtonFlipped = new boolean[16]; // מעקב אם כפתור כבר נחשף
    private boolean[] isButtonMatched = new boolean[16]; // מעקב אם הכפתור כבר נמצא בזוג נכון

    private TextView statusText;
    private Button resetButton; // כפתור איפוס
    private int player1Matches = 0;  // מספר הזוגות שנחשפו על ידי שחקן 1
    private int player2Matches = 0;  // מספר הזוגות שנחשפו על ידי שחקן 2

    private int currentPlayer = 1;  // 1 עבור שחקן 1, 2 עבור שחקן 2

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playwithafriends); // קישור ל-XML שלך

        // קבלת הערכים שהועברו מהדיאלוג
        Intent intent = getIntent();
        String selectedImage = intent.getStringExtra("selectedImage"); // התמונה שנבחרה
        String player1Name = intent.getStringExtra("player1Name"); // שם השחקן הראשון
        String player2Name = intent.getStringExtra("player2Name"); // שם השחקן השני

        if (selectedImage.equals("1")) {
            // להשתמש בתמונות של חיות
            imageResources = new int [] {R.drawable.image1, R.drawable.image1, R.drawable.image2, R.drawable.image2,
                    R.drawable.image3, R.drawable.image3, R.drawable.image4, R.drawable.image4,
                    R.drawable.image5, R.drawable.image5, R.drawable.image6, R.drawable.image6,
                    R.drawable.image7, R.drawable.image7, R.drawable.image8, R.drawable.image8};
        }
        else if (selectedImage.equals("2")) {
            // להשתמש בתמונות של דמויות מצוירות
            imageResources = new int[] {R.drawable.animal1, R.drawable.animal1, R.drawable.animal2, R.drawable.animal2,
                    R.drawable.animal3, R.drawable.animal3, R.drawable.animal4, R.drawable.animal4,
                    R.drawable.animal5, R.drawable.animal5, R.drawable.animal6, R.drawable.animal6,
                    R.drawable.animal7, R.drawable.animal7, R.drawable.animal8, R.drawable.animal8};
        }
        else if (selectedImage.equals("3")) {
            // להשתמש בתמונות של דמויות מצוירות
            imageResources = new int[]{R.drawable.flag1, R.drawable.flag1, R.drawable.flag2, R.drawable.flag2,
                    R.drawable.flag3, R.drawable.flag3, R.drawable.flag4, R.drawable.flag4,
                    R.drawable.flag5, R.drawable.flag5, R.drawable.flag6, R.drawable.flag6,
                    R.drawable.flag7, R.drawable.flag7, R.drawable.flag8, R.drawable.flag8};
        }
        else if (selectedImage.equals("4")) {
            // להשתמש בתמונות של דמויות מצוירות
            imageResources = new int[] {R.drawable.food1, R.drawable.food1, R.drawable.food2, R.drawable.food2,
                    R.drawable.food16, R.drawable.food16, R.drawable.food4, R.drawable.food4,
                    R.drawable.food5, R.drawable.food5, R.drawable.food6, R.drawable.food6,
                    R.drawable.food7, R.drawable.food7, R.drawable.food8, R.drawable.food8};
        }
        // הצגת שמות השחקנים במסך המשחק
        TextView playerNamesTextView = findViewById(R.id.playerNamesTextView); // TextView להציג את השמות
        playerNamesTextView.setText(player1Name + " VS " + player2Name); // הצגת השמות

        // אתחול כפתורים
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

        // אתחול ה-TextView
        statusText = findViewById(R.id.statusText);

        // אתחול כפתור האיפוס
        resetButton = findViewById(R.id.resetButton);

        // מיקסום התמונות באופן אקראי
        startNewGame(player1Name);

        // מאזין ללחיצות על כפתורים
        for (int i = 0; i < 16; i++) {
            final int index = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onButtonClick(index,player1Name,player2Name);
                }
            });
        }

        // מאזין לכפתור איפוס
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewGame(player1Name); // איפוס המשחק
            }
        });
    }

    private void startNewGame(String player1Name) {
        // אתחול מחדש של כפתורים (מסתיר את התמונות)
        for (int i = 0; i < 16; i++) {
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
        statusText.setText(player1Name+ " turn!");
    }

    private void onButtonClick(int index,String player1Name,String player2Name) {
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

            // אם התמונות תואמות
            if (firstChoice == secondChoice) {
                if (currentPlayer == 1) {
                    player1Matches++;
                    statusText.setText(player1Name + " found a match!");
                }
                else{
                    player2Matches++;
                    statusText.setText(player2Name + " found a match!");
                }
                isButtonMatched[firstChoiceIndex] = true;
                isButtonMatched[secondChoiceIndex] = true;
                resetChoices(player1Name, player2Name);
                setclickable(true);  // שחקן מקבל תור נוסף, לא מחליפים שחקן
            }
            // אם התמונות לא תואמות
            else {
                statusText.setText("Try again");
                // השהה את הצגת התמונות למספר שניות, ואז החבא אותן
                buttons[firstChoiceIndex].postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // החבא את התמונה הראשונה אם לא תואמת
                        buttons[firstChoiceIndex].setImageResource(android.R.color.transparent);
                        // החבא את התמונה השנייה אם לא תואמת
                        buttons[secondChoiceIndex].setImageResource(android.R.color.transparent);
                        setclickable(true);
                        resetChoices(player1Name,player2Name); // אתחול הבחירות
                        switchPlayer(player1Name,player2Name);
                    }
                }, 700); // השהייה של שנייה לפני החבאת התמונות
            }
        }
    }

    private void resetChoices(String player1Name,String player2Name) {
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
            showTimeDialog(player1Name,player2Name);
            isGameRunning = false;  // עצור את זמן הריצה
            handler.removeCallbacks(timerRunnable);  // הסר את הריצה של עדכון הזמן
        }
    }

    private void showTimeDialog(String player1Name,String player2Name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        SpannableString title = new SpannableString("Game over - Well done!");
        title.setSpan(new AbsoluteSizeSpan(24, true), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // שינוי גודל לכותרת הראשונה ל-36sp
        builder.setTitle(title);

        if (player1Matches<player2Matches){
            SpannableString subTitle = new SpannableString( player2Name +" Wons!!");
            subTitle.setSpan(new AbsoluteSizeSpan(16, true), 0, subTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // שינוי גודל לכותרת המשנה ל-24sp
            builder.setMessage(subTitle);

        }
        else if (player2Matches<player1Matches){
            SpannableString subTitle = new SpannableString(player1Name +" Wons!!");
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
        dbHelper.insertGame("playing with a friend game",score, (int) elapsedTime / 1000);


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
                Intent intent = new Intent(MainPlayWithFriends.this, MainActivity.class);
                startActivity(intent);  // התחלת ה-Activity החדש (חזרה לדף הבית)
            }
        });
        builder.setCancelable(false);  // אם אתה רוצה שהשחקן לא יוכל לדלג על ההודעה לפני שלחץ על כפתור
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

    private void switchPlayer(String player1Name,String player2Name) {
        if (currentPlayer == 1) {
            currentPlayer = 2;  // אם השחקן הנוכחי הוא 1, תעבור לשחקן 2
            statusText.setText(player2Name + "'s turn!");
        } else {
            currentPlayer = 1;  // אם השחקן הנוכחי הוא לא 1 (אז הוא 2), תעבור לשחקן 1
            statusText.setText(player1Name + "'s turn!");
        }
    }

    private void setclickable(boolean b) {
        for (int i = 0; i < 16; i++) {
            buttons[i].setEnabled(b);
        }
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
            Intent intent = new Intent(MainPlayWithFriends.this, MainActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש
        }
        if (id==R.id.action_settings){
            Toast.makeText(this, "You can change the settings ONLY from the single player game!", Toast.LENGTH_SHORT).show();
        }
        if (id==R.id.action_recordBoard){
            Intent intent = new Intent(MainPlayWithFriends.this, RecordBoardActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש
        }
        if (id==R.id.action_shop) {
            Intent intent = new Intent(MainPlayWithFriends.this, MainShop.class);
            startActivity(intent); // התחלת ה-Activity החדש        }
        }
        if (id==R.id.action_help) {
            Intent intent = new Intent(MainPlayWithFriends.this, helpActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש        }
        }
        if (id==R.id.action_start){
            Intent intent = new Intent(MainPlayWithFriends.this, MainStart.class);
            startActivity(intent); // התחלת ה-Activity החדש
            Toast.makeText(this, "You pressed RESTART -  Please wait a few seconds", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
