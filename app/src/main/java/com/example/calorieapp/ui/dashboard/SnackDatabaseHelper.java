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

public class SnackDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "snack_database";
    private static final int DATABASE_VERSION = 2;

    static final String TABLE_SNACK = "snack";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PRODUCT_NAME = "product_name";
    public static final String COLUMN_GRAMS = "grams";
    public static final String COLUMN_CALORIES = "calories";
    public static final String COLUMN_PROTEIN = "protein";
    public static final String COLUMN_FAT = "fat";
    public static final String COLUMN_CARBOHYDRATE = "carbohydrate";
    public static final String COLUMN_DATE = "date";

    // SQL query to create the snack table
    private static final String CREATE_SNACK_TABLE = "CREATE TABLE " + TABLE_SNACK + " (" +
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


    static final String TABLE_PROTEIN_SUMMARY = "protein_summary";
    public static final String COLUMN_DATE_SUMMARY_PROTEIN = "date_summary_protein";
    public static final String COLUMN_TOTAL_PROTEIN = "total_protein";

    // SQL query to create the calories_summary table
    // SQL query to create the calories_summary table

    private static final String CREATE_PROTEIN_SUMMARY_TABLE = "CREATE TABLE " + TABLE_PROTEIN_SUMMARY + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_DATE_SUMMARY_PROTEIN + " TEXT, " +
            COLUMN_TOTAL_PROTEIN + " REAL);";


    static final String TABLE_FAT_SUMMARY = "fat_summary";
    public static final String COLUMN_DATE_SUMMARY_FAT = "date_summary_fat";
    public static final String COLUMN_TOTAL_FAT = "total_fat";

    // SQL query to create the calories_summary table
    // SQL query to create the calories_summary table

    private static final String CREATE_FAT_SUMMARY_TABLE = "CREATE TABLE " + TABLE_FAT_SUMMARY + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_DATE_SUMMARY_FAT + " TEXT, " +
            COLUMN_TOTAL_FAT + " REAL);";

    static final String TABLE_CARBOHYDRATE_SUMMARY = "carbohydrate_summary";
    public static final String COLUMN_DATE_SUMMARY_CARBOHYDRATE = "date_summary_carbohydrate";
    public static final String COLUMN_TOTAL_CARBOHYDRATE = "total_carbohydrate";

    // SQL query to create the calories_summary table
    // SQL query to create the calories_summary table

    private static final String CREATE_CARBOHYDRATE_SUMMARY_TABLE = "CREATE TABLE " + TABLE_CARBOHYDRATE_SUMMARY + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_DATE_SUMMARY_CARBOHYDRATE + " TEXT, " +
            COLUMN_TOTAL_CARBOHYDRATE + " REAL);";


    public SnackDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SNACK_TABLE);
        db.execSQL(CREATE_CALORIES_SUMMARY_TABLE);
        db.execSQL(CREATE_PROTEIN_SUMMARY_TABLE);
        db.execSQL(CREATE_FAT_SUMMARY_TABLE);
        db.execSQL(CREATE_CARBOHYDRATE_SUMMARY_TABLE);

        Log.d("SnackDatabaseHelper", "Tables created: snack, calories_summary");
    }


    public void updateCaloriesSummarySnack(String date) {
        // Выполняем запрос для получения суммы калорий по выбранной дате с округлением до сотых
        String query = "SELECT ROUND(SUM(" + COLUMN_CALORIES + "), 2) FROM " + TABLE_SNACK +
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


    public double getTotalCaloriesSummarySnack(String date) {
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

    public void updateProteinSummarySnack(String date) {
        // Выполняем запрос для получения суммы калорий по выбранной дате с округлением до сотых
        String query = "SELECT ROUND(SUM(" + COLUMN_PROTEIN + "), 2) FROM " + TABLE_SNACK +
                " WHERE " + COLUMN_DATE + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{date});

        double totalProtein = 0;

        // Если есть результат, переходим к первой записи
        if (cursor.moveToFirst()) {
            totalProtein = cursor.getDouble(0);
        }

        cursor.close();

        // Теперь вставляем или обновляем данные в таблице protein_summary
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE_SUMMARY_PROTEIN, date);
        values.put(COLUMN_TOTAL_PROTEIN, totalProtein);

        db = this.getWritableDatabase();
        db.replace(TABLE_PROTEIN_SUMMARY, null, values);
        db.close();
    }


    public double getTotalProteinSummarySnack(String date) {
        // Выполняем запрос для получения суммы калорий из таблицы protein_summary по выбранной дате
        String query = "SELECT " + COLUMN_TOTAL_PROTEIN + " FROM " + TABLE_PROTEIN_SUMMARY +
                " WHERE " + COLUMN_DATE_SUMMARY_PROTEIN + " = ?" +
                " ORDER BY " + COLUMN_ID + " DESC";  // Упорядочиваем по убыванию id

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{date});

        double totalProtein = 0;

        // Если есть результат, переходим к первой записи
        if (cursor.moveToFirst()) {
            totalProtein = cursor.getDouble(0);
        }

        cursor.close();
        db.close();

        return totalProtein;
    }

    public void updateFatSummarySnack(String date) {
        // Выполняем запрос для получения суммы калорий по выбранной дате с округлением до сотых
        String query = "SELECT ROUND(SUM(" + COLUMN_FAT + "), 2) FROM " + TABLE_SNACK +
                " WHERE " + COLUMN_DATE + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{date});

        double totalFat = 0;

        // Если есть результат, переходим к первой записи
        if (cursor.moveToFirst()) {
            totalFat = cursor.getDouble(0);
        }

        cursor.close();

        // Теперь вставляем или обновляем данные в таблице protein_summary
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE_SUMMARY_FAT, date);
        values.put(COLUMN_TOTAL_FAT, totalFat);

        db = this.getWritableDatabase();
        db.replace(TABLE_FAT_SUMMARY, null, values);
        db.close();
    }


    public double getTotalFatSummarySnack(String date) {
        // Выполняем запрос для получения суммы калорий из таблицы protein_summary по выбранной дате
        String query = "SELECT " + COLUMN_TOTAL_FAT + " FROM " + TABLE_FAT_SUMMARY +
                " WHERE " + COLUMN_DATE_SUMMARY_FAT + " = ?" +
                " ORDER BY " + COLUMN_ID + " DESC";  // Упорядочиваем по убыванию id

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{date});

        double totalFat = 0;

        // Если есть результат, переходим к первой записи
        if (cursor.moveToFirst()) {
            totalFat = cursor.getDouble(0);
        }

        cursor.close();
        db.close();

        return totalFat;
    }

    public void updateCarbSummarySnack(String date) {
        // Выполняем запрос для получения суммы калорий по выбранной дате с округлением до сотых
        String query = "SELECT ROUND(SUM(" + COLUMN_CARBOHYDRATE + "), 2) FROM " + TABLE_SNACK +
                " WHERE " + COLUMN_DATE + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{date});

        double totalCarb = 0;

        // Если есть результат, переходим к первой записи
        if (cursor.moveToFirst()) {
            totalCarb = cursor.getDouble(0);
        }

        cursor.close();

        // Теперь вставляем или обновляем данные в таблице protein_summary
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE_SUMMARY_CARBOHYDRATE, date);
        values.put(COLUMN_TOTAL_CARBOHYDRATE, totalCarb);

        db = this.getWritableDatabase();
        db.replace(TABLE_CARBOHYDRATE_SUMMARY, null, values);
        db.close();
    }


    public double getTotalCarbSummarySnack(String date) {
        // Выполняем запрос для получения суммы калорий из таблицы protein_summary по выбранной дате
        String query = "SELECT " + COLUMN_TOTAL_CARBOHYDRATE + " FROM " + TABLE_CARBOHYDRATE_SUMMARY +
                " WHERE " + COLUMN_DATE_SUMMARY_CARBOHYDRATE + " = ?" +
                " ORDER BY " + COLUMN_ID + " DESC";  // Упорядочиваем по убыванию id

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{date});

        double totalCarb = 0;

        // Если есть результат, переходим к первой записи
        if (cursor.moveToFirst()) {
            totalCarb = cursor.getDouble(0);
        }

        cursor.close();
        db.close();

        return totalCarb;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("SnackDatabaseHelper", "Upgrading database from version " + oldVersion + " to " + newVersion);
        // Handle database upgrades if needed

        if (oldVersion < newVersion) {
            if (oldVersion < 2) {
                // Upgrade from version 1 to 2
                db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_PROTEIN_SUMMARY + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_DATE_SUMMARY_PROTEIN + " TEXT, " +
                        COLUMN_TOTAL_PROTEIN + " REAL);");
            }



            if (oldVersion < 2) {
                // Upgrade from version 1 to 2
                db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_FAT_SUMMARY + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_DATE_SUMMARY_FAT + " TEXT, " +
                        COLUMN_TOTAL_FAT + " REAL);");
            }

            if (oldVersion < 2) {
                // Upgrade from version 1 to 2
                db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CARBOHYDRATE_SUMMARY + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_DATE_SUMMARY_CARBOHYDRATE + " TEXT, " +
                        COLUMN_TOTAL_CARBOHYDRATE + " REAL);");
            }

        }
    }

}