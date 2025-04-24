package com.horovitz.memorygame;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainShop extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_shop);  // ודא שה-XML שלך נקרא נכון
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.action_firstpage){
            Intent intent = new Intent(MainShop.this, MainActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש
        }
        if (id==R.id.action_settings) {
            Toast.makeText(this, "You can change the settings only when you are at a single game player", Toast.LENGTH_SHORT).show();
        }
        if (id==R.id.action_start){
            Toast.makeText(this, "You selected restart - Please wait a few seconds", Toast.LENGTH_SHORT).show();
            // יצירת Intent כדי לעבור לדף החדש
            Intent intent = new Intent(MainShop.this, MainStart.class);
            startActivity(intent); // התחלת ה-Activity החדש
        }
        return super.onOptionsItemSelected(item);
    }
}
    //        // איפוס המידע
//        layout = findViewById(R.id.layout);  // מתחבר ל-LinearLayout הפנימי ב-ScrollView
//
//        // יצירת שני LinearLayouts אופקיים עבור כל שורה
//        LinearLayout row1 = new LinearLayout(MainShop.this);
//        row1.setOrientation(LinearLayout.HORIZONTAL); // שורה אחת
//        row1.setGravity(Gravity.CENTER); // הצבת התמונות במרכז של השורה
//
//        LinearLayout row2 = new LinearLayout(MainShop.this);
//        row2.setOrientation(LinearLayout.HORIZONTAL); // שורה שנייה
//        row2.setGravity(Gravity.CENTER); // הצבת התמונות במרכז של השורה
//
//        // הוספת תמונות לשורות
//        ImageView imageView1 = createImageView("1", R.drawable.image1);
//        ImageView imageView2 = createImageView("2", R.drawable.animal1);
//        ImageView imageView3 = createImageView("3", R.drawable.flag13);
//        ImageView imageView4 = createImageView("4", R.drawable.food9);
//
//        row1.addView(imageView1);
//        row1.addView(imageView2);
//
//        row2.addView(imageView3);
//        row2.addView(imageView4);
//
//        // הוספת השורות ל-layout הראשי בתוך ה-ScrollView
//        layout.addView(row1);
//        layout.addView(row2);
//    }
//
//    private ImageView createImageView(final String imageName, int imageRes) {
//        ImageView imageView = new ImageView(this);
//        imageView.setImageResource(imageRes);
//
//        // קביעת גודל אחיד לתמונה
//        int size = getResources().getDimensionPixelSize(R.dimen.image_size); // לדוגמה, 100dp
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
//        imageView.setLayoutParams(params);
//
//        // הגדרת מסגרת סביב התמונה
//        imageView.setPadding(10, 10, 10, 10);
//        imageView.setBackgroundResource(R.drawable.border); // border הוא קובץ עיצוב שיצור את המסגרת
//
//        // מאזין ללחיצה על התמונה
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // עדכון התמונה שנבחרה
//                selectedImage = imageName;
//                System.out.println("בחרת את: " + imageName);
//            }
//        });
//
//        return imageView;
//    }


