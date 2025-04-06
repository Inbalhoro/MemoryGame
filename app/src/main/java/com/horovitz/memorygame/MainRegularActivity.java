
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

public class MainRegularActivity extends AppCompatActivity {
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

    private ImageButton[] buttons = new ImageButton[16]; // מערך של כפתורים
    private ArrayList<Integer> images = new ArrayList<>(); // תמונות שנמצאות במשחק
    private int[] imageResources = {R.drawable.image1, R.drawable.image1, // Pair 1
            R.drawable.image2, R.drawable.image2,
            R.drawable.image3, R.drawable.image3,
            R.drawable.image4, R.drawable.image4,
            R.drawable.image5, R.drawable.image5,
            R.drawable.image6, R.drawable.image6,
            R.drawable.image7, R.drawable.image7,
            R.drawable.image8, R.drawable.image8, }; // כאן תוכל להוסיף את התמונות שלך

    private int firstChoice = -1;
    private int secondChoice = -1;
    private int firstChoiceIndex = -1;
    private int secondChoiceIndex = -1;
    private int timeInNumbersS;

    private boolean[] isButtonFlipped = new boolean[16]; // מעקב אם כפתור כבר נחשף
    private boolean[] isButtonMatched = new boolean[16]; // מעקב אם הכפתור כבר נמצא בזוג נכון

    private TextView statusText;
    private Button resetButton; // כפתור איפוס

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_regular); // קישור ל-XML שלך

