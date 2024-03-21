package com.example.calorieapp.ui.dashboard;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SelectedButtonDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "selected_button_database";
    private static final int DATABASE_VERSION = 1;

    static final String TABLE_SELECTED_BUTTON = "selected_button";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_BUTTON = "button";

    // SQL query to create the selected_button table
    private static final String CREATE_SELECTED_BUTTON_TABLE = "CREATE TABLE " + TABLE_SELECTED_BUTTON + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_BUTTON + " TEXT);";

    public SelectedButtonDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SELECTED_BUTTON_TABLE);
        Log.d("SelectedButtonDBHelper", "Table created: selected_button");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("SelectedButtonDBHelper", "Upgrading database from version " + oldVersion + " to " + newVersion);
        // Handle database upgrades if needed
    }

    public void insertSelectedButton(String button) {
        // Вставляем выбранную дату в таблицу
        SQLiteDatabase db = this.getWritableDatabase();

        // Проверяем, есть ли уже запись в базе данных
        String existingButton = getSelectedButton();
        if (existingButton != null) {
            // Если запись уже есть, обновляем ее новым значением даты
            ContentValues values = new ContentValues();
            values.put(COLUMN_BUTTON, button);
            db.update(TABLE_SELECTED_BUTTON, values, null, null);
        } else {
            // Если записи еще нет, вставляем новую запись
            ContentValues values = new ContentValues();
            values.put(COLUMN_BUTTON, button);
            db.insert(TABLE_SELECTED_BUTTON, null, values);
        }

        db.close();
    }

    @SuppressLint("Range")
    public String getSelectedButton() {
        // Получаем выбранную дату из таблицы
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SELECTED_BUTTON, new String[]{COLUMN_BUTTON}, null,
                null, null, null, null);
        String selectedButton = null;
        if (cursor.moveToFirst()) {
            selectedButton = cursor.getString(cursor.getColumnIndex(COLUMN_BUTTON));
        }
        cursor.close();
        //db.close();
        //db.close();
        return selectedButton;
    }


}
