package com.example.calorieapp.ui.dashboard;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.view.inputmethod.EditorInfo;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calorieapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ProductListFragment extends Fragment {



    private String selectedDate;

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String DATA_INITIALIZED_KEY = "dataInitialized";

    private boolean saveButtonClicked = false;  // Флаг для отслеживания нажатия кнопки сохранения
    private List<Product> productList;
    private ProductDatabaseHelper databaseHelper;

    private String editTextValue = "";

    public ProductListFragment() {
        // Required empty public constructor
    }
    private ProductDatabaseHelper dbHelper;

    // Объявляем экземпляр класса базы данных для выбранной даты
    private SelectedDateDatabaseHelper selectedDateDBHelper;

    // В методе onCreate() инициализируем экземпляр
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedDateDBHelper = new SelectedDateDatabaseHelper(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_product_list, container, false);

        // Получаем выбранную дату из базы данных
        selectedDate = selectedDateDBHelper.getSelectedDate();

        //selectedDate = getArguments().getString("selectedDate");
        /**
        // Получите переданный аргумент из Bundle
        Bundle args = getArguments();
        if (args != null) {

        } else {
            // Обработайте ситуацию, когда аргументы равны null
            selectedDate = "default_value"; // Установите значение по умолчанию или выполните другие действия
        }
        **/



        // Initialize the database helper
        databaseHelper = new ProductDatabaseHelper(requireContext());

        // Check if data has already been initialized
        boolean dataInitialized = loadDataInitializationStatus();
        if (!dataInitialized) {
            // Add products to the database by reading from CSV
            insertDataFromCSV();

            // Mark data as initialized
            saveDataInitializationStatus(true);
        }

        // Retrieve product names from the database
        List<String> productNames = getProductNames();

        // Set up RecyclerView
        RecyclerView recyclerView = root.findViewById(R.id.productListRecyclerView);
        ProductAdapter productAdapter = new ProductAdapter(productNames, new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String productName) {
                // Handle item click by showing the bottom sheet
                showBottomSheet(productName);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(productAdapter);

        // Add a button to close the fragment and go back to DashboardFragment
        Button closeButton = root.findViewById(R.id.buttonCloseFragment);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the FragmentManager
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });


        // Инициализация SearchView
        SearchView searchView = root.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Выполняется при подтверждении поиска (например, по нажатию Enter)
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Выполняется при изменении текста в SearchView
                filterProducts(newText, productNames, recyclerView);
                return true;
            }
        });


        Button createProductButton = root.findViewById(R.id.createProduct);
        createProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Создаем новый экземпляр фрагмента CreateProductFragment
                CreateProductFragment createProductFragment = new CreateProductFragment();

                // Получаем FragmentManager и начинаем транзакцию
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_main, createProductFragment) // Заменяем текущий фрагмент на новый
                        .addToBackStack(null) // Добавляем транзакцию в стек возврата
                        .commit(); // Применяем транзакцию
            }
        });

        return root;
    }


    // Добавлен метод для фильтрации продуктов
    private void filterProducts(String query, List<String> productNames, RecyclerView recyclerView) {
        List<String> filteredProductNames = new ArrayList<>();

        for (String productName : productNames) {
            if (productName.toLowerCase().contains(query.toLowerCase())) {
                filteredProductNames.add(productName);
            }
        }

        // Обновление адаптера RecyclerView
        ((ProductAdapter) recyclerView.getAdapter()).filterList(filteredProductNames);
    }

    private void showBottomSheet(String productName) {
        // Inflate the bottom sheet layout
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);

        // Find views in the bottom sheet layout
        TextView titleTextView = bottomSheetView.findViewById(R.id.bottomSheetTitle);
        TextView compositionTextView = bottomSheetView.findViewById(R.id.bottomSheetComposition);
        TextView caloriesTextView = bottomSheetView.findViewById(R.id.bottomSheetCalories);
        TextView proteinTextView = bottomSheetView.findViewById(R.id.bottomSheetProtein);
        TextView fatTextView = bottomSheetView.findViewById(R.id.bottomSheetFat);
        TextView carbohydrateTextView = bottomSheetView.findViewById(R.id.bottomSheetCarbohydrate);
        TextView categoryTextView = bottomSheetView.findViewById(R.id.bottomSheetCategory);


        // Добавление поля ввода для грамм продукта
        EditText editTextGrams = bottomSheetView.findViewById(R.id.editTextGrams);

        // Добавьте слушателя для обработки событий клавиатуры
        editTextGrams.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Закрыть клавиатуру
                    InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editTextGrams.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        // Retrieve product details from the database based on the name
        Product productDetails = getProductDetails(productName);

        // Set values to views
        titleTextView.setText(productDetails.getName());
        compositionTextView.setText("Composition: " + productDetails.getComposition());
        caloriesTextView.setText("Calories: " + String.valueOf(productDetails.getCalories()));
        proteinTextView.setText("Protein: " + String.valueOf(productDetails.getProteins()));
        fatTextView.setText("Fat: " + String.valueOf(productDetails.getFats()));
        carbohydrateTextView.setText("Carbohydrate: " + String.valueOf(productDetails.getCarbohydrates()));
        categoryTextView.setText("Category: " + productDetails.getCategory());

        // Set up a TextWatcher to listen for changes in the grams input
        editTextGrams.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Сохраните введенный текст
                editTextValue = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // После изменения введенных граммов, обновите значения калорий, белка, жира и углеводов
                updateNutritionValues(productDetails, editable.toString(), caloriesTextView, proteinTextView, fatTextView, carbohydrateTextView);


            }
        });

        // Найти кнопку сохранения в макете нижнего листа
        Button saveButton = bottomSheetView.findViewById(R.id.saveProductBreakfast);

        // Create a BottomSheetDialog and set the layout
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(bottomSheetView);


        // Find the close button in the bottom sheet layout
        Button closeButton = bottomSheetView.findViewById(R.id.buttonCloseBottomSheet);

        // Set an onClickListener for the close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Close the bottom sheet
                bottomSheetDialog.dismiss();
            }
        });


        // Установить слушатель onClickListener для кнопки сохранения
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Установить флаг, указывающий, что кнопка сохранения была нажата
                saveButtonClicked = true;

                // Используйте значение editTextValue при сохранении данных
                saveProductBreakfast(productDetails, editTextValue);

                // Закрыть клавиатуру
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editTextGrams.getWindowToken(), 0);

                // Закрыть bottomSheetDialog
                bottomSheetDialog.dismiss();
            }
        });

        // Show the bottom sheet
        bottomSheetDialog.show();
    }


    private void saveProductBreakfast(Product product, String gramsInput) {
        double grams = tryParseDouble(gramsInput);
        double calories = product.getCalories() * (grams / 100.0);
        double protein = product.getProteins() * (grams / 100.0);
        double fat = product.getFats() * (grams / 100.0);
        double carbohydrate = product.getCarbohydrates() * (grams / 100.0);

        // Откройте базу данных для записи
        SQLiteDatabase db = new BreakfastDatabaseHelper(requireContext()).getWritableDatabase();

        // Вставьте данные в таблицу завтрака
        ContentValues values = new ContentValues();
        values.put(BreakfastDatabaseHelper.COLUMN_PRODUCT_NAME, product.getName());
        values.put(BreakfastDatabaseHelper.COLUMN_GRAMS, grams);
        values.put(BreakfastDatabaseHelper.COLUMN_CALORIES, calories);
        values.put(BreakfastDatabaseHelper.COLUMN_PROTEIN, protein);
        values.put(BreakfastDatabaseHelper.COLUMN_FAT, fat);
        values.put(BreakfastDatabaseHelper.COLUMN_CARBOHYDRATE, carbohydrate);
        values.put(BreakfastDatabaseHelper.COLUMN_DATE, selectedDate);

        db.insert(BreakfastDatabaseHelper.TABLE_BREAKFAST, null, values);

        // Закройте базу данных
        db.close();

        // Обновите сумму калорий в таблице calories_summary
        new BreakfastDatabaseHelper(requireContext()).updateCaloriesSummary(selectedDate);
    }


    private void updateNutritionValues(Product product, String gramsInput, TextView caloriesTextView, TextView proteinTextView, TextView fatTextView, TextView carbohydrateTextView) {
        double grams = tryParseDouble(gramsInput);

        // Выполните пересчет значений калорий, белка, жира и углеводов на основе введенного количества грамм
        double calories = product.getCalories() * (grams / 100.0);
        double protein = product.getProteins() * (grams / 100.0);
        double fat = product.getFats() * (grams / 100.0);
        double carbohydrate = product.getCarbohydrates() * (grams / 100.0);

        // Форматирование значений с двумя цифрами после точки
        String formattedCalories = String.format("%.2f", calories);
        String formattedProtein = String.format("%.2f", protein);
        String formattedFat = String.format("%.2f", fat);
        String formattedCarbohydrate = String.format("%.2f", carbohydrate);

        // Установите новые значения в соответствующие TextView
        caloriesTextView.setText("Calories: " + formattedCalories);
        proteinTextView.setText("Protein: " + formattedProtein);
        fatTextView.setText("Fat: " + formattedFat);
        carbohydrateTextView.setText("Carbohydrate: " + formattedCarbohydrate);
    }

    private Product getProductDetails(String productName) {
        Product productDetails = null;

        // Open the database for reading
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        // Query the database to retrieve product details based on the name
        Cursor cursor = db.query(ProductDatabaseHelper.TABLE_PRODUCTS,
                new String[]{
                        ProductDatabaseHelper.COLUMN_NAME,
                        ProductDatabaseHelper.COLUMN_COMPOSITION,
                        ProductDatabaseHelper.COLUMN_CALORIES,
                        ProductDatabaseHelper.COLUMN_PROTEIN,
                        ProductDatabaseHelper.COLUMN_FAT,
                        ProductDatabaseHelper.COLUMN_CARBOHYDRATE,
                        ProductDatabaseHelper.COLUMN_CATEGORY
                },
                ProductDatabaseHelper.COLUMN_NAME + "=?",
                new String[]{productName},
                null, null, null);

        // Check if cursor contains data
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ProductDatabaseHelper.COLUMN_NAME));
            @SuppressLint("Range") String composition = cursor.getString(cursor.getColumnIndex(ProductDatabaseHelper.COLUMN_COMPOSITION));
            @SuppressLint("Range") double calories = cursor.getDouble(cursor.getColumnIndex(ProductDatabaseHelper.COLUMN_CALORIES));
            @SuppressLint("Range") double protein = cursor.getDouble(cursor.getColumnIndex(ProductDatabaseHelper.COLUMN_PROTEIN));
            @SuppressLint("Range") double fat = cursor.getDouble(cursor.getColumnIndex(ProductDatabaseHelper.COLUMN_FAT));
            @SuppressLint("Range") double carbohydrate = cursor.getDouble(cursor.getColumnIndex(ProductDatabaseHelper.COLUMN_CARBOHYDRATE));
            @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex(ProductDatabaseHelper.COLUMN_CATEGORY));

            productDetails = new Product(name, calories, protein, fat, carbohydrate, composition, category);
        }

        // Close the cursor and database
        cursor.close();
        db.close();

        return productDetails;
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

    private void insertDataFromCSV() {
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.data); // Replace 'data' with your file name in res/raw/
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            // Open the database for writing
            SQLiteDatabase db = databaseHelper.getWritableDatabase();

            // Read the first line (header) without processing it
            reader.readLine();

            // Read each subsequent line from the CSV file and insert into the database
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(";");

                // Assuming the CSV columns order is: title, squad, calories, protein, fat, carbohydrate, category
                String productName = data[0].trim();
                String composition = data[1].trim();
                double calories = tryParseDouble(data[2].trim().replace(",", "."));
                double protein = tryParseDouble(data[3].trim().replace(",", "."));
                double fat = tryParseDouble(data[4].trim().replace(",", "."));
                double carbohydrate = tryParseDouble(data[5].trim().replace(",", "."));
                String category = data[6].trim();

                // Insert the data into the products table
                String insertQuery = "INSERT INTO " + ProductDatabaseHelper.TABLE_PRODUCTS + " (" +
                        ProductDatabaseHelper.COLUMN_NAME + ", " +
                        ProductDatabaseHelper.COLUMN_COMPOSITION + ", " +
                        ProductDatabaseHelper.COLUMN_CALORIES + ", " +
                        ProductDatabaseHelper.COLUMN_PROTEIN + ", " +
                        ProductDatabaseHelper.COLUMN_FAT + ", " +
                        ProductDatabaseHelper.COLUMN_CARBOHYDRATE + ", " +
                        ProductDatabaseHelper.COLUMN_CATEGORY + ") VALUES ('" +
                        productName + "', '" +
                        composition + "', " +
                        calories + ", " +
                        protein + ", " +
                        fat + ", " +
                        carbohydrate + ", '" +
                        category + "');";
                db.execSQL(insertQuery);
            }

            // Close the reader and database
            reader.close();
            db.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> getProductNames() {
        List<String> productList = new ArrayList<>();

        // Open the database for reading
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        // Query the database to retrieve product names
        Cursor cursor = db.query(ProductDatabaseHelper.TABLE_PRODUCTS,
                new String[]{ProductDatabaseHelper.COLUMN_NAME},
                null, null, null, null, null);

        // Iterate through the cursor and add product names to the list
        while (cursor.moveToNext()) {
            @SuppressLint("Range") String productName = cursor.getString(cursor.getColumnIndex(ProductDatabaseHelper.COLUMN_NAME));
            productList.add(productName);
        }

        // Close the cursor and database
        cursor.close();
        db.close();

        return productList;
    }

    private boolean loadDataInitializationStatus() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(DATA_INITIALIZED_KEY, false);
    }

    private void saveDataInitializationStatus(boolean dataInitialized) {
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(DATA_INITIALIZED_KEY, dataInitialized);
        editor.apply();
    }
}
