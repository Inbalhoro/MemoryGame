package com.horovitz.memorygame;
//
//import androidx.appcompat.app.AppCompatActivity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//
//import com.horovitz.memorygame.R;
//
//public class helpActivity extends AppCompatActivity {
//    EditText message;
//    EditText subject;
//    String tomyemail="inbal.peer317@gmail.com";
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.help_activity); // קישור ל-XML שלך
//
//        message = findViewById(R.id.textMessage);
//        subject = findViewById(R.id.themeofemail);
//    }
//    public void sendEmail(View view) {
//        Intent email = new Intent(Intent.ACTION_SEND); //המשלוח בפועל יתבצע דרך החשבון שהוא מחובר איתו באפליקציה.
//        email.putExtra(Intent.EXTRA_EMAIL, new String[]{ tomyemail});  email.putExtra(Intent.EXTRA_SUBJECT, subject.getText().toString());  email.putExtra(Intent.EXTRA_TEXT, message.getText().toString());
////need this to prompts email client only
//        email.setType("message/rfc822"); //זו הדרך של Android להבין שצריך להפעיל אפליקציית מייל
//        startActivity(Intent.createChooser(email, "Choose an Email client :"));
//    }
//}

import android.content.Intent;
import android.net.Uri;
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

    private EditText emailEditText, messageEditText;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {//8787jryjgjtלמחוק
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_activity);

        emailEditText = findViewById(R.id.emailText);
        messageEditText = findViewById(R.id.themeofemail);
        sendButton = findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });
    }

    private void sendEmail() {
        String userEmail = emailEditText.getText().toString().trim();
        String message = messageEditText.getText().toString().trim();

        if (userEmail.isEmpty() || message.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // אימייל של יוצרת המשחק – שימי כאן את האימייל שלך
        String developerEmail = "inbal.peer317@gmail.com";

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + developerEmail));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{developerEmail});
        intent.putExtra(Intent.EXTRA_SUBJECT, "פנייה מיוזמת המשחק");
        intent.putExtra(Intent.EXTRA_TEXT, "אימייל המשתמשת: " + userEmail + "\n\n" + message);

        try {
            startActivity(Intent.createChooser(intent, "בחרי אפליקציה לשליחת דוא\"ל"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "אין אפליקציה מתאימה לשליחת אימייל", Toast.LENGTH_SHORT).show();
        }
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
            Intent intent = new Intent(helpActivity.this, MainActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש
        }
        if (id==R.id.action_settings){
            Toast.makeText(this, "You can change the settings ONLY from the single player game!", Toast.LENGTH_SHORT).show();
        }
        if (id==R.id.action_recordBoard){
            Intent intent = new Intent(helpActivity.this, RecordBoardActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש
        }
        if (id==R.id.action_shop) {
            Intent intent = new Intent(helpActivity.this, MainShop.class);
            startActivity(intent); // התחלת ה-Activity החדש        }
        }
        if (id==R.id.action_help){
            Toast.makeText(this, "You are already here", Toast.LENGTH_SHORT).show();
        }
        if (id==R.id.action_start){
            Intent intent = new Intent(helpActivity.this, MainStart.class);
            startActivity(intent); // התחלת ה-Activity החדש
            Toast.makeText(this, "You pressed RESTART -  Please wait a few seconds", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
