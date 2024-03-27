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
    public static final String COLUMN_DATE = "date";

    // Создание таблицы
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_NAME + " TEXT," +
                    COLUMN_AGE + " INTEGER," +
                    COLUMN_HEIGHT + " REAL," +
                    COLUMN_WEIGHT + " REAL," +
                    COLUMN_GENDER + " TEXT," +
                    COLUMN_ACTIVITY_LEVEL + " REAL," +
                    COLUMN_DATE + " TEXT)";



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



    public void insertData(String name, int age, float height, float weight, String gender, String activityLevel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_AGE, age);
        values.put(COLUMN_HEIGHT, height);
        values.put(COLUMN_WEIGHT, weight);
        values.put(COLUMN_GENDER, gender);
        values.put(COLUMN_ACTIVITY_LEVEL, activityLevel);
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
