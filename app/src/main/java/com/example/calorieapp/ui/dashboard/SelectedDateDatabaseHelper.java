package com.example.calorieapp.ui.dashboard;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SelectedDateDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "selected_date_database";
    private static final int DATABASE_VERSION = 1;

    static final String TABLE_SELECTED_DATE = "selected_date";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE = "date";

    // SQL query to create the selected_date table
    private static final String CREATE_SELECTED_DATE_TABLE = "CREATE TABLE " + TABLE_SELECTED_DATE + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_DATE + " TEXT);";

    public SelectedDateDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SELECTED_DATE_TABLE);
        Log.d("SelectedDateDBHelper", "Table created: selected_date");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("SelectedDateDBHelper", "Upgrading database from version " + oldVersion + " to " + newVersion);
        // Handle database upgrades if needed
    }

    public void insertSelectedDate(String date) {
        // Вставляем выбранную дату в таблицу
        SQLiteDatabase db = this.getWritableDatabase();

        // Проверяем, есть ли уже запись в базе данных
        String existingDate = getSelectedDate();
        if (existingDate != null) {
            // Если запись уже есть, обновляем ее новым значением даты
            ContentValues values = new ContentValues();
            values.put(COLUMN_DATE, date);
            db.update(TABLE_SELECTED_DATE, values, null, null);
        } else {
            // Если записи еще нет, вставляем новую запись
            ContentValues values = new ContentValues();
            values.put(COLUMN_DATE, date);
            db.insert(TABLE_SELECTED_DATE, null, values);
        }

        db.close();
    }

    @SuppressLint("Range")
    public String getSelectedDate() {
        // Получаем выбранную дату из таблицы
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SELECTED_DATE, new String[]{COLUMN_DATE}, null,
                null, null, null, null);
        String selectedDate = null;
        if (cursor.moveToFirst()) {
            selectedDate = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
        }
        cursor.close();
        //db.close();
        //db.close();
        return selectedDate;
    }


}
