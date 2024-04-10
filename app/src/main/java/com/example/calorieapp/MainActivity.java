package com.example.calorieapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.calorieapp.ui.dashboard.ProductDatabaseHelper;
import com.example.calorieapp.ui.home.EditPersonValueFragment;
import com.example.calorieapp.ui.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.calorieapp.databinding.ActivityMainBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private ProductDatabaseHelper databaseHelper;
    private ActivityMainBinding binding;
    private static final String PREF_FIRST_LAUNCH = "first_launch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide(); // скрываем тайтлбар
        databaseHelper = new ProductDatabaseHelper(this); // Инициализация объекта ProductDatabaseHelper
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (isFirstLaunch()) {
            openEditPersonValueFragment();
            insertDataFromCSV();
        } else {
            openHomeFragment();
        }




        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    private boolean isFirstLaunch() {
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        boolean isFirstLaunch = prefs.getBoolean(PREF_FIRST_LAUNCH, true);
        if (isFirstLaunch) {
            prefs.edit().putBoolean(PREF_FIRST_LAUNCH, false).apply();
        }
        return isFirstLaunch;
    }

    private void openEditPersonValueFragment() {
        EditPersonValueFragment editFragment = new EditPersonValueFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, editFragment)
                .commit();
    }

    private void insertDataFromCSV() {
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.dataset); // Replace 'data' with your file name in res/raw/
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            int lineNumber = 0; // Добавляем счетчик номера строки для отладки
            String line;

            // Open the database for writing
            SQLiteDatabase db = databaseHelper.getWritableDatabase();

            // Read the first line (header) without processing it
            reader.readLine();

            // Read each subsequent line from the CSV file and insert into the database
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(";");
                // Выводим отладочную информацию о текущей строке
                lineNumber++; // Увеличиваем номер строки
                Log.d("InsertData", "Processing line: " + lineNumber);

                // Assuming the CSV columns order is: title, squad, calories, protein, fat, carbohydrate, category, barcode
                String productName = data[0].trim();
                String composition = data[1].trim();
                double calories = tryParseDouble(data[2].trim().replace(",", "."));
                double protein = tryParseDouble(data[3].trim().replace(",", "."));
                double fat = tryParseDouble(data[4].trim().replace(",", "."));
                double carbohydrate = tryParseDouble(data[5].trim().replace(",", "."));
                String category = data[6].trim();
                String barcode = data[7].trim();

                // Insert the data into the products table
                String insertQuery = "INSERT INTO " + ProductDatabaseHelper.TABLE_PRODUCTS + " (" +
                        ProductDatabaseHelper.COLUMN_NAME + ", " +
                        ProductDatabaseHelper.COLUMN_COMPOSITION + ", " +
                        ProductDatabaseHelper.COLUMN_CALORIES + ", " +
                        ProductDatabaseHelper.COLUMN_PROTEIN + ", " +
                        ProductDatabaseHelper.COLUMN_FAT + ", " +
                        ProductDatabaseHelper.COLUMN_CARBOHYDRATE + ", " +
                        ProductDatabaseHelper.COLUMN_CATEGORY + ", " +
                        ProductDatabaseHelper.COLUMN_BARCODE + ") VALUES ('" +
                        productName + "', '" +
                        composition + "', " +
                        calories + ", " +
                        protein + ", " +
                        fat + ", " +
                        carbohydrate + ", '" +
                        category + "', '" +
                        barcode + "');";  // Добавлено значение barcode
                db.execSQL(insertQuery);
            }

            // Close the reader and database
            reader.close();
            db.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double tryParseDouble(String value) {
        try {
            // Заменяем запятую на точку и преобразуем в число с плавающей точкой
            return Double.parseDouble(value.replace(",", "."));
        } catch (NumberFormatException e) {
            // В случае ошибки преобразования возвращаем 0.0 или другое значение по умолчанию
            return 0.0;
        }
    }
    private void openHomeFragment() {
        HomeFragment homeFragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, homeFragment)
                .commit();
    }
}