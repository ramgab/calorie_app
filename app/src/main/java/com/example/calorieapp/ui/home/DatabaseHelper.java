package com.example.calorieapp.ui.home;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "person_values.db";
    private static final int DATABASE_VERSION = 1;

    // Определение таблицы
    public static final String TABLE_NAME = "person_values";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_AGE = "age";
    public static final String COLUMN_HEIGHT = "height";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_ACTIVITY_LEVEL = "activity_level";
    public static final String COLUMN_FAT_PERCENT = "fat_percent";
    public static final String COLUMN_GOALS = "goals";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_CAL_MIN_DEFICIT = "cal_min_deficit";
    public static final String COLUMN_CAL_MAX_DEFICIT = "cal_max_deficit";
    public static final String COLUMN_CAL_NORM = "cal_norm";
    public static final String COLUMN_CAL_MIN_SURPLUS = "cal_min_surplus";
    public static final String COLUMN_CAL_MAX_SURPLUS = "cal_max_surplus";
    public static final String COLUMN_PROTEIN_DEFICIT = "protein_deficit";
    public static final String COLUMN_PROTEIN_NORM = "protein_norm";
    public static final String COLUMN_PROTEIN_SURPLUS = "protein_surplus";
    public static final String COLUMN_FAT_DEFICIT = "fat_deficit";
    public static final String COLUMN_FAT_NORM = "fat_norm";
    public static final String COLUMN_FAT_SURPLUS = "fat_surplus";
    public static final String COLUMN_CARBOHYDRATE_MIN_DEFICIT = "carb_min_deficit";
    public static final String COLUMN_CARBOHYDRATE_MAX_DEFICIT = "carb_max_deficit";
    public static final String COLUMN_CARBOHYDRATE_NORM = "carb_norm";
    public static final String COLUMN_CARBOHYDRATE_MIN_SURPLUS = "carb_min_surplus";
    public static final String COLUMN_CARBOHYDRATE_MAX_SURPLUS = "carb_max_surplus";
    public static final String COLUMN_WATER = "water";
    public static final String COLUMN_FIBER = "fiber";
    public static final String COLUMN_SALT = "salt";
    public static final String COLUMN_CAFFEINE_NORM = "caffeine_norm";
    public static final String COLUMN_CAFFEINE_MAX = "caffeine_max";

    // Создание таблицы
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_DATE + " TEXT," +
                    COLUMN_NAME + " TEXT," +
                    COLUMN_AGE + " INTEGER," +
                    COLUMN_HEIGHT + " REAL," +
                    COLUMN_WEIGHT + " REAL," +
                    COLUMN_GENDER + " TEXT," +
                    COLUMN_ACTIVITY_LEVEL + " REAL," +
                    COLUMN_FAT_PERCENT + " REAL," +
                    COLUMN_GOALS + " TEXT," +
                    COLUMN_CAL_MIN_DEFICIT + " REAL," +
                    COLUMN_CAL_MAX_DEFICIT + " REAL," +
                    COLUMN_CAL_NORM + " REAL," +
                    COLUMN_CAL_MIN_SURPLUS + " REAL," +
                    COLUMN_CAL_MAX_SURPLUS + " REAL," +
                    COLUMN_PROTEIN_DEFICIT + " REAL," +
                    COLUMN_PROTEIN_NORM + " REAL," +
                    COLUMN_PROTEIN_SURPLUS + " REAL," +
                    COLUMN_FAT_DEFICIT + " REAL," +
                    COLUMN_FAT_NORM + " REAL," +
                    COLUMN_FAT_SURPLUS + " REAL," +
                    COLUMN_CARBOHYDRATE_MIN_DEFICIT + " REAL," +
                    COLUMN_CARBOHYDRATE_MAX_DEFICIT + " REAL," +
                    COLUMN_CARBOHYDRATE_NORM + " REAL," +
                    COLUMN_CARBOHYDRATE_MIN_SURPLUS + " REAL," +
                    COLUMN_CARBOHYDRATE_MAX_SURPLUS + " REAL," +
                    COLUMN_WATER + " REAL," +
                    COLUMN_FIBER + " REAL," +
                    COLUMN_SALT + " REAL," +
                    COLUMN_CAFFEINE_NORM + " REAL," +
                    COLUMN_CAFFEINE_MAX + " REAL)";


    static final String TABLE_CALORIES_SUMMARY_DAY = "calories_summary_day";
    public static final String COLUMN_DATE_DAY = "date_day";
    public static final String COLUMN_TOTAL_CALORIES_DAY = "total_calories_day";
    public static final String COLUMN_ID = "_id";

    // SQL query to create the calories_summary_day table
    private static final String CREATE_CALORIES_SUMMARY_DAY_TABLE = "CREATE TABLE " + TABLE_CALORIES_SUMMARY_DAY + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_DATE_DAY + " TEXT, " +
            COLUMN_TOTAL_CALORIES_DAY + " REAL);";

    public void updateCaloriesSummaryDay(String date, double totalCalories) {
        // Вставляем или обновляем данные в таблице calories_summary_day
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE_DAY, date);
        values.put(COLUMN_TOTAL_CALORIES_DAY, totalCalories);
        db.replace(TABLE_CALORIES_SUMMARY_DAY, null, values);
        db.close();
    }

    public double getTotalCaloriesSummaryDay(String date) {
        // Выполняем запрос для получения суммы калорий из таблицы calories_summary_day по выбранной дате
        String query = "SELECT " + COLUMN_TOTAL_CALORIES_DAY + " FROM " + TABLE_CALORIES_SUMMARY_DAY +
                " WHERE " + COLUMN_DATE_DAY + " = ?" +
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


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
        db.execSQL(CREATE_CALORIES_SUMMARY_DAY_TABLE);
    }



    public void insertData(String name, int age, double height, double weight, String gender, String activityLevel, double fatPercent, String goals,
                           double calMinDeficit, double calMaxDeficit, double calNorm, double calMinSurplus, double calMaxSurplus,
                           double proteinDeficit, double proteinNorm, double proteinSurplus, double fatDeficit, double fatNorm, double fatSurplus,
                           double carbMinDeficit, double carbMaxDeficit, double carbNorm, double carbMinSurplus, double carbMaxSurplus,
                           double water, double fiber, double salt, double caffeineNorm, double caffeineMax) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_AGE, age);
        values.put(COLUMN_HEIGHT, height);
        values.put(COLUMN_WEIGHT, weight);
        values.put(COLUMN_GENDER, gender);
        values.put(COLUMN_ACTIVITY_LEVEL, activityLevel);
        values.put(COLUMN_FAT_PERCENT, fatPercent);
        values.put(COLUMN_GOALS, goals);
        values.put(COLUMN_CAL_MIN_DEFICIT, calMinDeficit);
        values.put(COLUMN_CAL_MAX_DEFICIT, calMaxDeficit);
        values.put(COLUMN_CAL_NORM, calNorm);
        values.put(COLUMN_CAL_MIN_SURPLUS, calMinSurplus);
        values.put(COLUMN_CAL_MAX_SURPLUS, calMaxSurplus);
        values.put(COLUMN_PROTEIN_DEFICIT, proteinDeficit);
        values.put(COLUMN_PROTEIN_NORM, proteinNorm);
        values.put(COLUMN_PROTEIN_SURPLUS, proteinSurplus);
        values.put(COLUMN_FAT_DEFICIT, fatDeficit);
        values.put(COLUMN_FAT_NORM, fatNorm);
        values.put(COLUMN_FAT_SURPLUS, fatSurplus);
        values.put(COLUMN_CARBOHYDRATE_MIN_DEFICIT, carbMinDeficit);
        values.put(COLUMN_CARBOHYDRATE_MAX_DEFICIT, carbMaxDeficit);
        values.put(COLUMN_CARBOHYDRATE_NORM, carbNorm);
        values.put(COLUMN_CARBOHYDRATE_MIN_SURPLUS, carbMinSurplus);
        values.put(COLUMN_CARBOHYDRATE_MAX_SURPLUS, carbMaxSurplus);
        values.put(COLUMN_WATER, water);
        values.put(COLUMN_FIBER, fiber);
        values.put(COLUMN_SALT, salt);
        values.put(COLUMN_CAFFEINE_NORM, caffeineNorm);
        values.put(COLUMN_CAFFEINE_MAX, caffeineMax);
        // Добавление текущей даты в формате YYYY-MM-DD HH:MM:SS
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        values.put(COLUMN_DATE, dateFormat.format(date));
        db.insert(TABLE_NAME, null, values);
        db.close();
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);


        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CALORIES_SUMMARY_DAY + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DATE_DAY + " TEXT, " +
                COLUMN_TOTAL_CALORIES_DAY + " REAL);");


    }

}
