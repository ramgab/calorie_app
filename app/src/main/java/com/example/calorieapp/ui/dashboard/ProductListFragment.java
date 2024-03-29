package com.example.calorieapp.ui.dashboard;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calorieapp.R;
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

public class ProductListFragment extends Fragment {


    private TextView barcodeTextView;
    private String selectedDate;
    private String breakfast_lunch_or_dinner;

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String DATA_INITIALIZED_KEY = "dataInitialized";

    private boolean saveButtonClicked = false;  // Флаг для отслеживания нажатия кнопки сохранения
    private List<Product> productList;
    private ProductDatabaseHelper databaseHelper;
    private String productName; // Объявляем переменную productName на уровне класса

    private String editTextValue = "";

    public ProductListFragment() {
        // Required empty public constructor
    }
    private ProductDatabaseHelper dbHelper;

    // Объявляем экземпляр класса базы данных для выбранной даты
    private SelectedDateDatabaseHelper selectedDateDBHelper;
    private SelectedButtonDatabaseHelper selectedButtonDBHelper;

    // В методе onCreate() инициализируем экземпляр
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedDateDBHelper = new SelectedDateDatabaseHelper(requireContext());
        selectedButtonDBHelper = new SelectedButtonDatabaseHelper(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_product_list, container, false);

        // Получаем выбранную дату из базы данных
        selectedDate = selectedDateDBHelper.getSelectedDate();
        breakfast_lunch_or_dinner = selectedButtonDBHelper.getSelectedButton();

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
                if (Objects.equals(breakfast_lunch_or_dinner, "breakfast")) {
                    showBottomSheet(productName);
                }

                if (Objects.equals(breakfast_lunch_or_dinner, "lunch")) {
                    showBottomSheetLunch(productName);
                }

                if (Objects.equals(breakfast_lunch_or_dinner, "dinner")) {
                    showBottomSheetDinner(productName);
                }

                if (Objects.equals(breakfast_lunch_or_dinner, "snack")) {
                    showBottomSheetSnack(productName);
                }
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(productAdapter);

