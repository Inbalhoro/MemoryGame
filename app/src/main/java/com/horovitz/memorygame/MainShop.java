package com.horovitz.memorygame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainShop extends AppCompatActivity {
    private TextView gameMoneyInDis;
    private int currentgameMoney = 0;
    private Button buyButton1, buyButton2, buyButton3;
    private LinearLayout backgroundSelectorLayout;
    private LinearLayout backgroundOptionsLayout;
    private static final String[] BACKGROUND_KEYS = {"bg1", "bg2", "bg3"};
    private static final int[] BACKGROUND_RESOURCES = {
        R.drawable.backinshop1,
        R.drawable.backinshop2,
        R.drawable.backinshop3
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_shop);

        // Initialize views
        gameMoneyInDis = findViewById(R.id.gameMoneyFromScores);
        buyButton1 = findViewById(R.id.buy_button1);
        buyButton2 = findViewById(R.id.buy_button2);
        buyButton3 = findViewById(R.id.buy_button3);
        backgroundSelectorLayout = findViewById(R.id.backgroundSelectorLayout);
        backgroundOptionsLayout = findViewById(R.id.backgroundOptionsLayout);

        updateScoreDisplay(gameMoneyInDis, currentgameMoney);

        setupBackgroundSelector();

        loadButtonState();
        checkAndShowBackgroundSelector();

        // Set onClick listeners for buttons
        buyButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlePurchase(buyButton1, BACKGROUND_KEYS[0], BACKGROUND_RESOURCES[0]);
            }
        });

        buyButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlePurchase(buyButton2, BACKGROUND_KEYS[1], BACKGROUND_RESOURCES[1]);
            }
        });

        buyButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlePurchase(buyButton3, BACKGROUND_KEYS[2], BACKGROUND_RESOURCES[2]);
            }
        });


    }

    private void setupBackgroundSelector() {
        // Always add default background option
        addBackgroundOption(R.drawable.backgroundsingleplayer);

        // Add purchased backgrounds
        SharedPreferences prefs = getSharedPreferences("GameData", MODE_PRIVATE);
        for (int i = 0; i < BACKGROUND_KEYS.length; i++) {
            if (prefs.getBoolean(BACKGROUND_KEYS[i], false)) {
                addBackgroundOption(BACKGROUND_RESOURCES[i]);
            }
        }
    }

    private void addBackgroundOption(int backgroundRes) {
        ImageView bgOption = new ImageView(this); // יוצרת תמונה חדשה

        // מגדירה לה גודל של 70dp על 70dp עם שוליים
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpToPx(70), dpToPx(70));
        params.setMargins(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4));
        bgOption.setLayoutParams(params);

        // מציבה את התמונה של הרקע שנשלח לפונקציה
        bgOption.setImageResource(backgroundRes);

        // מגדירה שהתמונה תתאים למרכז ותחתוך את מה שמיותר
        bgOption.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // מסגרת
        bgOption.setBackgroundResource(R.drawable.border);

        // שומרת את המזהה של הרקע ב־tag כדי שנוכל לדעת מה נבחר
        bgOption.setTag(backgroundRes);

        // מאזין ללחיצה
        bgOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackgroundSelected(v);
            }
        });

        // מוסיפה את הרקע למסך
        backgroundOptionsLayout.addView(bgOption);


    }

    private void checkAndShowBackgroundSelector() {
        SharedPreferences prefs = getSharedPreferences("GameData", MODE_PRIVATE);
        int selectedBackground = prefs.getInt("selectedBackground", R.drawable.backgroundsingleplayer);

        boolean hasPurchasedBackgrounds = false;
        for (String key : BACKGROUND_KEYS) {
            if (prefs.getBoolean(key, false)) {
                hasPurchasedBackgrounds = true;
                break;
            }
        }
        backgroundSelectorLayout.setVisibility(hasPurchasedBackgrounds ? View.VISIBLE : View.GONE);
    }



    public void onBackgroundSelected(View view) {
        try {
            int backgroundRes = (int) view.getTag();

                ImageView selectedView = (ImageView) view;
                backgroundRes = (int) selectedView.getTag();


                // Save the selected background
                SharedPreferences prefs = getSharedPreferences("GameData", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("selectedBackground", backgroundRes);
                editor.apply();

                // Highlight the selected background
                for (int i = 0; i < backgroundOptionsLayout.getChildCount(); i++) {
                    View child = backgroundOptionsLayout.getChildAt(i);
                    child.setBackgroundResource(R.drawable.border);
                }
                view.setBackgroundResource(R.drawable.border);

                Toast.makeText(this, "Background selected!", Toast.LENGTH_SHORT).show();

        }
        catch (Exception ex){
            Log.d("INBA", ex.toString() );
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
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
        int totalScore = prefs.getInt("totalScore", Constants.INITIAL_SCORE);

        // Update the UI
        gameMoneyInDis.setText(String.valueOf(totalScore));
    }
    private void handlePurchase(Button button, String backgroundKey, int backgroundRes) {
        SharedPreferences prefs = getSharedPreferences("GameData", MODE_PRIVATE);

        // בדיקה אם הכפתור כבר נרכש
        boolean alreadyBought = prefs.getBoolean("button" + button.getId(), false);
        if (alreadyBought) {
            Toast.makeText(this, "You already bought this!", Toast.LENGTH_SHORT).show();
            return;
        }

        int itemPrice = Integer.parseInt(button.getText().toString().trim());
        int totalScore = prefs.getInt("totalScore", Constants.INITIAL_SCORE);

        if (totalScore >= itemPrice) {
            int newTotal = totalScore - itemPrice;
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("totalScore", newTotal);
            editor.putBoolean(backgroundKey, true); // הרקע נרכש
            editor.putBoolean("button" + button.getId(), true); // שומר שהכפתור נרכש
            editor.apply();

            gameMoneyInDis.setText(String.valueOf(newTotal));
            button.setText("You bought it");
            button.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.blue));
            button.setEnabled(false);

            addBackgroundOption(backgroundRes); // מציג את הרקע החדש למעלה
            checkAndShowBackgroundSelector();
        } else {
            Toast.makeText(this, "Not enough money!", Toast.LENGTH_SHORT).show();
        }


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.popupmenu_main, menu);
        GameDatabaseHelper.setIconInMenu(this, menu, R.id.action_firstpage, R.string.firstpage, R.drawable.baseline_home);
        GameDatabaseHelper.setIconInMenu(this, menu, R.id.action_settings, R.string.setting, R.drawable.baseline_settings_24);
        GameDatabaseHelper.setIconInMenu(this, menu, R.id.action_shop, R.string.shop, R.drawable.baseline_shopping_cart);
        GameDatabaseHelper.setIconInMenu(this, menu, R.id.action_recordBoard, R.string.recordBoard, R.drawable.baseline_record);
        GameDatabaseHelper.setIconInMenu(this, menu, R.id.action_help, R.string.help, R.drawable.baseline_help);
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
