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

        // Set onClick listeners for buttons
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

        // Load button states from SharedPreferences
        loadButtonState();
    }

    private void handlePurchase(Button button) {
        // Retrieve the price from the button's text
        int itemPrice = Integer.parseInt(button.getText().toString().trim());

        SharedPreferences prefs = getSharedPreferences("GameData", MODE_PRIVATE);
        int totalScore = prefs.getInt("totalScore", 0);

        if (totalScore >= itemPrice) {
            // Sufficient funds – Update total score
            int newTotal = totalScore - itemPrice;

            // Save new score to SharedPreferences
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("totalScore", newTotal);
            editor.apply();

            // Update the display
            gameMoneyInDis.setText(String.valueOf(newTotal));

            // Mark the item as bought and update the button state
            button.setText("You bought it");
            button.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.blue));
            button.setEnabled(false);

            // Save the state of the button
            SharedPreferences.Editor stateEditor = prefs.edit();
            stateEditor.putBoolean("button" + button.getId(), true);
            stateEditor.apply();
        } else {
            // Not enough money
            Toast.makeText(this, "Not enough money!", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "But keep playing, you can do it", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadButtonState() {
        SharedPreferences prefs = getSharedPreferences("GameData", MODE_PRIVATE);

        // Check if each button was bought previously and update their states
        if (prefs.getBoolean("button" + R.id.buy_button1, false)) {
            buyButton1.setText("You bought it");
            buyButton1.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.blue));
            buyButton1.setEnabled(false);
        }
        if (prefs.getBoolean("button" + R.id.buy_button2, false)) {
            buyButton2.setText("You bought it");
            buyButton2.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.blue));
            buyButton2.setEnabled(false);
        }
        if (prefs.getBoolean("button" + R.id.buy_button3, false)) {
            buyButton3.setText("You bought it");
            buyButton3.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.blue));
            buyButton3.setEnabled(false);
        }
    }

    private void updateScoreDisplay(TextView gameMoneyInDis, int currentgameMoney) {
        // Get SharedPreferences
        SharedPreferences prefs = getSharedPreferences("GameData", MODE_PRIVATE);

        // Get the total score
        int totalScore = prefs.getInt("totalScore", 0);

        // Update the UI
        gameMoneyInDis.setText(String.valueOf(totalScore));
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
            startActivity(intent);
        }
        if (id==R.id.action_settings){
            Intent intent = new Intent(MainShop.this, SettingsActivity.class);
            startActivity(intent);
        }
        if (id==R.id.action_recordBoard){
            Intent intent = new Intent(MainShop.this, RecordBoardActivity.class);
            startActivity(intent);
        }
        if (id==R.id.action_help){
            Intent intent = new Intent(MainShop.this, helpActivity.class);
            startActivity(intent);
        }
        if (id==R.id.action_shop){
            Toast.makeText(this, "You are already here", Toast.LENGTH_SHORT).show();
        }
        if (id==R.id.action_start){
            Intent intent = new Intent(MainShop.this, MainStart.class);
            startActivity(intent);
            Toast.makeText(this, "You pressed RESTART -  Please wait a few seconds", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
