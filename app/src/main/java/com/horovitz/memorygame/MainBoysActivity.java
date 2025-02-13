
package com.horovitz.memorygame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
public class MainBoysActivity extends AppCompatActivity {
    Button navigateButton; // הכפתור שיעביר אותנו לדף החדש
    Button navigateToFirstPageButton; // הכפתור שיעביר אותנו לדף החדש
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
            timerTextView.setText("זמן: " + (elapsedTime / 1000) + " שניות");

            // מריצים את הריצה הזו כל 100 מילישניות (0.1 שניה)
            if (isGameRunning) {
                handler.postDelayed(this, 100);  // עדכון כל 100 מילישניות
            }
        }
    };

    private ImageButton[] buttons = new ImageButton[16]; // מערך של כפתורים
    private ArrayList<Integer> images = new ArrayList<>(); // תמונות שנמצאות במשחק
    private int[] imageResources = {R.drawable.imageb1, R.drawable.imageb1, R.drawable.imageb2, R.drawable.imageb2,
            R.drawable.imageb3, R.drawable.imageb3, R.drawable.imageb4, R.drawable.imageb4,
            R.drawable.imageb5, R.drawable.imageb5, R.drawable.imageb6, R.drawable.imageb6,
            R.drawable.imageb7, R.drawable.imageb7, R.drawable.imageb8, R.drawable.imageb8}; // כאן תוכל להוסיף את התמונות שלך

    private int firstChoice = -1;
    private int secondChoice = -1;
    private int firstChoiceIndex = -1;
    private int secondChoiceIndex = -1;
    private boolean[] isButtonFlipped = new boolean[16]; // מעקב אם כפתור כבר נחשף
    private boolean[] isButtonMatched = new boolean[16]; // מעקב אם הכפתור כבר נמצא בזוג נכון

    private TextView statusText;
    private Button resetButton; // כפתור איפוס

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityforboys); // קישור ל-XML שלך

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
        navigateToFirstPageButton = findViewById(R.id.navigateToFirstPageButton2);
        navigateToFirstPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // יצירת Intent כדי לעבור לדף החדש
                Intent intent = new Intent(MainBoysActivity.this, MainActivity.class);
                startActivity(intent); // התחלת ה-Activity החדש
            }
        });
        navigateButton = findViewById(R.id.navigateButton); // למצוא את הכפתור במסך
        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // יצירת Intent כדי לעבור לדף החדש
                Intent intent = new Intent(MainBoysActivity.this, MainGirlsActivity.class);
                startActivity(intent); // התחלת ה-Activity החדש
            }


        });

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

    private void startNewGame() {
        // אתחול מחדש של כפתורים (מסתיר את התמונות)
        for (int i = 0; i < 16; i++) {
            buttons[i].setImageResource(android.R.color.transparent);
            isButtonFlipped[i] = false; // לא נחשף
            isButtonMatched[i] = false; // כפתור לא נמצא בזוג נכון
        }

        startTime = System.currentTimeMillis();  // אתחול זמן ההתחלה (במילישניות)
        isGameRunning = true;  // המשחק רץ

        handler.postDelayed(timerRunnable, 100);  // כל 100 מילישניות

        // אקראי מחדש את התמונות
        images.clear();
        for (int i = 0; i < 16; i++) {
            images.add(imageResources[i]);
        }
        Collections.shuffle(images);

        // איפוס משתנים
        firstChoice = -1;
        secondChoice = -1;
        firstChoiceIndex = -1;
        secondChoiceIndex = -1;

        // עדכון טקסט סטטוס
        statusText.setText("התחל לשחק!");
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
                statusText.setText("זוג נכון!");
                isButtonMatched[firstChoiceIndex] = true; // הצבת הכפתור הראשון ככפתור תואם
                isButtonMatched[secondChoiceIndex] = true; // הצבת הכפתור השני ככפתור תואם
                resetChoices();
                setclickable(true);
            }

            // אם התמונות לא תואמות
            else {
                statusText.setText("ניסית זוג לא נכון.");
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
                }, 700); // השהייה של שנייה לפני החבאת התמונות
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
            // יצירת Intent כדי לעבור לדף החדש
            Intent intent = new Intent(MainBoysActivity.this, MainActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש

        }
        if (id==R.id.action_settings){
            Intent intent = new Intent(MainBoysActivity.this, SettingsActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש
        }
        if (id==R.id.action_start){
            Toast.makeText(this, "You selected start", Toast.LENGTH_SHORT).show();

            // יצירת Intent כדי לעבור לדף החדש
            Intent intent = new Intent(MainBoysActivity.this, MainActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש


        }
        return super.onOptionsItemSelected(item);
    }
    private void setclickable(boolean b) {
        for (int i = 0; i < 16; i++) {
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
            statusText.setText("המשחק הסתיים! כל הזוגות נחשפו.");
            showTimeDialog();
            isGameRunning = false;  // עצור את זמן הריצה
            handler.removeCallbacks(timerRunnable);  // הסר את הריצה של עדכון הזמן
        }
    }

    private void showTimeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("המשחק הסתיים!");
        builder.setMessage("הזמן שלך: " + (elapsedTime / 1000) + " שניות");  // הצגת הזמן בשניות

        builder.setPositiveButton("חזור לדף הבית", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // כפתור חזרה לדף הבית
                Intent intent = new Intent(MainBoysActivity.this, MainGirlsActivity.class);
                startActivity(intent);  // התחלת ה-Activity החדש (חזרה לדף הבית)
            }
        });

        builder.setCancelable(false);  // אם אתה רוצה שהשחקן לא יוכל לדלג על ההודעה לפני שלחץ על כפתור
        builder.show();
    }
}
