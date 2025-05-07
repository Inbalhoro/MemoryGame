package com.horovitz.memorygame;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.ArrayList;

public class RecordBoardActivity extends AppCompatActivity {

    ListView gameListView;
    GameDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_board);


        gameListView = findViewById(R.id.gameListView);
        dbHelper = new GameDatabaseHelper(this);

        List<GameResult> games = dbHelper.getAllGames();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                getGameTitles(games));
        gameListView.setAdapter(adapter);

        gameListView.setOnItemClickListener((parent, view, position, id) -> {
            GameResult game = games.get(position);
            showGameDetails(game);
        });
    }

    private List<String> getGameTitles(List<GameResult> games) {
        List<String> titles = new ArrayList<>();
        for (GameResult game : games) {
            titles.add("GAME #" + game.getId());
        }
        return titles;
    }

    private void showGameDetails(GameResult game) {
        String message = "Which game you played: " + game.getGameType() +
                "\nHow long it lasted: " + game.getTime() + " s" +
                "\nScore: " + game.getScore();

        new AlertDialog.Builder(this)
                .setTitle("Game details")
                .setMessage(message)
                .setPositiveButton("close", null)
                .show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.popupmenu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.action_firstpage){
            Intent intent = new Intent(RecordBoardActivity.this, MainActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש
        }
        if (id==R.id.action_settings){
            Intent intent = new Intent(RecordBoardActivity.this, SettingsActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש
        }
        if (id==R.id.action_recordBoard){
            Toast.makeText(this, "You are already here", Toast.LENGTH_SHORT).show();
        }
        if (id==R.id.action_shop) {
            Intent intent = new Intent(RecordBoardActivity.this, MainShop.class);
            startActivity(intent); // התחלת ה-Activity החדש        }
        }
        if (id==R.id.action_help){
            Intent intent = new Intent(RecordBoardActivity.this, helpActivity.class);
            startActivity(intent); // התחלת ה-Activity החדש
        }
        if (id==R.id.action_start){
            Intent intent = new Intent(RecordBoardActivity.this, MainStart.class);
            startActivity(intent); // התחלת ה-Activity החדש
            Toast.makeText(this, "You pressed RESTART -  Please wait a few seconds", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
