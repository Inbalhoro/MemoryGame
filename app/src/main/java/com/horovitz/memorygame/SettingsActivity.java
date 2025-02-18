package com.horovitz.memorygame;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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

        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SettingsActivity.this, "saved", Toast.LENGTH_SHORT).show();
            }
        });
        soundSwitch = findViewById(R.id.switch_sound);
        difficultySpinner = findViewById(R.id.spinner_difficulty);
        timeSpinner = findViewById(R.id.spinner_time);
        themeRadioGroup = findViewById(R.id.radio_group_theme);

        // הגדרת את סוג הקושי
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.difficulty_levels, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,R.array.time_presentation_cards, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultySpinner.setAdapter(adapter);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(adapter1);

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
        SharedPreferences sharedPreferences = getSharedPreferences("GameSettings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String timeSelection = adapterView.getItemAtPosition(i).toString();
                editor.putString("timeSelection", timeSelection);  // שמור את הבחירה
                editor.apply();  // שמירה באופן אסינכרוני
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        themeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // שמור את הנושא שנבחר
        });
    }

}