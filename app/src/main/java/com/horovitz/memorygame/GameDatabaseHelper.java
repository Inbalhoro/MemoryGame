package com.horovitz.memorygame;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class GameDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "games.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_GAMES = "games";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_GAME_TYPE = "game_type";
    public static final String COLUMN_SCORE = "score"; // הוספתי את העמודה הזו
    public static final String COLUMN_TIME = "time";

    public GameDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_GAMES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_GAME_TYPE + " TEXT, " +
                COLUMN_SCORE + " INTEGER, " + // הוספתי את העמודה הזאת
                COLUMN_TIME + " INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAMES);
        onCreate(db);
    }

    // פונקציה להוספת משחק חדש
    public void insertGame(String gameType, int score, int time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GAME_TYPE, gameType);
        values.put(COLUMN_SCORE, score);
        values.put(COLUMN_TIME, time);
        db.insert(TABLE_GAMES, null, values);
        db.close();
    }

    // פונקציה להחזיר את כל המשחקים שנשמרו
    public List<GameResult> getAllGames() {
        List<GameResult> results = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_GAMES, null, null, null, null, null, COLUMN_ID + " DESC");

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GAME_TYPE));
                int score = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE)); // הוספתי את השורה הזו
                int time = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TIME));

                results.add(new GameResult(id, type, score, time)); // עדכנתי את הבנאי של GameResult
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return results;
    }
}
