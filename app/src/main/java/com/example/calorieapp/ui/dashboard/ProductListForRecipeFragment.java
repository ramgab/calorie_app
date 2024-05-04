package com.example.calorieapp.ui.dashboard;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calorieapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProductListForRecipeFragment extends Fragment {


    private TextView barcodeTextView;
    private TextView textForInfo;
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String DATA_INITIALIZED_KEY = "dataInitialized";

    private boolean saveButtonClicked = false;  // Флаг для отслеживания нажатия кнопки сохранения
    private List<Product> productList;
    private ProductDatabaseHelper databaseHelper;
    private String productName; // Объявляем переменную productName на уровне класса

    private String editTextValue = "";

    public ProductListForRecipeFragment() {
        // Required empty public constructor
    }
    private ProductDatabaseHelper dbHelper;

    // Объявляем экземпляр класса базы данных для выбранной даты
    private SelectedDateDatabaseHelper selectedDateDBHelper;
    private SelectedButtonDatabaseHelper selectedButtonDBHelper;
    private BottomNavigationView bottomNavigationView; // Добавляем BottomNavigationView

    // В методе onCreate() инициализируем экземпляр
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedDateDBHelper = new SelectedDateDatabaseHelper(requireContext());
        selectedButtonDBHelper = new SelectedButtonDatabaseHelper(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_product_list_for_recipe, container, false);

        textForInfo = root.findViewById(R.id.text_for_info_recipe);
        RecyclerView recyclerView = root.findViewById(R.id.productListRecyclerView_recipe);
        ConstraintLayout layoutProductBlock2 = recyclerView.getRootView().findViewById(R.id.layout_product_block_2_recipe);
        layoutProductBlock2.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black));

        bottomNavigationView = requireActivity().findViewById(R.id.nav_view);
        // Скрываем BottomNavigationView
        bottomNavigationView.setVisibility(View.GONE);

        // Установка цвета статус-бара
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            // Для API 30 и выше
            requireActivity().getWindow().setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.dark_grey));
        } else {
            // Для API ниже 30
            requireActivity().getWindow().setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.dark_grey));
            // Убедитесь, что ваш стиль активности не устанавливает прозрачный статус-бар (android:windowTranslucentStatus)
        }

        // Удаляем свечение при прокрутке
        RecyclerView recyclerViewForProductList = root.findViewById(R.id.productListRecyclerView_recipe);
        recyclerViewForProductList.setOverScrollMode(View.OVER_SCROLL_NEVER);


        // Получаем выбранную дату из базы данных// Initialize the database helper
        databaseHelper = new ProductDatabaseHelper(requireContext());



        // Retrieve product names from the database
        List<String> productNames = new ArrayList<>(); // Создаем новый список для хранения найденных продуктов


        ProductAdapter productAdapter = new ProductAdapter(productNames, new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String productName) {
                // Handle item click by showing the bottom sheet

                showBottomSheetRecipe(productName);

            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(productAdapter);

        // Add a button to close the fragment and go back to DashboardFragment
        ImageView closeButton = root.findViewById(R.id.buttonCloseFragment_recipe);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateRecipeFragment createRecipeFragment = new CreateRecipeFragment();

                // Получаем FragmentManager и начинаем транзакцию
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_main, createRecipeFragment) // Заменяем текущий фрагмент на ProductListFragment
                        .addToBackStack(null)
                        .commit(); // Применяем транзакцию
            }
        });


        // Инициализация SearchView
        androidx.appcompat.widget.SearchView searchView = root.findViewById(R.id.searchView_recipe);

        searchView.setIconifiedByDefault(false);

        ImageView searchIcon=
                searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);

        // To change color of close button, use:
        ImageView searchCloseIcon = (ImageView)searchView
                .findViewById(androidx.appcompat.R.id.search_close_btn);

        searchIcon.setColorFilter(getResources().getColor(R.color.grey),
                android.graphics.PorterDuff.Mode.SRC_IN);

        searchCloseIcon.setColorFilter(getResources().getColor(R.color.grey),
                android.graphics.PorterDuff.Mode.SRC_IN);

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Вызываем метод для фильтрации продуктов при нажатии на кнопку поиска
                filterProducts(query, recyclerView);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Возвращаем false, чтобы не фильтровать продукты при каждом изменении текста
                return false;
            }
        });




        return root;
    }


    // Добавлен метод для фильтрации продуктов
    private void filterProducts(String query, RecyclerView recyclerView) {
        // Получаем отфильтрованный список продуктов по запросу
        List<String> filteredProductNames = getFilteredProductNames(query);

        // Ограничиваем количество отображаемых продуктов до 50
        if (filteredProductNames.size() > 50) {
            filteredProductNames = filteredProductNames.subList(0, 50);
        }

        // Обновляем данные в адаптере
        ((ProductAdapter) recyclerView.getAdapter()).filterList(filteredProductNames);

        // Получаем ConstraintLayout для изменения его цвета
        ConstraintLayout layoutProductBlock2 = recyclerView.getRootView().findViewById(R.id.layout_product_block_2_recipe);

        // Проверяем, если размер отфильтрованного списка равен 0, меняем цвет ConstraintLayout
        if (filteredProductNames.size() == 0) {
            layoutProductBlock2.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black));
            textForInfo.setText("Продукт не найден");
        } else {
            // Возвращаем исходный цвет, если список не пуст
            layoutProductBlock2.setBackgroundResource(R.drawable.shape_3);
            textForInfo.setText("");
        }
    }
    // Добавим метод для обработки события сворачивания клавиатуры





    private void showBottomSheetRecipe(String productName) {
        // Inflate the bottom sheet layout
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout_recipe, null);

        // Find views in the bottom sheet layout
        TextView titleTextView = bottomSheetView.findViewById(R.id.bottomSheetTitleRecipe);
        TextView compositionTextView = bottomSheetView.findViewById(R.id.bottomSheetCompositionRecipe);
        TextView caloriesTextView = bottomSheetView.findViewById(R.id.bottomSheetCaloriesRecipe);
        TextView proteinTextView = bottomSheetView.findViewById(R.id.bottomSheetProteinRecipe);
        TextView fatTextView = bottomSheetView.findViewById(R.id.bottomSheetFatRecipe);
        TextView carbohydrateTextView = bottomSheetView.findViewById(R.id.bottomSheetCarbohydrateRecipe);
        TextView categoryTextView = bottomSheetView.findViewById(R.id.bottomSheetCategoryRecipe);
        barcodeTextView = bottomSheetView.findViewById(R.id.bottomSheetBarcodeRecipe);


        // Добавление поля ввода для грамм продукта
        EditText editTextGrams = bottomSheetView.findViewById(R.id.editTextGramsRecipe);

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
        compositionTextView.setText(productDetails.getComposition());
        caloriesTextView.setText(String.valueOf(productDetails.getCalories()) + "ккал");
        proteinTextView.setText(String.valueOf(productDetails.getProteins()) + "г");
        fatTextView.setText(String.valueOf(productDetails.getFats()) + "г");
        carbohydrateTextView.setText(String.valueOf(productDetails.getCarbohydrates()) + "г");
        categoryTextView.setText(productDetails.getCategory());
        barcodeTextView.setText(productDetails.getBarcode());

        // Найти кнопку добавления штрихкода в макете нижнего листа
        ImageView addBarcodeButton = bottomSheetView.findViewById(R.id.add_barcode);

        // Установить слушатель onClickListener для кнопки добавления штрихкода
        addBarcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Вызов метода для сканирования штрихкода
                Log.d("BarcodeScanner", "Starting barcode scan...");
                IntentIntegrator integrator = new IntentIntegrator(getActivity());
                integrator.setOrientationLocked(true);
                integrator.setPrompt("Пожалуйста, отсканируйте штрихкод");
                integrator.initiateScan();
            }
        });

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
        Button saveButton = bottomSheetView.findViewById(R.id.saveProductRecipe);

        // Create a BottomSheetDialog and set the layout
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(bottomSheetView);


        // Find the close button in the bottom sheet layout
        ImageView closeButton = bottomSheetView.findViewById(R.id.buttonCloseBottomSheetRecipe);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(intentResult != null){
            String contents = intentResult.getContents();
            if(contents != null){
                barcodeTextView.setText(intentResult.getContents());
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }



    }

    private void saveProductBreakfast(Product product, String gramsInput) {
        double grams = tryParseDouble(gramsInput);
        double calories = product.getCalories() * (grams / 100.0);
        double protein = product.getProteins() * (grams / 100.0);
        double fat = product.getFats() * (grams / 100.0);
        double carbohydrate = product.getCarbohydrates() * (grams / 100.0);
        String category = "Ваши рецепты";

        // Откройте базу данных для записи
        SQLiteDatabase db = new RecipeDatabaseHelper(requireContext()).getWritableDatabase();

        // Вставьте данные в таблицу завтрака
        ContentValues values = new ContentValues();
        values.put(RecipeDatabaseHelper.COLUMN_PRODUCT_NAME, product.getName());
        values.put(RecipeDatabaseHelper.COLUMN_GRAMS, grams);
        values.put(RecipeDatabaseHelper.COLUMN_CALORIES, calories);
        values.put(RecipeDatabaseHelper.COLUMN_PROTEIN, protein);
        values.put(RecipeDatabaseHelper.COLUMN_FAT, fat);
        values.put(RecipeDatabaseHelper.COLUMN_CARBOHYDRATE, carbohydrate);
        values.put(RecipeDatabaseHelper.COLUMN_CATEGORY, category);
        db.insert(RecipeDatabaseHelper.TABLE_RECIPE, null, values);

        // Закройте базу данных
        db.close();

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
        caloriesTextView.setText(formattedCalories + "ккал");
        proteinTextView.setText(formattedProtein + "г");
        fatTextView.setText(formattedFat + "г");
        carbohydrateTextView.setText(formattedCarbohydrate + "г");
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
                        ProductDatabaseHelper.COLUMN_CATEGORY,
                        ProductDatabaseHelper.COLUMN_BARCODE
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
            @SuppressLint("Range") String barcode = cursor.getString(cursor.getColumnIndex(ProductDatabaseHelper.COLUMN_BARCODE));

            productDetails = new Product(name, calories, protein, fat, carbohydrate, composition, category, barcode);
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

    private List<String> getFilteredProductNames(String query) {
        List<String> filteredProductNames = new ArrayList<>();

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        // Разделим запрос пользователя на отдельные слова
        String[] searchWords = query.split("\\s+");

        // Переменная для хранения параметров запроса
        StringBuilder selection = new StringBuilder();
        String[] selectionArgs = new String[searchWords.length];

        // Формируем параметры запроса для каждого слова
        for (int i = 0; i < searchWords.length; i++) {
            selection.append(ProductDatabaseHelper.COLUMN_NAME).append(" LIKE ?");
            selectionArgs[i] = "%" + searchWords[i] + "%";
            if (i < searchWords.length - 1) {
                selection.append(" AND ");
            }
        }

        // Выполняем запрос к базе данных
        Cursor cursor = db.query(ProductDatabaseHelper.TABLE_PRODUCTS,
                new String[]{ProductDatabaseHelper.COLUMN_NAME},
                selection.toString(),
                selectionArgs,
                null, null, null);

        // Перебираем результаты запроса и добавляем найденные продукты в список
        while (cursor.moveToNext()) {
            @SuppressLint("Range") String productName = cursor.getString(cursor.getColumnIndex(ProductDatabaseHelper.COLUMN_NAME));
            filteredProductNames.add(productName);
        }

        cursor.close();
        db.close();

        return filteredProductNames;
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
