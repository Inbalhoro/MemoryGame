package com.horovitz.memorygame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainShop extends AppCompatActivity {
    private TextView gameMoneyInDis;
    private int currentgameMoney = 0;
    Button buyButton1, buyButton2, buyButton3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_shop);

        gameMoneyInDis = findViewById(R.id.gameMoneyFromScores);
        buyButton1 = findViewById(R.id.buy_button1);
        buyButton2 = findViewById(R.id.buy_button2);
        buyButton3 = findViewById(R.id.buy_button3);

        updateScoreDisplay(gameMoneyInDis, currentgameMoney);
        buyButton1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                handlePurchase(buyButton1);
            }
        });
        buyButton2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                handlePurchase(buyButton2);
            }
        });
        buyButton3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                handlePurchase(buyButton3);
            }
        });
    }
    private void handlePurchase(Button button) {
        // שליפת הסכום מהכפתור
        int itemPrice = Integer.parseInt(button.getText().toString().trim());

        SharedPreferences prefs = getSharedPreferences("GameData", MODE_PRIVATE);
        int totalScore = prefs.getInt("totalScore", 0);

        if (totalScore >= itemPrice) {
            // מספיק כסף – עדכון
            int newTotal = totalScore - itemPrice;

            // שמירת הסכום החדש
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("totalScore", newTotal);
            editor.apply();

            // עדכון תצוגה
            gameMoneyInDis.setText(String.valueOf(newTotal));

            // שינוי עיצוב הכפתור
            button.setText("You bought it");
            button.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.blue)); //  ייצרתי צבע בresurce שהשם שלו יהיה blue

            // ביטול אפשרות לחיצה נוספת
            button.setEnabled(false);

        } else {
            // אין מספיק כסף
            Toast.makeText(this, "Not enough money!", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "But keep playing, you can do it", Toast.LENGTH_SHORT).show();

        }
    }


    private void updateScoreDisplay(TextView gameMoneyInDis, int currentgameMoney) {
        // Get SharedPreferences
        SharedPreferences prefs = getSharedPreferences("GameData", MODE_PRIVATE);

        // Get the total score and last game score
        int totalScore = prefs.getInt("totalScore", 0);
        int lastGameScore = prefs.getInt("lastGameScore", 0);

        // Update the UI
        gameMoneyInDis.setText("" + totalScore);
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
            Intent intent = new Intent(MainShop.this, MainActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש
        }
        if (id==R.id.action_settings){
            Intent intent = new Intent(MainShop.this, SettingsActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש
        }
        if (id==R.id.action_recordBoard){
            Intent intent = new Intent(MainShop.this, RecordBoardActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש
        }
        if (id==R.id.action_help){
            Intent intent = new Intent(MainShop.this, helpActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש
        }
        if (id==R.id.action_shop){
            Toast.makeText(this, "You are already here", Toast.LENGTH_SHORT).show();
        }
        if (id==R.id.action_start){
            Intent intent = new Intent(MainShop.this, MainStart.class);
            startActivity(intent); // התחלת ה-Activity החדש
            Toast.makeText(this, "You pressed RESTART -  Please wait a few seconds", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}