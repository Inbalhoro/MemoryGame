package com.horovitz.memorygame;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;



public class SettingsActivity extends AppCompatActivity {

    private Switch soundSwitch;
    private Spinner difficultySpinner;
    private RadioGroup themeRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        soundSwitch = findViewById(R.id.switch_sound);
        difficultySpinner = findViewById(R.id.spinner_difficulty);
        themeRadioGroup = findViewById(R.id.radio_group_theme);

        // הגדרת את סוג הקושי
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.difficulty_levels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultySpinner.setAdapter(adapter);

        // שמירה של ההגדרות כאשר המשתמש משנה אותם
        soundSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // שמור את הגדרת הצלילים
            // לדוג' SharedPreferences או כל מנגנון אחר לשמירת הגדרות
        });

        difficultySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View view, int position, long id) {
                // שמור את הקושי שנבחר
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // אם לא נבחר דבר
            }
        });

        themeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // שמור את הנושא שנבחר
        });
    }

}