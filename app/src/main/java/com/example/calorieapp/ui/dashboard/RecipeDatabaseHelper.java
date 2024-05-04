package com.example.calorieapp.ui.dashboard;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.Pair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RecipeDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "recipe_database";
    private static final int DATABASE_VERSION = 2;

    static final String TABLE_RECIPE = "recipe";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PRODUCT_NAME = "product_name";
    public static final String COLUMN_GRAMS = "grams";
    public static final String COLUMN_CALORIES = "calories";
    public static final String COLUMN_PROTEIN = "protein";
    public static final String COLUMN_FAT = "fat";
    public static final String COLUMN_CARBOHYDRATE = "carbohydrate";
    public static final String COLUMN_CATEGORY = "category";

    // SQL query to create the recipe table
    private static final String CREATE_RECIPE_TABLE = "CREATE TABLE " + TABLE_RECIPE + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_PRODUCT_NAME + " TEXT, " +
            COLUMN_GRAMS + " REAL, " +
            COLUMN_CALORIES + " REAL, " +
            COLUMN_PROTEIN + " REAL, " +
            COLUMN_FAT + " REAL, " +
            COLUMN_CARBOHYDRATE + " REAL, " +
            COLUMN_CATEGORY + " TEXT);";



    public RecipeDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_RECIPE_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}