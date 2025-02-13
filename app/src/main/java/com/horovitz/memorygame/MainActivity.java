package com.horovitz.memorygame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    private Button navigateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main); ///כפתור הזזה בין מסך למסך בנים בנות


        navigateButton = findViewById(R.id.navigateButton);
        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // יצירת AlertDialog עם שני כפתורים
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("בחר מסך")
                        .setMessage("בחר לאן אתה רוצה לעבור:")
                        .setPositiveButton("למסך בנות", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // כפתור אחד - מעבר למסך MainActivity
                                Intent intent = new Intent(MainActivity.this, MainGirlsActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("למסך בנים", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // כפתור שני - מעבר למסך אחר
                                Intent intent = new Intent(MainActivity.this, MainBoysActivity.class);
                                startActivity(intent);
                            }
                        })
                        .show();
            }

        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.action_login){
            Toast.makeText(this, "You selected login", Toast.LENGTH_SHORT).show();
        }
        if (id==R.id.action_register){
            Toast.makeText(this, "You selected register", Toast.LENGTH_SHORT).show();
        }
        if (id==R.id.action_start){
            Toast.makeText(this, "You selected start", Toast.LENGTH_SHORT).show();

        }
        return super.onOptionsItemSelected(item);
       }
}
