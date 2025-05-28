package com.horovitz.memorygame;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class helpActivity extends AppCompatActivity {
    // משתנה שיזכור אם נפתחה אפליקציית אימייל
    private boolean emailIntentLaunched = false;
    private EditText emailEditText, subjectEditText, messageEditText;
    private Button sendButtonToMail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_activity);

        // קישור לשדות בטופס
//        emailEditText = findViewById(R.id.emailText);
        subjectEditText = findViewById(R.id.themeofemail);
        messageEditText = findViewById(R.id.textMessage);

    }

    // נשלח כשהמשתמש לוחץ על כפתור שליחה
    public void sendEmail(View view) {
        String userEmail = emailEditText.getText().toString().trim();
        String subject = subjectEditText.getText().toString().trim();
        String message = messageEditText.getText().toString().trim();

        if (userEmail.isEmpty() || subject.isEmpty() || message.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        String developerEmail = "inbal.peer317@gmail.com";//האימייל של היוצרת - אני

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822"); // מציין שמדובר באימייל
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{developerEmail});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, "I'm playing the memory game you created and I'm having some problems. I'd appreciate it if you could help me. " + "\n\n" + message);

        try {
            emailIntentLaunched = true; // זוכר שנשלחה בקשה
            startActivity(Intent.createChooser(intent, "Choose an Email client:"));

            } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "No email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    // כשהאפליקציה חוזרת מהפוקוס (לדוג' אחרי ששלחו אימייל)
    @Override
    protected void onResume() {
        super.onResume();
        if (emailIntentLaunched) {
            emailIntentLaunched = false;

            // ננקה את השדות
//            emailEditText.setText("");
            subjectEditText.setText("");
            messageEditText.setText("");

            // נציג alert
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Thank you for your feedback!")
                    .setMessage("I truly appreciate it and will get back to you as soon as possible. ❤️")
                    .setPositiveButton("Back to game", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // חוזרים לדף הבית
                            Intent back = new Intent(helpActivity.this, helpActivity.class);
                            startActivity(back);
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .show();
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
        if (id == R.id.action_firstpage) {
            startActivity(new Intent(this, MainActivity.class));
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.action_recordBoard) {
            startActivity(new Intent(this, RecordBoardActivity.class));
        } else if (id == R.id.action_shop) {
            startActivity(new Intent(this, MainShop.class));
        } else if (id == R.id.action_help) {
            Toast.makeText(this, "You are already here", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.action_start) {
            startActivity(new Intent(this, MainStart.class));
            Toast.makeText(this, "You pressed RESTART - Please wait a few seconds", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}