// קריאת ההגדרות מ-SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("GameSettings", MODE_PRIVATE);
        String difficulty = sharedPreferences.getString("difficulty", "Regular");  // ברירת מחדל היא "Easy"

        String time = sharedPreferences.getString("selectedTime", "Regular"); // ברירת מחדל:
        String theme = sharedPreferences.getString("selectedTheme", "Cartoon Characters"); // ברירת מחדל: "דמויות מצוירות"
        boolean isSoundEnabled = sharedPreferences.getBoolean("isSoundEnabled", true); // ברירת מחדל: true

        updateGameSettings(difficulty, time, theme, isSoundEnabled);

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
        startNewGame();

        // מאזין ללחיצות על כפתורים
        for (int i = 0; i < 16; i++) {
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
                    R.drawable.image3, R.drawable.image3, R.drawable.image4, R.drawable.image4,
                    R.drawable.image5, R.drawable.image5, R.drawable.image6, R.drawable.image6,
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
                    R.drawable.food3, R.drawable.food3, R.drawable.food4, R.drawable.food4,
                    R.drawable.food5, R.drawable.food5, R.drawable.food6, R.drawable.food6,
                    R.drawable.food7, R.drawable.food7, R.drawable.food8, R.drawable.food8};
        }else if (theme.equals("Flags")) {
            // להשתמש בתמונות של דמויות מצוירות
            imageResources = new int[] {R.drawable.flag1, R.drawable.flag1, R.drawable.flag2, R.drawable.flag2,
                    R.drawable.flag3, R.drawable.flag3, R.drawable.flag4, R.drawable.flag4,
                    R.drawable.flag5, R.drawable.flag5, R.drawable.flag6, R.drawable.flag6,
                    R.drawable.flag7, R.drawable.flag7, R.drawable.flag8, R.drawable.flag8};
        }

        // אם צלילים מופעלים, תתחיל את המוזיקה, אחרת תפסיק אותה
        if (isSoundEnabled) {
            startMusicService();
        } else {
            stopMusicService();
        }
    }

    private void stopMusicService() {
        Intent serviceIntent = new Intent(MainRegularActivity.this, MusicService.class);
        stopService(serviceIntent); // עוצר את המוזיקה
    }

    private void startMusicService() {
        Intent serviceIntent = new Intent(MainRegularActivity.this, MusicService.class);
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

        // חישוב הניקוד - נניח ניקוד התחלתי של 1000 נקודות, ונפחית 1 נקודה לכל שנייה
        int baseScore = 500;
        int timePenalty = (int) elapsedTimeInSeconds;
        int score = baseScore - timePenalty; // 100 נקודות לכל זוג שנמצא

        message += "Score: " +score;  // הניקוד
        Log.d("Rinat", "score " + score);


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

                // כפתור חזרה לדף הבית
                Intent intent = new Intent(MainRegularActivity.this, MainActivity.class);
                startActivity(intent);  // התחלת ה-Activity החדש (חזרה לדף הבית)
            }
        });
        builder.setNegativeButton("Yeah!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveScoreToSharedPreferences(score);

                // כפתור חזרה לדף הבית
                Intent intent = new Intent(MainRegularActivity.this, MainEasyActivity.class);
                startActivity(intent);  // התחלת ה-Activity החדש (חזרה לדף הבית)
            }
        });
        builder.setNeutralButton("Record board", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveScoreToSharedPreferences(score);

                Intent intent = new Intent(MainRegularActivity.this, RecordBoardActivity.class);
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.action_firstpage){
            Intent intent = new Intent(MainRegularActivity.this, MainActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש
        }
        if (id==R.id.action_settings) {
            Intent intent = new Intent(MainRegularActivity.this, SettingsActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש
        }
        if (id==R.id.action_start){
            Toast.makeText(this, "You selected start", Toast.LENGTH_SHORT).show();
            // יצירת Intent כדי לעבור לדף החדש
            Intent intent = new Intent(MainRegularActivity.this, MainActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש

        }
        return super.onOptionsItemSelected(item);
    }
}
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.os.Handler;
//import android.text.SpannableString;
//import android.text.Spanned;
//import android.text.style.AbsoluteSizeSpan;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.Button;
//import android.widget.GridLayout;
//import android.widget.ImageButton;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.ResourceBundle;
//
////
//public class MainRegularActivity extends AppCompatActivity {
//    private Button navigateButton; // הכפתור שיעביר אותנו לדף החדש
//    private Button navigateToFirstPageButton;
//    private long startTime,elapsedTime;
//    private TextView timerTextView,statusText;  // TextView להצגת הזמן הרץ
//    private Handler handler = new Handler();  // Handler לעדכון הזמן
//    private boolean isGameRunning = false;  // משתנה לבדוק אם המשחק רץ
//    private Runnable timerRunnable = new Runnable() {
//        @Override
//        public void run() {
//            // מחשבים את הזמן הנוכחי (שנמסר מאז התחלת המשחק)
//            long elapsedTime = System.currentTimeMillis() - startTime;
//
//            // מעדכנים את ה-TextView עם הזמן החדש
//            timerTextView.setText("time: " + (elapsedTime / 1000) );
//
//            // מריצים את הריצה הזו כל 100 מילישניות (0.1 שניה)
//            if (isGameRunning) {
//                handler.postDelayed(this, 100);  // עדכון כל 100 מילישניות
//            }
//        }
//    };
//
//    private ImageButton[] buttons; // מערך של כפתורים
////     = new ImageButton[16] היה פעם
//    private ArrayList<Integer> images = new ArrayList<>(); // תמונות שנמצאות במשחק
//    private int[] imageResources;
////    = {R.drawable.image1, R.drawable.image1, R.drawable.image2, R.drawable.image2,
////            R.drawable.image3, R.drawable.image3, R.drawable.image4, R.drawable.image4,
////            R.drawable.image5, R.drawable.image5, R.drawable.image6, R.drawable.image6,
////            R.drawable.image7, R.drawable.image7, R.drawable.image8, R.drawable.image8}; // כאן תוכל להוסיף את התמונות שלך
//
//    private int firstChoice = -1, secondChoice = -1, firstChoiceIndex = -1,  secondChoiceIndex = -1;
//    private int timeInNumbersS;
//
//    private boolean[] isButtonFlipped; // מעקב אם כפתור כבר נחשף
////     = new boolean[16]
//    private boolean[] isButtonMatched; // מעקב אם הכפתור כבר נמצא בזוג נכון
////     = new boolean[16]
//    private Button resetButton; // כפתור איפוס
//    private ResourceBundle sharedPreferences;
//
//    private LinearLayout llDynamic;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_dinamic); // קישור ל-XML שלך
//
//        llDynamic = findViewById(R.id.llDynamic);
//
//// קריאת ההגדרות מ-SharedPreferences
//        SharedPreferences sharedPreferences = getSharedPreferences("GameSettings", MODE_PRIVATE);
//        String difficulty = sharedPreferences.getString("difficulty", "Regular");  // ברירת מחדל היא "Easy"
//
//        String time = sharedPreferences.getString("selectedTime", "Regular"); // ברירת מחדל: "5 שניות"
//        String theme = sharedPreferences.getString("selectedTheme", "Cartoon Characters"); // ברירת מחדל: "דמויות מצוירות"
//        boolean isSoundEnabled = sharedPreferences.getBoolean("isSoundEnabled", true); // ברירת מחדל: true
//
////// הגדרת התמונות לפי הנושא (למשל חיות או אוכל)
////        int[] imageResources;
////        if (theme.equals("Animals")) {
////            imageResources = new int[] {
////                    R.drawable.animal1, R.drawable.animal1,
////                    R.drawable.animal2, R.drawable.animal2,
////                    R.drawable.animal3, R.drawable.animal3,
////                    R.drawable.animal4, R.drawable.animal4,
////                    R.drawable.animal5, R.drawable.animal5,
////                    R.drawable.animal6, R.drawable.animal6,
////                    R.drawable.animal7, R.drawable.animal7,
////                    R.drawable.animal8, R.drawable.animal8
////            };
////        } else if (theme.equals("Food")) {
////            imageResources = new int[] {
////                    R.drawable.food1, R.drawable.food1,
////                    R.drawable.food2, R.drawable.food2,
////                    R.drawable.food3, R.drawable.food3,
////                    R.drawable.food4, R.drawable.food4,
////                    R.drawable.food5, R.drawable.food5,
////                    R.drawable.food6, R.drawable.food6,
////                    R.drawable.food7, R.drawable.food7,
////                    R.drawable.food8, R.drawable.food8
////            };
////        } else {
////            // תמונות ברירת מחדל אם הנושא לא תואם
////            imageResources = new int[] {
////                    R.drawable.default_image1, R.drawable.default_image1,
////                    R.drawable.default_image2, R.drawable.default_image2
////            };
////        }
//
//        updateGameSettings(difficulty,time, theme, isSoundEnabled);
////
////        // אתחול כפתורים
////        buttons[0] = findViewById(R.id.button_1);
////        buttons[1] = findViewById(R.id.button_2);
////        buttons[2] = findViewById(R.id.button_3);
////        buttons[3] = findViewById(R.id.button_4);
////        buttons[4] = findViewById(R.id.button_5);
////        buttons[5] = findViewById(R.id.button_6);
////        buttons[6] = findViewById(R.id.button_7);
////        buttons[7] = findViewById(R.id.button_8);
////        buttons[8] = findViewById(R.id.button_9);
////        buttons[9] = findViewById(R.id.button_10);
////        buttons[10] = findViewById(R.id.button_11);
////        buttons[11] = findViewById(R.id.button_12);
////        buttons[12] = findViewById(R.id.button_13);
////        buttons[13] = findViewById(R.id.button_14);
////        buttons[14] = findViewById(R.id.button_15);
////        buttons[15] = findViewById(R.id.button_16);
//
////        navigateToFirstPageButton = findViewById(R.id.navigateToFirstPageButton);
////        navigateToFirstPageButton.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                // יצירת Intent כדי לעבור לדף החדש
////                Intent intent = new Intent(MainGirlsActivity.this, MainActivity.class);
////                startActivity(intent); // התחלת ה-Activity החדש
////            }
////        });
//
//
//
//
////        navigateButton = findViewById(R.id.navigateButton); // למצוא את הכפתור במסך
////        navigateButton.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                // יצירת Intent כדי לעבור לדף החדש
////                Intent intent = new Intent(MainRegularActivity.this, MainBoysActivity.class);
////                startActivity(intent); // התחלת ה-Activity החדש
////            }
////
////
////        });
//        // אתחול כפתור האיפוס
//        resetButton = findViewById(R.id.resetButton);
//
//        timerTextView = findViewById(R.id.timerTextView);
//
//        statusText = findViewById(R.id.statusText);
//
//
//        // מיקסום התמונות באופן אקראי
//        startNewGame();
//
//
//        // מאזין לכפתור איפוס
//        resetButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startNewGame(); // איפוס המשחק
//            }
//        });
//    }
//
////
//private void updateGameSettings(String difficulty,String time, String theme, boolean isSoundEnabled) {
//
//    if (time.equals("Short")) {
//        timeInNumbersS = 300;  // זמן בשניות
//    } else if (time.equals("Regular")) {
//        timeInNumbersS = 700;
//    } else if (time.equals("Long")) {
//        timeInNumbersS = 1000;
//    }
//
//
//    int buttonCount = 16; // ברירת מחדל לכמות כפתורים (לרוב משחקים עם 16)
//    // לפי רמת הקושי, נקבע את מספר הכפתורים והקלפים
//    if (difficulty.equals("Easy")) {
//        buttonCount = 4;  // עבור רמת קושי "Easy" יהיו 6 קלפים
//    } else if (difficulty.equals("Regular")) {
//        buttonCount = 16; // עבור רמת קושי "Medium" יהיו 16 קלפים
//    } else if (difficulty.equals("Hard")) {
//        buttonCount = 36; // עבור רמת קושי "Hard" יהיו 36 קלפים
//    }
//
//    images.clear();
//    // על פי מספר הכפתורים/קלפים, נוסיף תמונות בצורה דינמית
//    for (int i = 0; i < buttonCount / 2; i++) {
//        int image_key = getResources().getIdentifier("image"+(i+1),"drawable",getPackageName());
//        images.add(image_key);
//        images.add(image_key);
//    }
//    Collections.shuffle(images);  // מיקסום התמונות
//
//    // יצירת מערך דינמי של כפתורים
//    buttons = new ImageButton[buttonCount];
//    isButtonFlipped = new boolean[buttonCount];
//    isButtonMatched = new boolean[buttonCount];
//
//    // 1. Get a reference to your LinearLayout (llDynamic)
//    LinearLayout llDynamic = findViewById(R.id.llDynamic);
//
//// 2. Calculate the number of rows and columns for a square grid
//    ////////////////////int buttonCount = 16; // For example, you want 16 buttons (4x4 grid)
//    int gridSize = (int) Math.ceil(Math.sqrt(buttonCount));  // Calculate the square root and round up
//
//// 3. Create a new GridLayout dynamically
//    GridLayout gridLayout = new GridLayout(this);
//
//// 4. Set the number of columns and rows for the GridLayout
//    gridLayout.setColumnCount(gridSize);  // Set the number of columns
//    gridLayout.setRowCount(gridSize);  // Set the number of rows
//
//// Optional: Set other properties like padding or layout params for the GridLayout
//    GridLayout.LayoutParams gridLayoutParams = new GridLayout.LayoutParams();
//    gridLayoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
//    gridLayoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
//    gridLayout.setLayoutParams(gridLayoutParams);
//
//// 5. Add buttons dynamically to the GridLayout
//    for (int i = 0; i < buttonCount; i++) {
//
//        ImageButton button = new ImageButton(this);
//
//        // Set the white background (you can replace 'white' with the actual drawable resource name)
//        button.setBackgroundResource(R.drawable.white); // Set the background to white
//
//        // Set the size of the button (optional, to make sure they are big enough to see)
//        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
//        params.width = 0;  // Set width as 0 to make it fill the available space
//        params.height = 0; // Set height as 0 to make it fill the available space
//        params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f); // Allow row to fill available space
//        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f); // Allow column to fill available space
//        button.setLayoutParams(params);  // Set the params to the button
//
//        button.setContentDescription("Button " + (i + 1));
//        isButtonFlipped[i] = false;  // Button not flipped yet
//        isButtonMatched[i] = false;  // Button not matched yet
//
//
//        // Optional: Set an OnClickListener to each button
//        final int index = i;
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Handle button click event
//                // For example: onButtonClick(timeInNumbersS, index);
//            }
//        });
//
//        // 6. Add the button to the GridLayout
//        gridLayout.addView(button);
//    }
//
//// 7. Add the GridLayout to the LinearLayout (llDynamic)
//    llDynamic.addView(gridLayout);
//
////    // אתחול כפתורים במסך
////    for (int i = 0; i < buttonCount; i++) {
////        int resId = getResources().getIdentifier("button_" + (i + 1), "id", getPackageName());
////        buttons[i] = findViewById(resId);
////        buttons[i].setImageResource(android.R.color.transparent);
////        final int index = i;
////
////        // עדכון לחיצה על הכפתור, בה מעבירים את ה- time כפרמטר
////        buttons[i].setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                onButtonClick(timeInNumbersS, index);  // כאן time ו-index מועברים יחד
////            }
////        });
////    }
//
//
//
//
////
////        // עדכון נושא
////        if (theme.equals("Cartoon Characters")) {
////            // להשתמש בתמונות של חיות
////            imageResources = new int [] {R.drawable.image1, R.drawable.image1, R.drawable.image2, R.drawable.image2,
////                    R.drawable.image3, R.drawable.image3, R.drawable.image4, R.drawable.image4,
////                    R.drawable.image5, R.drawable.image5, R.drawable.image6, R.drawable.image6,
////                    R.drawable.image7, R.drawable.image7, R.drawable.image8, R.drawable.image8};
////        } else if (theme.equals("Animals")) {
////            // להשתמש בתמונות של דמויות מצוירות
////            imageResources = new int[] {R.drawable.animal1, R.drawable.animal1, R.drawable.animal2, R.drawable.animal2,
////                    R.drawable.animal3, R.drawable.animal3, R.drawable.animal4, R.drawable.animal4,
////                    R.drawable.animal5, R.drawable.animal5, R.drawable.animal6, R.drawable.animal6,
////                    R.drawable.animal7, R.drawable.animal7, R.drawable.animal8, R.drawable.animal8};
////        }
////        else if (theme.equals("Food")) {
////            // להשתמש בתמונות של דמויות מצוירות
////            imageResources = new int[] {R.drawable.food1, R.drawable.food1, R.drawable.food2, R.drawable.food2,
////                    R.drawable.food3, R.drawable.food3, R.drawable.food4, R.drawable.food4,
////                    R.drawable.food5, R.drawable.food5, R.drawable.food6, R.drawable.food6,
////                    R.drawable.food7, R.drawable.food7, R.drawable.food8, R.drawable.food8};
////        }else if (theme.equals("Flags")) {
////            // להשתמש בתמונות של דמויות מצוירות
////            imageResources = new int[] {R.drawable.flag1, R.drawable.flag1, R.drawable.flag2, R.drawable.flag2,
////                    R.drawable.flag3, R.drawable.flag3, R.drawable.flag4, R.drawable.flag4,
////                    R.drawable.flag5, R.drawable.flag5, R.drawable.flag6, R.drawable.flag6,
////                    R.drawable.flag7, R.drawable.flag7, R.drawable.flag8, R.drawable.flag8};
////        }
//
//        // אם צלילים מופעלים, תתחיל את המוזיקה, אחרת תפסיק אותה
//        if (isSoundEnabled) {
//            startMusicService();
//        } else {
//            stopMusicService();
//        }
//    }
//
//    private void stopMusicService() {
//        Intent serviceIntent = new Intent(MainRegularActivity.this, MusicService.class);
//        stopService(serviceIntent); // עוצר את המוזיקה
//    }
//
//    private void startMusicService() {
//        Intent serviceIntent = new Intent(MainRegularActivity.this, MusicService.class);
//        startService(serviceIntent); // מתחיל את המוזיקה
//    }
//
//    private void startNewGame() {
//
//        startTime = System.currentTimeMillis();  // אתחול זמן ההתחלה (במילישניות)
//        isGameRunning = true;  // המשחק רץ
//
//        handler.postDelayed(timerRunnable, 100);  // כל 100 מילישניות
//
//
//        // איפוס משתנים
//        firstChoice = -1;
//        secondChoice = -1;
//        firstChoiceIndex = -1;
//        secondChoiceIndex = -1;
//
//        // עדכון טקסט סטטוס
//        statusText.setText("start!");
//    }
//
//
//
//
//
//
//    private void onButtonClick(int time, int index) {
//        // אם הכפתור כבר נמצא בזוג נכון, אל תאפשר ללחוץ עליו
//        if (isButtonMatched[index]) { return; }
//
//
//        if (index == firstChoiceIndex && firstChoice != -1) {
//            return;  // יציאה מהפונקציה אם הכפתור שנבחר כבר נבחר קודם
//        }
//
//        // הצגת התמונה בלחיצה
//        buttons[index].setImageResource(images.get(index));
//        isButtonFlipped[index] = true;
//
//        Log.d("Rinat", "onButtonClick firstChoice = " + firstChoice);
//        // אם זו הבחירה הראשונה
//        if (firstChoice == -1) {
//            firstChoice = images.get(index);
//            firstChoiceIndex = index;
//        }
//        // אם זו הבחירה השנייה
//        else {
//            setclickable(false);
//            secondChoice = images.get(index);
//            secondChoiceIndex = index;
//
//            //stop
//
//            // אם התמונות תואמות
//            if (firstChoice == secondChoice) {
//                statusText.setText("It's a match!");
//                isButtonMatched[firstChoiceIndex] = true; // הצבת הכפתור הראשון ככפתור תואם
//                isButtonMatched[secondChoiceIndex] = true; // הצבת הכפתור השני ככפתור תואם
//                resetChoices();
//                setclickable(true);
//            }
//
//            // אם התמונות לא תואמות
//            else {
//                statusText.setText("Try again");
//                // השהה את הצגת התמונות למספר שניות, ואז החבא אותן
//                buttons[firstChoiceIndex].postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.d("Rinat", "run firstChoice = " + firstChoice);
//                        // החבא את התמונה הראשונה אם לא תואמת
//                        buttons[firstChoiceIndex].setImageResource(android.R.color.transparent);
//                        // החבא את התמונה השנייה אם לא תואמת
//                        buttons[secondChoiceIndex].setImageResource(android.R.color.transparent);
//                        setclickable(true);
//                        resetChoices(); // אתחול הבחירות
//                    }
//                }, time); // השהייה של שנייה לפני החבאת התמונות
//            }
//        }
//    }
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//        if (id==R.id.action_firstpage){
//            Intent intent = new Intent(MainRegularActivity.this, MainActivity.class);
//            startActivity(intent); // התחלת ה-Activity החדש
//        }
//        if (id==R.id.action_settings) {
//            Intent intent = new Intent(MainRegularActivity.this, SettingsActivity.class);
//            startActivity(intent); // התחלת ה-Activity החדש
//        }
//        if (id==R.id.action_start){
//            Toast.makeText(this, "You selected start", Toast.LENGTH_SHORT).show();
//            // יצירת Intent כדי לעבור לדף החדש
//            Intent intent = new Intent(MainRegularActivity.this, MainActivity.class);
//            startActivity(intent); // התחלת ה-Activity החדש
//
//
//        }
//        return super.onOptionsItemSelected(item);
//    }
//    private void setclickable(boolean b) {
//        for (int i = 0; i < 16; i++) {
//            buttons[i].setEnabled(b);
//        }
//    }
//
//    private void resetChoices() {
//        Log.d("Rinat","resetChoices firstChoice = -1");
//        firstChoice = -1;
//        secondChoice = -1;
//        firstChoiceIndex = -1;
//        secondChoiceIndex = -1;
//
//        //start
//
//        // בדוק אם כל הכפתורים נחשפו
//        boolean allFlipped = true;
//        for (boolean matched : isButtonMatched) {
//            if (!matched) {
//                allFlipped = false;
//                break;
//            }
//        }
//
//        if (allFlipped) {
//            elapsedTime = System.currentTimeMillis() - startTime;  // זמן שלקח לסיים את המשחק
//            statusText.setText("Game over!");
//            showTimeDialog();
//            isGameRunning = false;  // עצור את זמן הריצה
//            handler.removeCallbacks(timerRunnable);  // הסר את הריצה של עדכון הזמן
//        }
//    }
//
//    private void showTimeDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//        SpannableString title = new SpannableString("Game over - Well done!");
//        title.setSpan(new AbsoluteSizeSpan(24, true), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // שינוי גודל לכותרת הראשונה ל-36sp
//        builder.setTitle(title);
//
//        SpannableString subTitle = new SpannableString("You succeeded to reveal all couples");
//        subTitle.setSpan(new AbsoluteSizeSpan(16, true), 0, subTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // שינוי גודל לכותרת המשנה ל-24sp
//        builder.setMessage(subTitle);
//
//
//        // הצגת הזמן והניקוד בשתי שורות
//        String message = "Time: " + (elapsedTime / 1000) + " s\n";  // זמן בשניות
//        message += "Score: " ;  // הניקוד
//
//// יצירת TextView עם טקסט מותאם אישית
//        TextView messageTextView = new TextView(this);
//        messageTextView.setText(message);
//        messageTextView.setGravity(Gravity.CENTER);  // יישור טקסט למרכז
//        messageTextView.setTextSize(20);  // שינוי גודל טקסט לניקוד ולזמן
//
//        builder.setView(messageTextView);  // הגדרת TextView כצפייה בהודעה
//
//
//
////        builder.setTitle("Well done!");
////        builder.setTitle("You succeeded to reveal all couples");
////
////        builder.setMessage("Time: " + (elapsedTime / 1000) + " s");  // הצגת הזמן בשניות
////        builder.setMessage("Score: "  );  // הצגת הניקוד/כסף שנקבל
//        builder.setPositiveButton("Home page", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // כפתור חזרה לדף הבית
//                Intent intent = new Intent(MainRegularActivity.this, MainActivity.class);
//                startActivity(intent);  // התחלת ה-Activity החדש (חזרה לדף הבית)
//            }
//        });
//        builder.setNegativeButton("Yeah!", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // כפתור חזרה לדף הבית
//                Intent intent = new Intent(MainRegularActivity.this, MainRegularActivity.class);
//                startActivity(intent);  // התחלת ה-Activity החדש (חזרה לדף הבית)
//            }
//        });
//        builder.setNeutralButton("Record board", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Intent intent = new Intent(MainRegularActivity.this, RecordBoardActivity.class);
//                startActivity(intent);  // התחלת ה-Activity החדש (חזרה לדף הבית)
//            }
//        });
//        builder.setCancelable(false);  // אם אתה רוצה שהשחקן לא יוכל לדלג על ההודעה לפני שלחץ על כפתור
//        builder.show();
//    }
//}
