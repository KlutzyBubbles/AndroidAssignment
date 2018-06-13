package com.klutzybubbles.threeinarow.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * <h1>DatabaseHelper.java</h1>
 * Class used to encapsulate all direct database interactions
 *
 * @author Lee Tzilantonis
 * @version 1.0.1
 * @since 10/6/2018
 * @see SQLiteOpenHelper
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    /**
     * The name of the database to access
     */
    private static final String NAME = "high_scores";

    /**
     * The name of the table(s) to access
     */
    private static final String TABLE_SCORES = "scores";

    /**
     * The name of the column's stored within the table(s)
     */
    private static final String COLUMN_ID = "id", COLUMN_TIME = "time", COLUMN_SET_ON = "set_on",
            COLUMN_SIZE = "size", COLUMN_FAIL = "is_fail";

    /**
     * Instantiates teh DatabaseHelper using the context and version for SQLiteOpenHelper
     *
     * @param context - The context for the helper to be created on
     * @param version - The version to create the helper with
     */
    public DatabaseHelper(Context context, int version) {
        super(context, DatabaseHelper.NAME, null, version);
    }

    /**
     * Listener for when the database is being created
     *
     * @param db - The SQLiteDatabase Object created
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DBHelper:onCreate", "call");
        StringBuilder b = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        b.append(DatabaseHelper.TABLE_SCORES).append('(').append(COLUMN_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        b.append(COLUMN_TIME).append(" INTEGER NOT NULL, ");
        b.append(COLUMN_SET_ON).append(" INTEGER NOT NULL, ");
        b.append(COLUMN_FAIL).append(" INTEGER NOT NULL, ");
        b.append(COLUMN_SIZE).append(" INTEGER NOT NULL)");
        Log.i("DBHelper:onCreate", "DB EXEC: " + b.toString());
        db.execSQL(b.toString());
    }

    /**
     * Listener for when the database is being upgraded
     *
     * @param db - The SQLiteDatabase Object upgraded
     * @param oldVersion - The previous database version
     * @param newVersion - The requested database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DBHelper:onUpgrade", "call");
        db.execSQL("DROP TABLE " + TABLE_SCORES);
        this.onCreate(db);
    }

    /**
     * Listener for when the database is being downgrades
     *
     * @param db - The SQLiteDatabase Object downgraded
     * @param oldVersion - The previous database version
     * @param newVersion - The requested database version
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DBHelper:onDowngrade", "call");
        db.execSQL("DROP TABLE " + TABLE_SCORES);
        this.onCreate(db);
    }

    /**
     * Inserts a record into the database with the setOn set to the current timestamp
     *
     * @param time - The time in milliseconds of the record
     * @param size - The size of the records grid
     */
    public void insert(long time, int size, boolean fail) {
        Log.d("DBHelper:insert", "call");
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder b = new StringBuilder("INSERT INTO ");
        b.append(DatabaseHelper.TABLE_SCORES).append(" (").append(COLUMN_TIME).append(',');
        b.append(COLUMN_SIZE).append(',');
        b.append(COLUMN_FAIL).append(',');
        b.append(COLUMN_SET_ON).append(") VALUES (");
        b.append(time).append(',').append(size).append(',');
        b.append(fail ? 1 : 0).append(',');
        b.append(System.currentTimeMillis()).append(')');
        Log.i("DBHelper:insert", "DB EXEC: " + b.toString());
        db.execSQL(b.toString());
    }

    /**
     * Gets all records matching the criteria
     *
     * @param size - The size criteria
     * @return - List of RecordItem's with all records matching the criteria
     */
    public List<RecordItem> getAllFrom(int size) {
        Log.d("DBHelper:getAllFrom", "call");
        Log.v("DBHelper:getAllFrom", "Size - " + size);
        SQLiteDatabase db = this.getReadableDatabase();
        StringBuilder b = new StringBuilder("SELECT * FROM ");
        b.append(TABLE_SCORES).append(" WHERE ");
        b.append(COLUMN_SIZE).append('=').append(size);
        b.append(" ORDER BY ").append(COLUMN_TIME);
        Log.i("DBHelper:getAllFrom", "DB RAW: " + b.toString());
        Cursor c = db.rawQuery(b.toString(), null);
        List<RecordItem> rows = new ArrayList<>();
        if (c.moveToFirst()) {
            Log.d("DBHelper:getAllFrom", "Moved to first");
            while (!c.isAfterLast()) {
                Log.v("DBHelper:getAllFrom", "Current Position: " + c.getPosition());
                rows.add(new RecordItem(c.getLong(c.getColumnIndex(COLUMN_TIME)),
                        c.getLong(c.getColumnIndex(COLUMN_SET_ON))));
                c.moveToNext();
            }
        }
        c.close();
        return rows;
    }

    /**
     * Gets a list of overall stats matching the criteria
     *
     * @param size - The size criteria
     * @return - List of overall stats using the records matching the criteria
     */
    public List<String> getInfo(int size) {
        Log.d("DBHelper:getAllFrom", "call");
        Log.v("DBHelper:getAllFrom", "Size - " + size);
        SQLiteDatabase db = this.getReadableDatabase();
        StringBuilder b = new StringBuilder("SELECT * FROM ");
        b.append(TABLE_SCORES).append(" WHERE ");
        b.append(COLUMN_SIZE).append('=').append(size);
        Log.i("DBHelper:getAllFrom", "DB RAW: " + b.toString());
        Cursor c = db.rawQuery(b.toString(), null);
        List<String> rows = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;
        long bestTime = -1;
        long timeTotal = 0;
        if (c.moveToFirst()) {
            Log.d("DBHelper:getAllFrom", "Moved to first");
            while (!c.isAfterLast()) {
                Log.v("DBHelper:getAllFrom", "Current Position: " + c.getPosition());
                boolean isFail = c.getInt(c.getColumnIndex(COLUMN_FAIL)) == 1;
                long time = c.getLong(c.getColumnIndex(COLUMN_TIME));
                if (isFail) {
                    failCount++;
                } else {
                    successCount++;
                    timeTotal += time;
                    if (bestTime > time || bestTime == -1)
                        bestTime = time;
                }
                c.moveToNext();
            }
        }
        rows.add("Games Played: " + (successCount + failCount));
        rows.add("Games Won: " + successCount);
        rows.add("Games Lost: " + failCount);
        if ((successCount + failCount) == 0)
            rows.add("Win %: 0%");
        else
            rows.add("Win %: " + round((double) successCount / ((double) successCount + (double) failCount)) + '%');
        rows.add("Best Time: " + String.format(Locale.ENGLISH, "%02d:%02d.%03d", TimeUnit.MILLISECONDS.toMinutes(bestTime),
                TimeUnit.MILLISECONDS.toSeconds(bestTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(bestTime)),
                bestTime % 1000));
        if (successCount == 0)
            rows.add("Average Time: N/A");
        else {
            long average = timeTotal / successCount;
            rows.add("Average Time: " + String.format(Locale.ENGLISH, "%02d:%02d.%03d", TimeUnit.MILLISECONDS.toMinutes(average),
                    TimeUnit.MILLISECONDS.toSeconds(average) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(average)),
                    average % 1000));
        }
        c.close();
        return rows;
    }

    private double round(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
