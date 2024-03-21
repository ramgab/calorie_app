package com.example.calorieapp.ui.dashboard;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ProductDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "product_database";
    private static final int DATABASE_VERSION = 1;

    static final String TABLE_PRODUCTS = "products";
    private static final String COLUMN_ID = "_id";
    static final String COLUMN_NAME = "name";
    public static final String COLUMN_COMPOSITION = "composition";
    public static final String COLUMN_CALORIES = "calories";
    public static final String COLUMN_PROTEIN = "protein";
    public static final String COLUMN_FAT = "fat";
    public static final String COLUMN_CARBOHYDRATE = "carbohydrate";
    public static final String COLUMN_CATEGORY = "category";

    // SQL query to create the products table
    private static final String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_PRODUCTS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT, " +
            COLUMN_COMPOSITION + " TEXT, " +
            COLUMN_CALORIES + " INTEGER, " +
            COLUMN_PROTEIN + " INTEGER, " +
            COLUMN_FAT + " INTEGER, " +
            COLUMN_CARBOHYDRATE + " INTEGER, " +
            COLUMN_CATEGORY + " TEXT);";

    static final String TABLE_PRODUCT_BREAKFAST = "product_breakfast";
    private static final String COLUMN_BREAKFAST_ID = "_id";
    public static final String COLUMN_BREAKFAST_NAME = "name";
    public static final String COLUMN_BREAKFAST_DATE = "date";
    public static final String COLUMN_BREAKFAST_CALORIES = "calories";
    public static final String COLUMN_BREAKFAST_PROTEIN = "protein";
    public static final String COLUMN_BREAKFAST_FAT = "fat";
    public static final String COLUMN_BREAKFAST_CARBOHYDRATE = "carbohydrate";
    public static final String COLUMN_BREAKFAST_GRAMS = "grams";

    // SQL query to create the product_breakfast table
    private static final String CREATE_PRODUCT_BREAKFAST_TABLE = "CREATE TABLE " + TABLE_PRODUCT_BREAKFAST + " (" +
            COLUMN_BREAKFAST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_BREAKFAST_NAME + " TEXT, " +
            COLUMN_BREAKFAST_DATE + " TEXT, " +
            COLUMN_BREAKFAST_CALORIES + " INTEGER, " +
            COLUMN_BREAKFAST_PROTEIN + " INTEGER, " +
            COLUMN_BREAKFAST_FAT + " INTEGER, " +
            COLUMN_BREAKFAST_CARBOHYDRATE + " INTEGER, " +
            COLUMN_BREAKFAST_GRAMS + " INTEGER);";

    public ProductDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Добавим новый конструктор для обновления базы данных
    public ProductDatabaseHelper(Context context, int newVersion) {
        super(context, DATABASE_NAME, null, newVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PRODUCTS_TABLE);
        db.execSQL(CREATE_PRODUCT_BREAKFAST_TABLE);
        Log.d("ProductDatabaseHelper", "Tables created: products, product_breakfast");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("ProductDatabaseHelper", "Upgrading database from version " + oldVersion + " to " + newVersion);
        // Handle database upgrades if needed
    }



}