        // Add a button to close the fragment and go back to DashboardFragment
        Button closeButton = root.findViewById(R.id.buttonCloseFragment);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DashboardFragment dashboardFragment = new DashboardFragment();
                // Получаем FragmentManager и начинаем транзакцию
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_main, dashboardFragment) // Заменяем текущий фрагмент на ProductListFragment
                        .commit(); // Применяем транзакцию
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
        barcodeTextView = bottomSheetView.findViewById(R.id.bottomSheetBarcode);


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
        compositionTextView.setText("Состав: " + productDetails.getComposition());
        caloriesTextView.setText("Калории: " + String.valueOf(productDetails.getCalories()));
        proteinTextView.setText("Белки: " + String.valueOf(productDetails.getProteins()));
        fatTextView.setText("Жиры: " + String.valueOf(productDetails.getFats()));
        carbohydrateTextView.setText("Углеводы: " + String.valueOf(productDetails.getCarbohydrates()));
        categoryTextView.setText("Категория: " + productDetails.getCategory());
        barcodeTextView.setText("Штрихкод: " + productDetails.getBarcode());

        // Найти кнопку добавления штрихкода в макете нижнего листа
        Button addBarcodeButton = bottomSheetView.findViewById(R.id.add_barcode);

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

    private void showBottomSheetLunch(String productName) {
        // Inflate the bottom sheet layout
        View bottomSheetViewLunch = getLayoutInflater().inflate(R.layout.bottom_sheet_layout_lunch, null);

        // Find views in the bottom sheet layout
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView titleTextViewLunch = bottomSheetViewLunch.findViewById(R.id.bottomSheetTitleLunch);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView compositionTextViewLunch = bottomSheetViewLunch.findViewById(R.id.bottomSheetCompositionLunch);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView caloriesTextViewLunch = bottomSheetViewLunch.findViewById(R.id.bottomSheetCaloriesLunch);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView proteinTextViewLunch = bottomSheetViewLunch.findViewById(R.id.bottomSheetProteinLunch);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView fatTextViewLunch = bottomSheetViewLunch.findViewById(R.id.bottomSheetFatLunch);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView carbohydrateTextViewLunch = bottomSheetViewLunch.findViewById(R.id.bottomSheetCarbohydrateLunch);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView categoryTextViewLunch = bottomSheetViewLunch.findViewById(R.id.bottomSheetCategoryLunch);


        // Добавление поля ввода для грамм продукта
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) EditText editTextGramsLunch = bottomSheetViewLunch.findViewById(R.id.editTextGramsLunch);

        // Добавьте слушателя для обработки событий клавиатуры
        editTextGramsLunch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Закрыть клавиатуру
                    InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editTextGramsLunch.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        // Retrieve product details from the database based on the name
        Product productDetails = getProductDetails(productName);

        // Set values to views
        titleTextViewLunch.setText(productDetails.getName());
        compositionTextViewLunch.setText("Состав: " + productDetails.getComposition());
        caloriesTextViewLunch.setText("Калории: " + String.valueOf(productDetails.getCalories()));
        proteinTextViewLunch.setText("Белки: " + String.valueOf(productDetails.getProteins()));
        fatTextViewLunch.setText("Жиры: " + String.valueOf(productDetails.getFats()));
        carbohydrateTextViewLunch.setText("Углеводы: " + String.valueOf(productDetails.getCarbohydrates()));
        categoryTextViewLunch.setText("Категория: " + productDetails.getCategory());

        // Устанавливаем значение переменной productName
        this.productName = productName;

        // Set up a TextWatcher to listen for changes in the grams input
        editTextGramsLunch.addTextChangedListener(new TextWatcher() {
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
                updateNutritionValues(productDetails, editable.toString(), caloriesTextViewLunch, proteinTextViewLunch, fatTextViewLunch, carbohydrateTextViewLunch);


            }
        });

        // Найти кнопку сохранения в макете нижнего листа
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button saveButtonLunch = bottomSheetViewLunch.findViewById(R.id.saveProductLunch);


        // Create a BottomSheetDialog and set the layout
        BottomSheetDialog bottomSheetDialogLunch = new BottomSheetDialog(requireContext());
        bottomSheetDialogLunch.setContentView(bottomSheetViewLunch);


        // Find the close button in the bottom sheet layout
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button closeButton = bottomSheetViewLunch.findViewById(R.id.buttonCloseBottomSheetLunch);

        // Set an onClickListener for the close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Close the bottom sheet
                bottomSheetDialogLunch.dismiss();
            }
        });


        // Установить слушатель onClickListener для кнопки сохранения
        saveButtonLunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Установить флаг, указывающий, что кнопка сохранения была нажата
                saveButtonClicked = true;

                // Используйте значение editTextValue при сохранении данных
                saveProductLunch(productDetails, editTextValue);

                // Закрыть клавиатуру
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editTextGramsLunch.getWindowToken(), 0);

                // Закрыть bottomSheetDialog
                bottomSheetDialogLunch.dismiss();
            }
        });

        // Show the bottom sheet
        bottomSheetDialogLunch.show();
    }



    private void showBottomSheetDinner(String productName) {
        // Inflate the bottom sheet layout
        View bottomSheetViewDinner = getLayoutInflater().inflate(R.layout.bottom_sheet_layout_dinner, null);

        // Find views in the bottom sheet layout
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView titleTextViewDinner = bottomSheetViewDinner.findViewById(R.id.bottomSheetTitleDinner);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView compositionTextViewDinner = bottomSheetViewDinner.findViewById(R.id.bottomSheetCompositionDinner);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView caloriesTextViewDinner = bottomSheetViewDinner.findViewById(R.id.bottomSheetCaloriesDinner);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView proteinTextViewDinner = bottomSheetViewDinner.findViewById(R.id.bottomSheetProteinDinner);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView fatTextViewDinner = bottomSheetViewDinner.findViewById(R.id.bottomSheetFatDinner);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView carbohydrateTextViewDinner = bottomSheetViewDinner.findViewById(R.id.bottomSheetCarbohydrateDinner);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView categoryTextViewDinner = bottomSheetViewDinner.findViewById(R.id.bottomSheetCategoryDinner);


        // Добавление поля ввода для грамм продукта
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) EditText editTextGramsDinner = bottomSheetViewDinner.findViewById(R.id.editTextGramsDinner);

        // Добавьте слушателя для обработки событий клавиатуры
        editTextGramsDinner.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Закрыть клавиатуру
                    InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editTextGramsDinner.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        // Retrieve product details from the database based on the name
        Product productDetails = getProductDetails(productName);

        // Set values to views
        titleTextViewDinner.setText(productDetails.getName());
        compositionTextViewDinner.setText("Состав: " + productDetails.getComposition());
        caloriesTextViewDinner.setText("Калории: " + String.valueOf(productDetails.getCalories()));
        proteinTextViewDinner.setText("Белки: " + String.valueOf(productDetails.getProteins()));
        fatTextViewDinner.setText("Жиры: " + String.valueOf(productDetails.getFats()));
        carbohydrateTextViewDinner.setText("Углеводы: " + String.valueOf(productDetails.getCarbohydrates()));
        categoryTextViewDinner.setText("Категория: " + productDetails.getCategory());

        // Set up a TextWatcher to listen for changes in the grams input
        editTextGramsDinner.addTextChangedListener(new TextWatcher() {
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
                updateNutritionValues(productDetails, editable.toString(), caloriesTextViewDinner, proteinTextViewDinner, fatTextViewDinner, carbohydrateTextViewDinner);


            }
        });

        // Найти кнопку сохранения в макете нижнего листа
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button saveButtonDinner = bottomSheetViewDinner.findViewById(R.id.saveProductDinner);


        // Create a BottomSheetDialog and set the layout
        BottomSheetDialog bottomSheetDialogDinner = new BottomSheetDialog(requireContext());
        bottomSheetDialogDinner.setContentView(bottomSheetViewDinner);


        // Find the close button in the bottom sheet layout
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button closeButton = bottomSheetViewDinner.findViewById(R.id.buttonCloseBottomSheetDinner);

        // Set an onClickListener for the close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Close the bottom sheet
                bottomSheetDialogDinner.dismiss();
            }
        });


        // Установить слушатель onClickListener для кнопки сохранения
        saveButtonDinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Установить флаг, указывающий, что кнопка сохранения была нажата
                saveButtonClicked = true;

                // Используйте значение editTextValue при сохранении данных
                saveProductDinner(productDetails, editTextValue);

                // Закрыть клавиатуру
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editTextGramsDinner.getWindowToken(), 0);

                // Закрыть bottomSheetDialog
                bottomSheetDialogDinner.dismiss();
            }
        });

        // Show the bottom sheet
        bottomSheetDialogDinner.show();
    }



    private void showBottomSheetSnack(String productName) {
        // Inflate the bottom sheet layout
        View bottomSheetViewSnack = getLayoutInflater().inflate(R.layout.bottom_sheet_layout_snack, null);

        // Find views in the bottom sheet layout
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView titleTextViewSnack = bottomSheetViewSnack.findViewById(R.id.bottomSheetTitleSnack);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView compositionTextViewSnack = bottomSheetViewSnack.findViewById(R.id.bottomSheetCompositionSnack);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView caloriesTextViewSnack = bottomSheetViewSnack.findViewById(R.id.bottomSheetCaloriesSnack);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView proteinTextViewSnack = bottomSheetViewSnack.findViewById(R.id.bottomSheetProteinSnack);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView fatTextViewSnack = bottomSheetViewSnack.findViewById(R.id.bottomSheetFatSnack);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView carbohydrateTextViewSnack = bottomSheetViewSnack.findViewById(R.id.bottomSheetCarbohydrateSnack);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView categoryTextViewSnack = bottomSheetViewSnack.findViewById(R.id.bottomSheetCategorySnack);


        // Добавление поля ввода для грамм продукта
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) EditText editTextGramsSnack = bottomSheetViewSnack.findViewById(R.id.editTextGramsSnack);

        // Добавьте слушателя для обработки событий клавиатуры
        editTextGramsSnack.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Закрыть клавиатуру
                    InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editTextGramsSnack.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        // Retrieve product details from the database based on the name
        Product productDetails = getProductDetails(productName);

        // Set values to views
        titleTextViewSnack.setText(productDetails.getName());
        compositionTextViewSnack.setText("Состав: " + productDetails.getComposition());
        caloriesTextViewSnack.setText("Калории: " + String.valueOf(productDetails.getCalories()));
        proteinTextViewSnack.setText("Белки: " + String.valueOf(productDetails.getProteins()));
        fatTextViewSnack.setText("Жиры: " + String.valueOf(productDetails.getFats()));
        carbohydrateTextViewSnack.setText("Углеводы: " + String.valueOf(productDetails.getCarbohydrates()));
        categoryTextViewSnack.setText("Категория: " + productDetails.getCategory());

        // Set up a TextWatcher to listen for changes in the grams input
        editTextGramsSnack.addTextChangedListener(new TextWatcher() {
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
                updateNutritionValues(productDetails, editable.toString(), caloriesTextViewSnack, proteinTextViewSnack, fatTextViewSnack, carbohydrateTextViewSnack);


            }
        });

        // Найти кнопку сохранения в макете нижнего листа
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button saveButtonSnack = bottomSheetViewSnack.findViewById(R.id.saveProductSnack);


        // Create a BottomSheetDialog and set the layout
        BottomSheetDialog bottomSheetDialogSnack = new BottomSheetDialog(requireContext());
        bottomSheetDialogSnack.setContentView(bottomSheetViewSnack);


        // Find the close button in the bottom sheet layout
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button closeButton = bottomSheetViewSnack.findViewById(R.id.buttonCloseBottomSheetSnack);

        // Set an onClickListener for the close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Close the bottom sheet
                bottomSheetDialogSnack.dismiss();
            }
        });


        // Установить слушатель onClickListener для кнопки сохранения
        saveButtonSnack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Установить флаг, указывающий, что кнопка сохранения была нажата
                saveButtonClicked = true;

                // Используйте значение editTextValue при сохранении данных
                saveProductSnack(productDetails, editTextValue);

                // Закрыть клавиатуру
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editTextGramsSnack.getWindowToken(), 0);

                // Закрыть bottomSheetDialog
                bottomSheetDialogSnack.dismiss();
            }
        });

        // Show the bottom sheet
        bottomSheetDialogSnack.show();
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
        new BreakfastDatabaseHelper(requireContext()).updateProteinSummary(selectedDate);
        new BreakfastDatabaseHelper(requireContext()).updateFatSummary(selectedDate);
        new BreakfastDatabaseHelper(requireContext()).updateCarbSummary(selectedDate);
    }

    private void saveProductLunch(Product product, String gramsInput) {
        double grams = tryParseDouble(gramsInput);
        double calories = product.getCalories() * (grams / 100.0);
        double protein = product.getProteins() * (grams / 100.0);
        double fat = product.getFats() * (grams / 100.0);
        double carbohydrate = product.getCarbohydrates() * (grams / 100.0);

        // Откройте базу данных для записи
        SQLiteDatabase db = new LunchDatabaseHelper(requireContext()).getWritableDatabase();

        // Вставьте данные в таблицу завтрака
        ContentValues values = new ContentValues();
        values.put(LunchDatabaseHelper.COLUMN_PRODUCT_NAME, product.getName());
        values.put(LunchDatabaseHelper.COLUMN_GRAMS, grams);
        values.put(LunchDatabaseHelper.COLUMN_CALORIES, calories);
        values.put(LunchDatabaseHelper.COLUMN_PROTEIN, protein);
        values.put(LunchDatabaseHelper.COLUMN_FAT, fat);
        values.put(LunchDatabaseHelper.COLUMN_CARBOHYDRATE, carbohydrate);
        values.put(LunchDatabaseHelper.COLUMN_DATE, selectedDate);

        db.insert(LunchDatabaseHelper.TABLE_LUNCH, null, values);

        // Закройте базу данных
        db.close();

        // Обновите сумму калорий в таблице calories_summary
        new LunchDatabaseHelper(requireContext()).updateCaloriesSummaryLunch(selectedDate);
        new LunchDatabaseHelper(requireContext()).updateProteinSummaryLunch(selectedDate);
        new LunchDatabaseHelper(requireContext()).updateFatSummaryLunch(selectedDate);
        new LunchDatabaseHelper(requireContext()).updateCarbSummaryLunch(selectedDate);
    }


    private void saveProductDinner(Product product, String gramsInput) {
        double grams = tryParseDouble(gramsInput);
        double calories = product.getCalories() * (grams / 100.0);
        double protein = product.getProteins() * (grams / 100.0);
        double fat = product.getFats() * (grams / 100.0);
        double carbohydrate = product.getCarbohydrates() * (grams / 100.0);

        // Откройте базу данных для записи
        SQLiteDatabase db = new DinnerDatabaseHelper(requireContext()).getWritableDatabase();

        // Вставьте данные в таблицу завтрака
        ContentValues values = new ContentValues();
        values.put(DinnerDatabaseHelper.COLUMN_PRODUCT_NAME, product.getName());
        values.put(DinnerDatabaseHelper.COLUMN_GRAMS, grams);
        values.put(DinnerDatabaseHelper.COLUMN_CALORIES, calories);
        values.put(DinnerDatabaseHelper.COLUMN_PROTEIN, protein);
        values.put(DinnerDatabaseHelper.COLUMN_FAT, fat);
        values.put(DinnerDatabaseHelper.COLUMN_CARBOHYDRATE, carbohydrate);
        values.put(DinnerDatabaseHelper.COLUMN_DATE, selectedDate);

        db.insert(DinnerDatabaseHelper.TABLE_DINNER, null, values);

        // Закройте базу данных
        db.close();

        // Обновите сумму калорий в таблице calories_summary
        new DinnerDatabaseHelper(requireContext()).updateCaloriesSummaryDinner(selectedDate);
        new DinnerDatabaseHelper(requireContext()).updateProteinSummaryDinner(selectedDate);
        new DinnerDatabaseHelper(requireContext()).updateFatSummaryDinner(selectedDate);
        new DinnerDatabaseHelper(requireContext()).updateCarbSummaryDinner(selectedDate);
    }

    private void saveProductSnack(Product product, String gramsInput) {
        double grams = tryParseDouble(gramsInput);
        double calories = product.getCalories() * (grams / 100.0);
        double protein = product.getProteins() * (grams / 100.0);
        double fat = product.getFats() * (grams / 100.0);
        double carbohydrate = product.getCarbohydrates() * (grams / 100.0);

        // Откройте базу данных для записи
        SQLiteDatabase db = new SnackDatabaseHelper(requireContext()).getWritableDatabase();

        // Вставьте данные в таблицу завтрака
        ContentValues values = new ContentValues();
        values.put(SnackDatabaseHelper.COLUMN_PRODUCT_NAME, product.getName());
        values.put(SnackDatabaseHelper.COLUMN_GRAMS, grams);
        values.put(SnackDatabaseHelper.COLUMN_CALORIES, calories);
        values.put(SnackDatabaseHelper.COLUMN_PROTEIN, protein);
        values.put(SnackDatabaseHelper.COLUMN_FAT, fat);
        values.put(SnackDatabaseHelper.COLUMN_CARBOHYDRATE, carbohydrate);
        values.put(SnackDatabaseHelper.COLUMN_DATE, selectedDate);

        db.insert(SnackDatabaseHelper.TABLE_SNACK, null, values);

        // Закройте базу данных
        db.close();

        // Обновите сумму калорий в таблице calories_summary
        new SnackDatabaseHelper(requireContext()).updateCaloriesSummarySnack(selectedDate);
        new SnackDatabaseHelper(requireContext()).updateProteinSummarySnack(selectedDate);
        new SnackDatabaseHelper(requireContext()).updateFatSummarySnack(selectedDate);
        new SnackDatabaseHelper(requireContext()).updateCarbSummarySnack(selectedDate);
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
        caloriesTextView.setText("Калории: " + formattedCalories);
        proteinTextView.setText("Белки: " + formattedProtein);
        fatTextView.setText("Жиры: " + formattedFat);
        carbohydrateTextView.setText("Углеводы: " + formattedCarbohydrate);
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

    private void insertDataFromCSV() {
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.data4); // Replace 'data' with your file name in res/raw/
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            // Open the database for writing
            SQLiteDatabase db = databaseHelper.getWritableDatabase();

            // Read the first line (header) without processing it
            reader.readLine();

            // Read each subsequent line from the CSV file and insert into the database
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(";");

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
