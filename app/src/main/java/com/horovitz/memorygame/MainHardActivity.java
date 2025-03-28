
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
//
public class MainHardActivity extends AppCompatActivity {
    Button navigateButton; // הכפתור שיעביר אותנו לדף החדש
    Button navigateToFirstPageButton;
    private long startTime;
    private long elapsedTime;
    private int timeofcards=700; //הזמן של לפני החבאת הקלפים זמן השהייה במצב הנוכחי
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

    private ImageButton[] buttons = new ImageButton[36]; // מערך של כפתורים
    private ArrayList<Integer> images = new ArrayList<>(); // תמונות שנמצאות במשחק
    private int[] imageResources = {
            R.drawable.image1, R.drawable.image1, // Pair 1
            R.drawable.image2, R.drawable.image2, // Pair 2
            R.drawable.image3, R.drawable.image3, // Pair 3
            R.drawable.image4, R.drawable.image4, // Pair 4
            R.drawable.image5, R.drawable.image5, // Pair 5
            R.drawable.image6, R.drawable.image6, // Pair 6
            R.drawable.image7, R.drawable.image7, // Pair 7
            R.drawable.image8, R.drawable.image8, // Pair 8
            R.drawable.imageb1, R.drawable.imageb1, // Pair 9
            R.drawable.image10, R.drawable.image10, // Pair 10
            R.drawable.imageb2, R.drawable.imageb2, // Pair 11
            R.drawable.imageb3, R.drawable.imageb3, // Pair 12
            R.drawable.imageb4, R.drawable.imageb4, // Pair 13
            R.drawable.imageb5, R.drawable.imageb5, // Pair 14
            R.drawable.imageb6, R.drawable.imageb6, // Pair 15
            R.drawable.imageb7, R.drawable.imageb7, // Pair 16
            R.drawable.imageb8, R.drawable.imageb8, // Pair 17
            R.drawable.image9b, R.drawable.image9b  // Pair 18
    }; // כאן תוכל להוסיף את התמונות שלך

    private int firstChoice = -1;
    private int secondChoice = -1;
    private int firstChoiceIndex = -1;
    private int secondChoiceIndex = -1;
    private boolean[] isButtonFlipped = new boolean[36]; // מעקב אם כפתור כבר נחשף
    private boolean[] isButtonMatched = new boolean[36]; // מעקב אם הכפתור כבר נמצא בזוג נכון

    private TextView statusText;
    private Button resetButton; // כפתור איפוס

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_hard); // קישור ל-XML שלך

// קריאת ההגדרות מ-SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("GameSettings", MODE_PRIVATE);
        String difficulty = sharedPreferences.getString("difficulty", "Regular");  // ברירת מחדל היא "Easy"

        String time = sharedPreferences.getString("selectedTime", "Regular"); // ברירת מחדל: "5 שניות"
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
        buttons[16] = findViewById(R.id.button_17);
        buttons[17] = findViewById(R.id.button_18);
        buttons[18] = findViewById(R.id.button_19);
        buttons[19] = findViewById(R.id.button_20);
        buttons[20] = findViewById(R.id.button_21);
        buttons[21] = findViewById(R.id.button_22);
        buttons[22] = findViewById(R.id.button_23);
        buttons[23] = findViewById(R.id.button_24);
        buttons[24] = findViewById(R.id.button_25);
        buttons[25] = findViewById(R.id.button_26);
        buttons[26] = findViewById(R.id.button_27);
        buttons[27] = findViewById(R.id.button_28);
        buttons[28] = findViewById(R.id.button_29);
        buttons[29] = findViewById(R.id.button_30);
        buttons[30] = findViewById(R.id.button_31);
        buttons[31] = findViewById(R.id.button_32);
        buttons[32] = findViewById(R.id.button_33);
        buttons[33] = findViewById(R.id.button_34);
        buttons[34] = findViewById(R.id.button_35);
        buttons[35] = findViewById(R.id.button_36);
        timerTextView = findViewById(R.id.timerTextView);
//        navigateToFirstPageButton = findViewById(R.id.navigateToFirstPageButton);
//        navigateToFirstPageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // יצירת Intent כדי לעבור לדף החדש
//                Intent intent = new Intent(MainGirlsActivity.this, MainActivity.class);
//                startActivity(intent); // התחלת ה-Activity החדש
//            }
//        });




