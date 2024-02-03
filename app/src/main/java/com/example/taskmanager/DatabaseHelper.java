package com.example.taskmanager;

import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "myTasks.db"; // название бд
    private static final int SCHEMA = 1; // версия базы данных
    static final String TABLE = "tasks"; // название таблицы в бд
    // названия столбцов
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "task";
    public static final String COLUMN_DEADLINE = "deadline";

    public static final String COLUMN_COMPLETED = "completed";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE tasks (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + " DATETIME, " +
                COLUMN_DEADLINE + " DATETIME, " +
                COLUMN_COMPLETED + " BOOLEAN);");

        // добавление начальных данных
        db.execSQL("INSERT INTO " + TABLE + " (" +
                COLUMN_NAME + ", " +
                COLUMN_DEADLINE + ", " +
                COLUMN_COMPLETED + ") VALUES ('TaskName', '2023-12-27 20:00:00', 0);");

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE);
        onCreate(db);
    }
}