package com.horovitz.memorygame;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
            titles.add("GAME #" + game.getId() + " (" + game.getGameType() + ")");
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
}