//        navigateButton = findViewById(R.id.navigateButton); // למצוא את הכפתור במסך
//        navigateButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // יצירת Intent כדי לעבור לדף החדש
//                Intent intent = new Intent(MainRegularActivity.this, MainBoysActivity.class);
//                startActivity(intent); // התחלת ה-Activity החדש
//            }
//
//
//        });

        // אתחול ה-TextView
        statusText = findViewById(R.id.statusText);

        // אתחול כפתור האיפוס
        resetButton = findViewById(R.id.resetButton);

        // מיקסום התמונות באופן אקראי
        startNewGame();

        // מאזין ללחיצות על כפתורים
        for (int i = 0; i < 36; i++) {
            final int index = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onButtonClick(index);
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

    //
    private void updateGameSettings(String difficulty, String time, String theme, boolean isSoundEnabled) {
        if (time.equals("Regular")){
            timeofcards = 700;
        } else if(time.equals("Short")){
            timeofcards = 200;
        }else if(time.equals("Long")){
            timeofcards = 1200;
        }

        // עדכון נושא
        if (theme.equals("Cartoon Characters")) {
            // להשתמש בתמונות של חיות
            imageResources = new int [] {R.drawable.image1, R.drawable.image1, R.drawable.image2, R.drawable.image2, R.drawable.image3, R.drawable.image3,
                    R.drawable.image4, R.drawable.image4, R.drawable.image5, R.drawable.image5,
                    R.drawable.image6, R.drawable.image6, R.drawable.image7, R.drawable.image7,
                    R.drawable.image8, R.drawable.image8, R.drawable.imageb1, R.drawable.imageb1,
                    R.drawable.image10, R.drawable.image10, R.drawable.imageb2, R.drawable.imageb2,
                    R.drawable.imageb3, R.drawable.imageb3, R.drawable.imageb4, R.drawable.imageb4,
                    R.drawable.imageb5, R.drawable.imageb5, R.drawable.imageb6, R.drawable.imageb6,
                    R.drawable.imageb7, R.drawable.imageb7, R.drawable.imageb8, R.drawable.imageb8,
                    R.drawable.image9b, R.drawable.image9b  };
        } else if (theme.equals("Animals")) {
            // להשתמש בתמונות של דמויות מצוירות
            imageResources = new int[] {R.drawable.animal1, R.drawable.animal1, R.drawable.animal2, R.drawable.animal2,
                    R.drawable.animal3, R.drawable.animal3, R.drawable.animal4, R.drawable.animal4,
                    R.drawable.animal5, R.drawable.animal5, R.drawable.animal6, R.drawable.animal6,
                    R.drawable.animal7, R.drawable.animal7, R.drawable.animal8, R.drawable.animal8,
                    R.drawable.animal9, R.drawable.animal9, R.drawable.animal10, R.drawable.animal10,
                    R.drawable.animal11, R.drawable.animal11, R.drawable.animal12, R.drawable.animal12,
                    R.drawable.animal13, R.drawable.animal13, R.drawable.animal14, R.drawable.animal14,
                    R.drawable.animal15, R.drawable.animal15, R.drawable.animal16, R.drawable.animal16,
                    R.drawable.animal17, R.drawable.animal17, R.drawable.animal18, R.drawable.animal18};
        }
        else if (theme.equals("Food")) {
            // להשתמש בתמונות של דמויות מצוירות
            imageResources = new int[] {R.drawable.food1, R.drawable.food1, R.drawable.food2, R.drawable.food2,
                    R.drawable.food3, R.drawable.food3, R.drawable.food4, R.drawable.food4,
                    R.drawable.food5, R.drawable.food5, R.drawable.food6, R.drawable.food6,
                    R.drawable.food7, R.drawable.food7, R.drawable.food8, R.drawable.food8,
                    R.drawable.food9, R.drawable.food9, R.drawable.food10, R.drawable.food10,
                    R.drawable.food11, R.drawable.food11, R.drawable.food12, R.drawable.food12,
                    R.drawable.food13, R.drawable.food13, R.drawable.food14, R.drawable.food14,
                    R.drawable.food15, R.drawable.food15, R.drawable.food16, R.drawable.food16,
                    R.drawable.food17, R.drawable.food17, R.drawable.food18, R.drawable.food18};
        }else if (theme.equals("Flags")) {
            // להשתמש בתמונות של דמויות מצוירות
            imageResources = new int[] {R.drawable.flag1, R.drawable.flag1, R.drawable.flag2, R.drawable.flag2,
                    R.drawable.flag3, R.drawable.flag3, R.drawable.flag4, R.drawable.flag4,
                    R.drawable.flag5, R.drawable.flag5, R.drawable.flag6, R.drawable.flag6,
                    R.drawable.flag7, R.drawable.flag7, R.drawable.flag8, R.drawable.flag8,
                    R.drawable.flag9, R.drawable.flag9, R.drawable.flag10, R.drawable.flag10,
                    R.drawable.flag11, R.drawable.flag11, R.drawable.flag12, R.drawable.flag12,
                    R.drawable.flag13, R.drawable.flag13, R.drawable.flag14, R.drawable.flag14,
                    R.drawable.flag15, R.drawable.flag15, R.drawable.flag16, R.drawable.flag16,
                    R.drawable.flag17, R.drawable.flag17, R.drawable.flag18, R.drawable.flag18};
        }

        // אם צלילים מופעלים, תתחיל את המוזיקה, אחרת תפסיק אותה
        if (isSoundEnabled) {
            startMusicService();
        } else {
            stopMusicService();
        }
    }

    private void stopMusicService() {
        Intent serviceIntent = new Intent(MainHardActivity.this, MusicService.class);
        stopService(serviceIntent); // עוצר את המוזיקה
    }

    private void startMusicService() {
        Intent serviceIntent = new Intent(MainHardActivity.this, MusicService.class);
        startService(serviceIntent); // מתחיל את המוזיקה
    }

    private void startNewGame() {
        // אתחול מחדש של כפתורים (מסתיר את התמונות)
        for (int i = 0; i < 36; i++) {
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

    private void onButtonClick(int index) {
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
                }, timeofcards); // השהייה של שנייה לפני החבאת התמונות
            }
        }
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
            Intent intent = new Intent(MainHardActivity.this, MainActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש
        }
        if (id==R.id.action_settings) {
            Intent intent = new Intent(MainHardActivity.this, SettingsActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש
        }
        if (id==R.id.action_start){
            Toast.makeText(this, "You selected start", Toast.LENGTH_SHORT).show();
            // יצירת Intent כדי לעבור לדף החדש
            Intent intent = new Intent(MainHardActivity.this, MainActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש


        }
        return super.onOptionsItemSelected(item);
    }
    private void setclickable(boolean b) {
        for (int i = 0; i < 36; i++) {
            buttons[i].setEnabled(b);
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
        message += "Score: " ;  // הניקוד

// יצירת TextView עם טקסט מותאם אישית
        TextView messageTextView = new TextView(this);
        messageTextView.setText(message);
        messageTextView.setGravity(Gravity.CENTER);  // יישור טקסט למרכז
        messageTextView.setTextSize(20);  // שינוי גודל טקסט לניקוד ולזמן

        builder.setView(messageTextView);  // הגדרת TextView כצפייה בהודעה



//        builder.setTitle("Well done!");
//        builder.setTitle("You succeeded to reveal all couples");
//
//        builder.setMessage("Time: " + (elapsedTime / 1000) + " s");  // הצגת הזמן בשניות
//        builder.setMessage("Score: "  );  // הצגת הניקוד/כסף שנקבל
        builder.setPositiveButton("Home page", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // כפתור חזרה לדף הבית
                Intent intent = new Intent(MainHardActivity.this, MainActivity.class);
                startActivity(intent);  // התחלת ה-Activity החדש (חזרה לדף הבית)
            }
        });
        builder.setNegativeButton("Yeah!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // כפתור חזרה לדף הבית
                Intent intent = new Intent(MainHardActivity.this, MainRegularActivity.class);
                startActivity(intent);  // התחלת ה-Activity החדש (חזרה לדף הבית)
            }
        });
        builder.setNeutralButton("Record board", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainHardActivity.this, RecordBoardActivity.class);
                startActivity(intent);  // התחלת ה-Activity החדש (חזרה לדף הבית)
            }
        });
        builder.setCancelable(false);  // אם אתה רוצה שהשחקן לא יוכל לדלג על ההודעה לפני שלחץ על כפתור
        builder.show();
    }
}
