package com.horovitz.memorygame;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button navigateButton;
    private Button navigateWithButton;

    SharedPreferences sharedPreferences;
    String selectedDifficulty;
    private String selectedImage = ""; // משתנה לשמירת התמונה שנבחרה


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); ///כפתור הזזה בין מסך למסך בנים בנות

        SharedPreferences sharedPreferences = getSharedPreferences("GameSettings", MODE_PRIVATE);

        String time = sharedPreferences.getString("selectedTime", "Regular"); // ברירת מחדל: "Regular"
        String theme = sharedPreferences.getString("selectedTheme", "Cartoon Characters"); // ברירת מחדל: "דמויות מצוירות"
        boolean isSoundEnabled = sharedPreferences.getBoolean("isSoundEnabled", true); // ברירת מחדל: true
        //    Log.d("RINAT",selectedDifficulty);

        Intent serviceIntent = new Intent(MainActivity.this, MusicService.class);
        startService(serviceIntent); // הפעלת מוזיקה ברגע ש-Activity נפתח

        navigateButton = findViewById(R.id.navigateButtonSingle);
        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SelectedDifficulty", "SelectedDifficulty = " + selectedDifficulty);
//                if ("Hard".equals(selectedDifficulty)) {
//                    Intent intent = new Intent(MainActivity.this, MainHardActivity.class);
//                    startActivity(intent);
//                } else if ("Easy".equals(selectedDifficulty)) {
//                    Intent intent = new Intent(MainActivity.this, MainEasyActivity.class);
//                    startActivity(intent);
//                } else {
                Intent intent = new Intent(MainActivity.this, MainRegularActivity.class);
                startActivity(intent);
            }

                // יצירת AlertDialog עם שני כפתורים
