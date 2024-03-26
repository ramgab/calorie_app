package com.example.calorieapp.ui.dashboard;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BarcodeDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "barcode_database";
    private static final int DATABASE_VERSION = 1;

    static final String TABLE_BARCODES = "barcodes";
    private static final String COLUMN_ID = "_id";
    static final String COLUMN_PRODUCT_NAME = "product_name";
    static final String COLUMN_BARCODE = "barcode";

    // SQL query to create the barcodes table
    private static final String CREATE_BARCODES_TABLE = "CREATE TABLE " + TABLE_BARCODES + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_PRODUCT_NAME + " TEXT, " +
            COLUMN_BARCODE + " TEXT);";

    public BarcodeDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BARCODES_TABLE);
        Log.d("BarcodeDatabaseHelper", "Table created: barcodes");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("BarcodeDatabaseHelper", "Upgrading database from version " + oldVersion + " to " + newVersion);
        // Handle database upgrades if needed
    }
}
