package com.horovitz.memorygame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private Switch soundSwitch;
    private Spinner difficultySpinner;
    private Spinner timeSpinner;
    private RadioGroup themeRadioGroup;
    private Button saveButton;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences("GameSettings", MODE_PRIVATE);

        soundSwitch = findViewById(R.id.switch_sound);
        difficultySpinner = findViewById(R.id.spinner_difficulty);
        timeSpinner = findViewById(R.id.spinner_time);
        themeRadioGroup = findViewById(R.id.radio_group_theme);
        saveButton = findViewById(R.id.saveButton);

        // Load saved values and update UI
        boolean isSoundEnabled = sharedPreferences.getBoolean("isSoundEnabled", true);
        soundSwitch.setChecked(isSoundEnabled);

        ArrayAdapter<CharSequence> difficultyAdapter = ArrayAdapter.createFromResource(this, R.array.difficulty_levels, android.R.layout.simple_spinner_item);
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultySpinner.setAdapter(difficultyAdapter);

        ArrayAdapter<CharSequence> timeAdapter = ArrayAdapter.createFromResource(this, R.array.time_presentation_cards, android.R.layout.simple_spinner_item);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(timeAdapter);

        String savedDifficulty = sharedPreferences.getString("selectedDifficulty", "REGULAR");
        difficultySpinner.setSelection(getIndexFromArray(savedDifficulty, R.array.difficulty_levels));

        String savedTime = sharedPreferences.getString("selectedTime", "REGULAR");
        timeSpinner.setSelection(getIndexFromArray(savedTime, R.array.time_presentation_cards));

        String savedTheme = sharedPreferences.getString("selectedTheme", "CARTOON_CHARACTERS");
        setThemeSelection(savedTheme);

        // Update music service based on switch state
        if (isSoundEnabled) {
            startMusicService();
        } else {
            stopMusicService();
        }

        // Sound switch saves immediately
        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isSoundEnabled", isChecked);
                editor.apply();

                if (isChecked) {
                    startMusicService();
                    Toast.makeText(SettingsActivity.this, "Music is now playing", Toast.LENGTH_SHORT).show();
                } else {
                    stopMusicService();
                    Toast.makeText(SettingsActivity.this, "Music is now stopped", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Save CHANGES
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSettings();
                Toast.makeText(SettingsActivity.this, "Settings saved", Toast.LENGTH_SHORT).show();
                // Navigate back to MainActivity or wherever you want
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // optional: close settings activity
            }
        });
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Save difficulty (exact string from spinner)
        String selectedDifficulty = difficultySpinner.getSelectedItem().toString();
        editor.putString("selectedDifficulty", selectedDifficulty);
        Log.d("DIFFICULTY_CHECK", "Loaded difficulty: " + selectedDifficulty);


        // Save time
        String selectedTime = timeSpinner.getSelectedItem().toString();
        editor.putString("selectedTime", selectedTime);

        // Save theme (get checked radio button text)
        int selectedThemeId = themeRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedThemeId);
        if (selectedRadioButton != null) {
            String selectedTheme = selectedRadioButton.getText().toString().toUpperCase().replace(" ","_");
            editor.putString("selectedTheme", selectedTheme);
        }

        // Save sound enabled state (already saved in switch listener but no harm repeating)
        boolean isSoundEnabled = soundSwitch.isChecked();
        editor.putBoolean("isSoundEnabled", isSoundEnabled);

        editor.apply();  // async save
    }

    private int getIndexFromArray(String value, int arrayResId) {
        String[] array = getResources().getStringArray(arrayResId);
        for (int i = 0; i < array.length; i++) {
            if (array[i].equalsIgnoreCase(value)) {
                return i;
            }
        }
        return 0; // fallback to first item
    }

    private void setThemeSelection(String theme) {
        String[] themes = getResources().getStringArray(R.array.themes);
        for (int i = 0; i < themes.length; i++) {
            if (themes[i].equalsIgnoreCase(theme)) {
                RadioButton radioButton = (RadioButton) themeRadioGroup.getChildAt(i);
                if (radioButton != null) {
                    radioButton.setChecked(true);
                }
                break;
            }
        }
    }

    private void stopMusicService() {
        Intent serviceIntent = new Intent(SettingsActivity.this, MusicService.class);
        stopService(serviceIntent);
    }

    private void startMusicService() {
        Intent serviceIntent = new Intent(SettingsActivity.this, MusicService.class);
        startService(serviceIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.popupmenu_main, menu);
        GameDatabaseHelper.setIconInMenu(this,
                menu,
                R.id.action_firstpage,
                R.string.firstpage,
                R.drawable.baseline_home);
        GameDatabaseHelper.setIconInMenu(this,
                menu,
                R.id.action_settings,
                R.string.setting,
                R.drawable.baseline_settings_24);
        GameDatabaseHelper.setIconInMenu(this,
                menu,
                R.id.action_shop,
                R.string.shop,
                R.drawable.baseline_shopping_cart);
        GameDatabaseHelper.setIconInMenu(this,
                menu,
                R.id.action_recordBoard,
                R.string.recordBoard,
                R.drawable.baseline_record);
        GameDatabaseHelper.setIconInMenu(this,
                menu,
                R.id.action_help,
                R.string.help,
                R.drawable.baseline_help);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_firstpage) {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_settings) {
            Toast.makeText(this, "You are already here", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_recordBoard) {
            Intent intent = new Intent(SettingsActivity.this, RecordBoardActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_shop) {
            Intent intent = new Intent(SettingsActivity.this, MainShop.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_help) {
            Intent intent = new Intent(SettingsActivity.this, helpActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_start) {
            Intent intent = new Intent(SettingsActivity.this, MainStart.class);
            startActivity(intent);
            Toast.makeText(this, "You pressed RESTART - Please wait a few seconds", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
