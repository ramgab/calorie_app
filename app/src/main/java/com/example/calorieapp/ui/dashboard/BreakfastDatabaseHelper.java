package com.example.calorieapp.ui.dashboard;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BreakfastDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "breakfast_database";
    private static final int DATABASE_VERSION = 1;

    static final String TABLE_BREAKFAST = "breakfast";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PRODUCT_NAME = "product_name";
    public static final String COLUMN_GRAMS = "grams";
    public static final String COLUMN_CALORIES = "calories";
    public static final String COLUMN_PROTEIN = "protein";
    public static final String COLUMN_FAT = "fat";
    public static final String COLUMN_CARBOHYDRATE = "carbohydrate";
    public static final String COLUMN_DATE = "date";

    // SQL query to create the breakfast table
    private static final String CREATE_BREAKFAST_TABLE = "CREATE TABLE " + TABLE_BREAKFAST + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_PRODUCT_NAME + " TEXT, " +
            COLUMN_GRAMS + " REAL, " +
            COLUMN_CALORIES + " REAL, " +
            COLUMN_PROTEIN + " REAL, " +
            COLUMN_FAT + " REAL, " +
            COLUMN_CARBOHYDRATE + " REAL, " +
            COLUMN_DATE + " TEXT);";


    static final String TABLE_CALORIES_SUMMARY = "calories_summary";
    public static final String COLUMN_DATE_SUMMARY = "date_summary";
    public static final String COLUMN_TOTAL_CALORIES = "total_calories";

    // SQL query to create the calories_summary table
    // SQL query to create the calories_summary table
    private static final String CREATE_CALORIES_SUMMARY_TABLE = "CREATE TABLE " + TABLE_CALORIES_SUMMARY + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_DATE_SUMMARY + " TEXT, " +
            COLUMN_TOTAL_CALORIES + " REAL);";


    public BreakfastDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BREAKFAST_TABLE);
        db.execSQL(CREATE_CALORIES_SUMMARY_TABLE);

        Log.d("BreakfastDatabaseHelper", "Tables created: breakfast, calories_summary");
    }


    public void updateCaloriesSummary(String date) {
        // Выполняем запрос для получения суммы калорий по выбранной дате с округлением до сотых
        String query = "SELECT ROUND(SUM(" + COLUMN_CALORIES + "), 2) FROM " + TABLE_BREAKFAST +
                " WHERE " + COLUMN_DATE + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{date});

        double totalCalories = 0;

        // Если есть результат, переходим к первой записи
        if (cursor.moveToFirst()) {
            totalCalories = cursor.getDouble(0);
        }

        cursor.close();

        // Теперь вставляем или обновляем данные в таблице calories_summary
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE_SUMMARY, date);
        values.put(COLUMN_TOTAL_CALORIES, totalCalories);

        db = this.getWritableDatabase();
        db.replace(TABLE_CALORIES_SUMMARY, null, values);
        db.close();
    }


    public double getTotalCaloriesSummary(String date) {
        // Выполняем запрос для получения суммы калорий из таблицы calories_summary по выбранной дате
        String query = "SELECT " + COLUMN_TOTAL_CALORIES + " FROM " + TABLE_CALORIES_SUMMARY +
                " WHERE " + COLUMN_DATE_SUMMARY + " = ?" +
                " ORDER BY " + COLUMN_ID + " DESC";  // Упорядочиваем по убыванию id

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{date});

        double totalCalories = 0;

        // Если есть результат, переходим к первой записи
        if (cursor.moveToFirst()) {
            totalCalories = cursor.getDouble(0);
        }

        cursor.close();
        db.close();

        return totalCalories;
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("BreakfastDatabaseHelper", "Upgrading database from version " + oldVersion + " to " + newVersion);
        // Handle database upgrades if needed
    }

}