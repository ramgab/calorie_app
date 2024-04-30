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
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class ProductListFragment extends Fragment {


    private TextView barcodeTextView;
    private TextView textForInfo;
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
        View root = inflater.inflate(R.layout.fragment_product_list, container, false);

        textForInfo = root.findViewById(R.id.text_for_info);
        RecyclerView recyclerView = root.findViewById(R.id.productListRecyclerView);
        ConstraintLayout layoutProductBlock2 = recyclerView.getRootView().findViewById(R.id.layout_product_block_2);
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
        RecyclerView recyclerViewForProductList = root.findViewById(R.id.productListRecyclerView);
        recyclerViewForProductList.setOverScrollMode(View.OVER_SCROLL_NEVER);


        // Получаем выбранную дату из базы данных
        selectedDate = selectedDateDBHelper.getSelectedDate();
        breakfast_lunch_or_dinner = selectedButtonDBHelper.getSelectedButton();

        // Initialize the database helper
        databaseHelper = new ProductDatabaseHelper(requireContext());



        // Retrieve product names from the database
        List<String> productNames = new ArrayList<>(); // Создаем новый список для хранения найденных продуктов


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
        ImageView closeButton = root.findViewById(R.id.buttonCloseFragment);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DashboardFragment dashboardFragment = new DashboardFragment();

                // Получаем FragmentManager и начинаем транзакцию
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_main, dashboardFragment) // Заменяем текущий фрагмент на ProductListFragment
                        .addToBackStack(null)
                        .commit(); // Применяем транзакцию
            }
        });


        // Инициализация SearchView
        androidx.appcompat.widget.SearchView searchView = root.findViewById(R.id.searchView);

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


        ImageView createProductButton = root.findViewById(R.id.createProduct);
        createProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Создаем новый экземпляр фрагмента CreateProductFragment
                CreateProductFragment createProductFragment = new CreateProductFragment();

                // Получаем FragmentManager и начинаем транзакцию
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_main, createProductFragment) // Заменяем текущий фрагмент на новый
                        .addToBackStack("product_list_fragment") // Добавляем транзакцию в стек возврата
                        .commit(); // Применяем транзакцию
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
        ConstraintLayout layoutProductBlock2 = recyclerView.getRootView().findViewById(R.id.layout_product_block_2);

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
        Button saveButton = bottomSheetView.findViewById(R.id.saveProductBreakfast);

        // Create a BottomSheetDialog and set the layout
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(bottomSheetView);


        // Find the close button in the bottom sheet layout
        ImageView closeButton = bottomSheetView.findViewById(R.id.buttonCloseBottomSheet);

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
        compositionTextViewLunch.setText(productDetails.getComposition());
        caloriesTextViewLunch.setText(String.valueOf(productDetails.getCalories()) + "ккал");
        proteinTextViewLunch.setText(String.valueOf(productDetails.getProteins()) + "г");
        fatTextViewLunch.setText(String.valueOf(productDetails.getFats()) + "г");
        carbohydrateTextViewLunch.setText(String.valueOf(productDetails.getCarbohydrates()) + "г");
        categoryTextViewLunch.setText(productDetails.getCategory());

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
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView closeButton = bottomSheetViewLunch.findViewById(R.id.buttonCloseBottomSheetLunch);

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
        compositionTextViewDinner.setText(productDetails.getComposition());
        caloriesTextViewDinner.setText(String.valueOf(productDetails.getCalories()) + "ккал");
        proteinTextViewDinner.setText(String.valueOf(productDetails.getProteins()) + "г");
        fatTextViewDinner.setText(String.valueOf(productDetails.getFats()) + "г");
        carbohydrateTextViewDinner.setText(String.valueOf(productDetails.getCarbohydrates()) + "г");
        categoryTextViewDinner.setText(productDetails.getCategory());

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
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView closeButton = bottomSheetViewDinner.findViewById(R.id.buttonCloseBottomSheetDinner);

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
        compositionTextViewSnack.setText(productDetails.getComposition());
        caloriesTextViewSnack.setText(String.valueOf(productDetails.getCalories()) + "ккал");
        proteinTextViewSnack.setText(String.valueOf(productDetails.getProteins()) + "г");
        fatTextViewSnack.setText(String.valueOf(productDetails.getFats()) + "г");
        carbohydrateTextViewSnack.setText(String.valueOf(productDetails.getCarbohydrates()) + "г");
        categoryTextViewSnack.setText(productDetails.getCategory());

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
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView closeButton = bottomSheetViewSnack.findViewById(R.id.buttonCloseBottomSheetSnack);

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
        String category = product.getCategory();

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
        values.put(BreakfastDatabaseHelper.COLUMN_CATEGORY, category);
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
        String category = product.getCategory();

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
        values.put(LunchDatabaseHelper.COLUMN_CATEGORY, category);
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
        String category = product.getCategory();

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
        values.put(DinnerDatabaseHelper.COLUMN_CATEGORY, category);
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
        String category = product.getCategory();

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
        values.put(SnackDatabaseHelper.COLUMN_CATEGORY, category);
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
