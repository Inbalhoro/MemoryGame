package com.horovitz.memorygame;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;



public class SettingsActivity extends AppCompatActivity {

    private Switch soundSwitch;
    Button saveButton;
    private Spinner difficultySpinner;
    private Spinner timeSpinner;

    private RadioGroup themeRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        soundSwitch = findViewById(R.id.switch_sound);
        SharedPreferences sharedPreferences = getSharedPreferences("GameSettings", MODE_PRIVATE);
        boolean isSoundEnabled = sharedPreferences.getBoolean("isSoundEnabled", true);
        soundSwitch.setChecked(isSoundEnabled);

        // Initialize views first
        difficultySpinner = findViewById(R.id.spinner_difficulty);
        timeSpinner = findViewById(R.id.spinner_time);
        themeRadioGroup = findViewById(R.id.radio_group_theme);

        // Load saved difficulty
        String savedDifficulty = sharedPreferences.getString("selectedDifficulty", "Regular");
        difficultySpinner.setSelection(getDifficultyIndex(savedDifficulty));

        // Load saved time
        String savedTime = sharedPreferences.getString("timeSelection", "Regular");
        timeSpinner.setSelection(getTimeIndex(savedTime));

        // Load saved theme after themeRadioGroup is initialized
        String savedTheme = sharedPreferences.getString("selectedTheme", "Cartoon Characters");
        setThemeSelection(savedTheme);

        // עדכון המוזיקה על פי המצב של ה-Switch
        if (isSoundEnabled) {
            startMusicService();
             } else {
            stopMusicService();
              }


        // שמירה של ההגדרות כאשר המשתמש משנה אותם
        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isSoundEnabled", isChecked); // שמירת מצב הסוויץ'
                editor.apply();
                // שמור את הגדרת הצלילים
                // לדוג' SharedPreferences או כל מנגנון אחר לשמירת הגדרות
                if (isChecked) {
                    startMusicService();
                    Toast.makeText(SettingsActivity.this, "המוזיקה פועלת", Toast.LENGTH_SHORT).show();
                }
                else {
                    stopMusicService();
                    Toast.makeText(SettingsActivity.this, "המוזיקה הופסקה", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.difficulty_levels, android.R.layout.simple_spinner_item);
        difficultySpinner.setAdapter(adapter);


        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,R.array.time_presentation_cards, android.R.layout.simple_spinner_item);
        timeSpinner.setAdapter(adapter1);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        difficultySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View view, int position, long id) {
                String selectedDifficulty = parentView.getItemAtPosition(position).toString();
                editor.putString("selectedDifficulty", selectedDifficulty);
                editor.apply();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // אם לא נבחר דבר
            }
        });
        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String timeSelection = adapterView.getItemAtPosition(i).toString();
                editor.putString("timeSelection", timeSelection);
                editor.apply();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        themeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedRadioButton = findViewById(checkedId);
            String selectedTheme = selectedRadioButton.getText().toString();
            editor.putString("selectedTheme", selectedTheme);
            editor.apply();
        });


        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                RadioGroup radioGroup = findViewById(R.id.spinner_difficulty);
//                int selectedId = radioGroup.getCheckedRadioButtonId();


                // שמירה של ההגדרות ב-SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("GameSettings", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                // שמירה של רמת הקושי
                String selectedDifficulty = difficultySpinner.getSelectedItem().toString();
                editor.putString("selectedDifficulty", selectedDifficulty);

                // שמירה של נושא המשחק
                int selectedThemeId = themeRadioGroup.getCheckedRadioButtonId();
                RadioButton selectedRadioButton = findViewById(selectedThemeId);
                String selectedTheme = selectedRadioButton.getText().toString();
                editor.putString("selectedTheme", selectedTheme);

                // שמירה של הגדרות הצלילים (כבר קיימת)
                boolean isSoundEnabled = soundSwitch.isChecked();
                editor.putBoolean("isSoundEnabled", isSoundEnabled);

                editor.apply(); // שמירה באופן אסינכרוני
                    Toast.makeText(SettingsActivity.this, "ההגדרות נשמרו", Toast.LENGTH_SHORT).show();
//                // מעבר לעמוד המשחק
//                if (selectedDifficulty.equals("Hard")) {
//                    Intent intent = new Intent(SettingsActivity.this, MainHardActivity.class);
//                    startActivity(intent); // התחלת ה-Activity החדש
//                } else if (selectedDifficulty.equals("Easy")) {
//                    Intent intent = new Intent(SettingsActivity.this, MainEasyActivity.class);
//                    startActivity(intent); // התחלת ה-Activity החדש
//                } else {
//                    Intent intent = new Intent(SettingsActivity.this, MainGirlsActivity.class);
//                    startActivity(intent); // התחלת ה-Activity החדש
//                    Toast.makeText(SettingsActivity.this, "ההגדרות נשמרו", Toast.LENGTH_SHORT).show();
//                }
            }
        });
    }

    private void stopMusicService() {
        Intent serviceIntent = new Intent(SettingsActivity.this, MusicService.class);
        stopService(serviceIntent); // עוצר את המוזיקה
    }

    private void startMusicService() {
        Intent serviceIntent = new Intent(SettingsActivity.this, MusicService.class);
        startService(serviceIntent); // מתחיל את המוזיקה
    }

    @Override
    protected void onStop() {
        super.onStop();
        // שמור את המצב של הצלילים כשה-Activity נפסק
        SharedPreferences sharedPreferences = getSharedPreferences("GameSettings", MODE_PRIVATE);
        boolean isSoundEnabled = soundSwitch.isChecked();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isSoundEnabled",  soundSwitch.isChecked());
        editor.apply();
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
            Toast.makeText(this, "You selected login", LENGTH_SHORT).show();
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש
        }
        if (id==R.id.action_settings){
            Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש
        }
        if (id==R.id.action_start){
            Intent intent = new Intent(SettingsActivity.this, MainStart.class);
            Toast.makeText(this, "You selected start", LENGTH_SHORT).show();

        }
        return super.onOptionsItemSelected(item);
    }

    private int getDifficultyIndex(String difficulty) {
        String[] difficulties = getResources().getStringArray(R.array.difficulty_levels);
        for (int i = 0; i < difficulties.length; i++) {
            if (difficulties[i].equals(difficulty)) {
                return i;
            }
        }
        return 0;
    }

    private int getTimeIndex(String time) {
        String[] times = getResources().getStringArray(R.array.time_presentation_cards);
        for (int i = 0; i < times.length; i++) {
            if (times[i].equals(time)) {
                return i;
            }
        }
        return 0;
    }

    private void setThemeSelection(String theme) {
        String[] themes = getResources().getStringArray(R.array.themes);
        for (int i = 0; i < themes.length; i++) {
            if (themes[i].equals(theme)) {
                RadioButton radioButton = (RadioButton) themeRadioGroup.getChildAt(i);
                if (radioButton != null) {
                    radioButton.setChecked(true);
                }
                break;
            }
        }
    }
}