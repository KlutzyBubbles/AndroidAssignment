package com.klutzybubbles.assignment1.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KlutzyBubbles on 3/05/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String NAME = "high_scores";

    private static final String TABLE_SCORES = "scores";
    private static final String COLUMN_ID = "id", COLUMN_TIME = "time", COLUMN_SET_ON = "set_on",
            COLUMN_SIZE = "size", COLUMN_DIFFICULTY = "difficulty";

    public DatabaseHelper(Context context, int version) {
        super(context, DatabaseHelper.NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v("DBHelper:onCreate", "call");
        StringBuilder b = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        b.append(DatabaseHelper.TABLE_SCORES).append('(').append(COLUMN_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        b.append(COLUMN_TIME).append(" INTEGER NOT NULL, ");
        b.append(COLUMN_SET_ON).append(" INTEGER NOT NULL, ");
        b.append(COLUMN_SIZE).append(" INTEGER NOT NULL, ");
        b.append(COLUMN_DIFFICULTY).append(" INTEGER NOT NULL)");
        Log.i("DBHelper:onCreate", "DB EXEC: " + b.toString());
        db.execSQL(b.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v("DBHelper:onUpgrade", "call");
        db.execSQL("DROP TABLE " + TABLE_SCORES);
        this.onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v("DBHelper:onDowngrade", "call");
        if (oldVersion > newVersion) {
            Log.v("DBHelper:onDowngrade", "New version is lower");
            db.execSQL("DROP TABLE " + TABLE_SCORES);
        }
        this.onCreate(db);
    }

    public void insert(long time, int size, int difficulty) {
        Log.v("DBHelper:insert", "call");
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder b = new StringBuilder("INSERT INTO ");
        b.append(DatabaseHelper.TABLE_SCORES).append(" (").append(COLUMN_TIME).append(',');
        b.append(COLUMN_SIZE).append(',');
        b.append(COLUMN_DIFFICULTY).append(',').append(COLUMN_SET_ON).append(") VALUES (");
        b.append(time).append(',').append(size).append(',');
        b.append(difficulty).append(',').append(System.currentTimeMillis()).append(')');
        Log.i("DBHelper:insert", "DB EXEC: " + b.toString());
        db.execSQL(b.toString());
    }

    @SuppressWarnings("unused")
    public List<RecordItem> getAllFrom(int size, int difficulty) {
        Log.v("DBHelper:getAllFrom", "call");
        Log.d("DBHelper:getAllFrom", "var size = " + size);
        Log.d("DBHelper:getAllFrom", "var difficulty = " + difficulty);
        SQLiteDatabase db = this.getReadableDatabase();
        StringBuilder b = new StringBuilder("SELECT * FROM ");
        b.append(TABLE_SCORES).append(" WHERE ");
        b.append(COLUMN_SIZE).append('=').append(size).append(" AND ");
        b.append(COLUMN_DIFFICULTY).append('=').append(difficulty);
        b.append(" ORDER BY ").append(COLUMN_TIME);
        Log.i("DBHelper:getAllFrom", "DB RAW: " + b.toString());
        Cursor c = db.rawQuery(b.toString(), null);
        List<RecordItem> rows = new ArrayList<>();
        if (c.moveToFirst()) {
            Log.d("DBHelper:getAllFrom", "Moved to first");
            while (!c.isAfterLast()) {
                Log.d("DBHelper:getAllFrom", "Current Position: " + c.getPosition());
                rows.add(new RecordItem(c.getLong(c.getColumnIndex(COLUMN_TIME)), c.getLong(c.getColumnIndex(COLUMN_SET_ON))));
                c.moveToNext();
            }
        }
        c.close();
        return rows;
    }


}