//                new AlertDialog.Builder(MainActivity.this)
//                    .setTitle("בחר מסך")
//                        .setMessage("בחר לאן אתה רוצה לעבור:")
//                        .setPositiveButton("למסך בנות", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    if ("Hard".equals(difficulty)) {
//                        Intent intent = new Intent(MainActivity.this, MainHardActivity.class);
//                        startActivity(intent);
//                    }
//                    else if("Easy".equals(difficulty)){
//                        Intent intent = new Intent(MainActivity.this, MainEasyActivity.class);
//                        startActivity(intent);
//                    }
//                    // דיפולט לעמוד הרגיל MainActivity
//                    Intent intent = new Intent(MainActivity.this, MainGirlsActivity.class);
//                    startActivity(intent);
//                }
//            })
//                    .setNegativeButton("למסך בנים", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    // כפתור שני - מעבר למסך אחר
//                    Intent intent = new Intent(MainActivity.this, MainBoysActivity.class);
//                    startActivity(intent);
//                }
//            })
//                    .show();







        });

        navigateWithButton = findViewById(R.id.navigateWithFriendButton);
        navigateWithButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Enter the players's names:");

                // יצירת Layout עבור הדיאלוג
                LinearLayout layout = new LinearLayout(MainActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setGravity(Gravity.CENTER); // הצבת כל התוכן במרכז


                final EditText nameEditText1 = new EditText(MainActivity.this);
                nameEditText1.setHint("Player 1");
                layout.addView(nameEditText1);

                final EditText nameEditText2 = new EditText(MainActivity.this);
                nameEditText2.setHint("Player 2");
                layout.addView(nameEditText2);

                builder.setView(layout);

                TextView title = new TextView(MainActivity.this);
                title.setText("בחר תמונה:");
                title.setGravity(Gravity.CENTER); // ממרכזים את הכותרת
                layout.addView(title);


                // יצירת שני LinearLayouts אופקיים עבור כל שורה
                LinearLayout row1 = new LinearLayout(MainActivity.this);
                row1.setOrientation(LinearLayout.HORIZONTAL); // שורה אחת
                row1.setGravity(Gravity.CENTER); // הצבת התמונות במרכז של השורה
                LinearLayout row2 = new LinearLayout(MainActivity.this);
                row2.setOrientation(LinearLayout.HORIZONTAL); // שורה שנייה
                row2.setGravity(Gravity.CENTER); // הצבת התמונות במרכז של השורה


                ImageView imageView1 = createImageView("1", R.drawable.image1);
                ImageView imageView2 = createImageView("2", R.drawable.animal1);
                ImageView imageView3 = createImageView("3", R.drawable.flag13);
                ImageView imageView4 = createImageView("4", R.drawable.food9);
                row1.addView(imageView1);
                row1.addView(imageView2);
                row2.addView(imageView3);
                row2.addView(imageView4);

                // הוספת השורות ל-layout הראשי
                layout.addView(row1);
                layout.addView(row2);

                builder.setMessage("")
                        .setPositiveButton("Start!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // כפתור אחד - מעבר למסך MainActivity
                                String player1Name = nameEditText1.getText().toString();
                                String player2Name = nameEditText2.getText().toString();

                                // אפשר לוודא שהשמות לא ריקים, או להגדיר ערך ברירת מחדל אם הם ריקים
                                if (player1Name.isEmpty()) {
                                    player1Name = "Player 1"; // שם ברירת מחדל
                                }
                                if (player2Name.isEmpty()) {
                                    player2Name = "Player 2"; // שם ברירת מחדל
                                }

                                Intent intent = new Intent(MainActivity.this, MainPlayWithFriends.class);
                                intent.putExtra("selectedImage", selectedImage); // שולחים את התמונה שנבחרה
                                intent.putExtra("player1Name", player1Name); // שולחים את שם השחקן הראשון
                                intent.putExtra("player2Name", player2Name); // שולחים את שם השחקן השני
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // כפתור שני - מעבר למסך אחר
                                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        });
                builder.show();
            }


        });


    }

    private ImageView createImageView(final String imageName, int imageRes) {
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(imageRes);

        // קביעת גודל אחיד לתמונה
        int size = getResources().getDimensionPixelSize(R.dimen.image_size); // לדוגמה, 100dp
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        imageView.setLayoutParams(params);

        // הגדרת מסגרת סביב התמונה
        imageView.setPadding(10, 10, 10, 10);
        imageView.setBackgroundResource(R.drawable.border); // border הוא קובץ עיצוב שיצור את המסגרת


        // מאזין ללחיצה על התמונה
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // עדכון התמונה שנבחרה
                selectedImage = imageName;
                System.out.println("בחרת את: " + imageName);
            }
        });

        return imageView;
    }


//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        // עצירת המוזיקה בעת סגירת ה-Activity
//        Intent serviceIntent = new Intent(MainActivity.this, MusicService.class);
//        stopService(serviceIntent);
//    }


    @Override
    protected void onStart() {
        super.onStart();

        // Reading the settings from SharedPreferences
        sharedPreferences = getSharedPreferences("GameSettings", MODE_PRIVATE);
        selectedDifficulty = sharedPreferences.getString("selectedDifficulty", "Regular");

        String theme = sharedPreferences.getString("selectedTheme", "Cartoon Characters"); // ברירת מחדל: "דמויות מצוירות"
        boolean isSoundEnabled = sharedPreferences.getBoolean("isSoundEnabled", true); // ברירת מחדל: true

        Intent serviceIntent = new Intent(MainActivity.this, MusicService.class);
        startService(serviceIntent); // הפעלת מוזיקה ברגע ש-Activity נפתח

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
            Toast.makeText(this, "You selected login", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש
        }
        if (id==R.id.action_settings){
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש
        }
        if (id==R.id.action_start){
            Intent intent = new Intent(MainActivity.this, MainStart.class);
            Toast.makeText(this, "You selected start", Toast.LENGTH_SHORT).show();

        }
        return super.onOptionsItemSelected(item);
       }
}
