package com.example.calorieapp.ui.dashboard;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calorieapp.R;
import com.example.calorieapp.databinding.FragmentDashboardBinding;
import com.example.calorieapp.ui.home.DatabaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.zxing.integration.android.IntentIntegrator;

import org.apache.poi.ss.formula.functions.T;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {
    private RecyclerView homeHorizontalRec;
    private List<HomeHorModel> homeHorModelList;
    private HomeHorAdapter homeHorAdapter;

    private FragmentDashboardBinding binding;
    private TextView currentDateTextView;
    private ImageButton openDatePickerButton;
    private DatePickerDialog datePickerDialog;

    // Добавьте ссылку на базу данных
    private BreakfastDatabaseHelper dbHelper = null;
    private LunchDatabaseHelper dbHelperLunch = null;
    private DinnerDatabaseHelper dbHelperDinner = null;
    private SnackDatabaseHelper dbHelperSnack = null;
    private ProductDatabaseHelper dbHelper2 = null;
    private DatabaseHelper dbHelperPerson = null;

    // Добавьте TextView для отображения суммы калорий
    private TextView sumCalorieBreakfast;
    private TextView sumCalorieLunch;
    private TextView sumCalorieDinner;
    private TextView sumCalorieSnack;
    private TextView calorieSum;
    private TextView calorieRealValue;
    private TextView proteinSum;
    private TextView fatSum;
    private TextView carbohydrateSum;

    private TextView calorieMinValueTextView;
    private TextView calorieMaxValueTextView;
    private TextView waterMax;


    // Объявляем экземпляр класса базы данных для выбранной даты
    private SelectedDateDatabaseHelper selectedDateDBHelper;
    private SelectedButtonDatabaseHelper selectedButtonDBHelper;
    private String breakfast_lunch_or_dinner;

    private View rootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedDateDBHelper = new SelectedDateDatabaseHelper(requireContext());
        selectedButtonDBHelper = new SelectedButtonDatabaseHelper(requireContext());
    }

    @SuppressLint("Range")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        rootView = root;

        // Установка цвета статус-бара
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            // Для API 30 и выше
            requireActivity().getWindow().setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.dark_grey));
        } else {
            // Для API ниже 30
            requireActivity().getWindow().setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.dark_grey));
        }

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        // Удаляем home_fragment из стека обратного вызова
        fragmentManager.popBackStack("home_fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);

        CardView story1 = root.findViewById(R.id.story1);
        CardView story2 = root.findViewById(R.id.story2);
        CardView story3 = root.findViewById(R.id.story3);
        CardView story4 = root.findViewById(R.id.story4);
        CardView story5 = root.findViewById(R.id.story5);
        CardView story6 = root.findViewById(R.id.story6);
        CardView story7 = root.findViewById(R.id.story7);
        CardView story8 = root.findViewById(R.id.story8);

        // Найдите TextView для минимального и максимального значения калорийности
        calorieMinValueTextView = root.findViewById(R.id.CalorieMinValueInDashboard);
        calorieMaxValueTextView = root.findViewById(R.id.CalorieMaxValueInDashboard);
        waterMax = root.findViewById(R.id.WaterValueInDashboard);
        story1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetStory(R.layout.bottom_sheet_story1);
            }
        });

        story2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetStory(R.layout.bottom_sheet_story2);
            }
        });

        story3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetStory(R.layout.bottom_sheet_story3);
            }
        });

        story4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetStory(R.layout.bottom_sheet_story4);
            }
        });

        story5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetStory(R.layout.bottom_sheet_story5);
            }
        });

        story6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetStory(R.layout.bottom_sheet_story6);
            }
        });

        story7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetStory(R.layout.bottom_sheet_story7);
            }
        });

        story8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetStory(R.layout.bottom_sheet_story8);
            }
        });






        // Удаляем свечение при прокрутке
        NestedScrollView nestedScrollView = root.findViewById(R.id.nestedscrollview_dashboard);
        nestedScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        HorizontalScrollView horizontalScrollView = root.findViewById(R.id.stoies);
        horizontalScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        // Найти кнопку добавления завтрака
        CardView buttonAddBreakfast = root.findViewById(R.id.buttonAddBreakfast);
        // Найти кнопку добавления обеда
        CardView buttonAddLunch = root.findViewById(R.id.buttonAddLunch);
        // Найти кнопку добавления ужина
        CardView buttonAddDinner = root.findViewById(R.id.buttonAddDinner);
        // Найти кнопку добавления перекусв
        CardView buttonAddSnack = root.findViewById(R.id.buttonAddSnack);
        // Установить слушатель нажатия
        buttonAddBreakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new instance of the ProductListFragment
                ProductListFragment productListFragment = new ProductListFragment();





                // Получите выбранную дату из вашего текстового поля
                String selectedDate = currentDateTextView.getText().toString();
                // Сохраняем выбранную дату в базу данных
                selectedDateDBHelper.insertSelectedDate(selectedDate);

                breakfast_lunch_or_dinner = "breakfast";
                selectedButtonDBHelper.insertSelectedButton(breakfast_lunch_or_dinner);

                // Get the FragmentManager
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

                // Start a FragmentTransaction
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Replace the current fragment (DashboardFragment) with the new one (ProductListFragment)
                fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, productListFragment);

                // Add the transaction to the back stack, so you can return to the previous fragment
                fragmentTransaction.addToBackStack("dashboard_fragment");

                // Apply the transaction
                fragmentTransaction.commit();
            }
        });

        buttonAddLunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new instance of the ProductListFragment
                ProductListFragment productListFragment = new ProductListFragment();

                // Получите выбранную дату из вашего текстового поля
                String selectedDate = currentDateTextView.getText().toString();
                // Сохраняем выбранную дату в базу данных
                selectedDateDBHelper.insertSelectedDate(selectedDate);

                breakfast_lunch_or_dinner = "lunch";
                selectedButtonDBHelper.insertSelectedButton(breakfast_lunch_or_dinner);

                // Get the FragmentManager
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

                // Start a FragmentTransaction
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Replace the current fragment (DashboardFragment) with the new one (ProductListFragment)
                fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, productListFragment);

                // Add the transaction to the back stack, so you can return to the previous fragment
                fragmentTransaction.addToBackStack("dashboard_fragment");

                // Apply the transaction
                fragmentTransaction.commit();
            }
        });

        buttonAddDinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Create a new instance of the ProductListFragment
                ProductListFragment productListFragment = new ProductListFragment();
                // Получите выбранную дату из вашего текстового поля
                String selectedDate = currentDateTextView.getText().toString();
                // Сохраняем выбранную дату в базу данных
                selectedDateDBHelper.insertSelectedDate(selectedDate);

                breakfast_lunch_or_dinner = "dinner";
                selectedButtonDBHelper.insertSelectedButton(breakfast_lunch_or_dinner);

                // Get the FragmentManager
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

                // Start a FragmentTransaction
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Replace the current fragment (DashboardFragment) with the new one (ProductListFragment)
                fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, productListFragment);

                // Add the transaction to the back stack, so you can return to the previous fragment
                fragmentTransaction.addToBackStack("dashboard_fragment");

                // Apply the transaction
                fragmentTransaction.commit();
            }
        });

        buttonAddSnack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Create a new instance of the ProductListFragment
                ProductListFragment productListFragment = new ProductListFragment();
                // Получите выбранную дату из вашего текстового поля
                String selectedDate = currentDateTextView.getText().toString();
                // Сохраняем выбранную дату в базу данных
                selectedDateDBHelper.insertSelectedDate(selectedDate);

                breakfast_lunch_or_dinner = "snack";
                selectedButtonDBHelper.insertSelectedButton(breakfast_lunch_or_dinner);

                // Get the FragmentManager
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

                // Start a FragmentTransaction
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Replace the current fragment (DashboardFragment) with the new one (ProductListFragment)
                fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, productListFragment);

                // Add the transaction to the back stack, so you can return to the previous fragment
                fragmentTransaction.addToBackStack("dashboard_fragment");

                // Apply the transaction
                fragmentTransaction.commit();
            }
        });

        // Найти CardView с id cardViewBreakfast
        CardView cardViewBreakfast = root.findViewById(R.id.cardViewBreakfast);

        // Установить слушатель нажатия
        cardViewBreakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Создайте новый экземпляр BreakfastDetailsFragment
                BreakfastDetailsFragment breakfastDetailsFragment = new BreakfastDetailsFragment();

                // Получите выбранную дату из вашего текстового поля
                String selectedDate = currentDateTextView.getText().toString();

                // Передайте выбранную дату в ProductListFragment через аргументы
                Bundle bundle = new Bundle();
                bundle.putString("selectedDate", selectedDate);
                breakfastDetailsFragment.setArguments(bundle);


                // Замените текущий фрагмент на BreakfastDetailsFragment
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_main, breakfastDetailsFragment)
                        .addToBackStack("dashboard_fragment")
                        .commit();
            }
        });

        // Найти CardView с id cardViewLunch
        CardView cardViewLunch = root.findViewById(R.id.cardViewLunch);

        // Установить слушатель нажатия
        cardViewLunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Создайте новый экземпляр BreakfastDetailsFragment
                LunchDetailsFragment lunchDetailsFragment = new LunchDetailsFragment();


                // Получите выбранную дату из вашего текстового поля
                String selectedDate = currentDateTextView.getText().toString();

                // Передайте выбранную дату в ProductListFragment через аргументы
                Bundle bundle = new Bundle();
                bundle.putString("selectedDate", selectedDate);
                lunchDetailsFragment.setArguments(bundle);

                // Замените текущий фрагмент на BreakfastDetailsFragment
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_main, lunchDetailsFragment)
                        .addToBackStack("dashboard_fragment")
                        .commit();
            }
        });

        // Найти CardView с id cardViewDinner
        CardView cardViewDinner = root.findViewById(R.id.cardViewDinner);

        // Установить слушатель нажатия
        cardViewDinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Создайте новый экземпляр BreakfastDetailsFragment
                DinnerDetailsFragment dinnerDetailsFragment = new DinnerDetailsFragment();


                // Получите выбранную дату из вашего текстового поля
                String selectedDate = currentDateTextView.getText().toString();

                // Передайте выбранную дату в ProductListFragment через аргументы
                Bundle bundle = new Bundle();
                bundle.putString("selectedDate", selectedDate);
                dinnerDetailsFragment.setArguments(bundle);


                // Замените текущий фрагмент на BreakfastDetailsFragment
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_main, dinnerDetailsFragment)
                        .addToBackStack("dashboard_fragment")
                        .commit();
            }
        });

        // Найти CardView с id cardViewSnack
        CardView cardViewSnack = root.findViewById(R.id.cardViewSnack);

        // Установить слушатель нажатия
        cardViewSnack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Создайте новый экземпляр BreakfastDetailsFragment
                SnackDetailsFragment snackDetailsFragment = new SnackDetailsFragment();


                // Получите выбранную дату из вашего текстового поля
                String selectedDate = currentDateTextView.getText().toString();

                // Передайте выбранную дату в ProductListFragment через аргументы
                Bundle bundle = new Bundle();
                bundle.putString("selectedDate", selectedDate);
                snackDetailsFragment.setArguments(bundle);


                // Замените текущий фрагмент на BreakfastDetailsFragment
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_main, snackDetailsFragment)
                        .addToBackStack("dashboard_fragment")
                        .commit();
            }
        });




        currentDateTextView = root.findViewById(R.id.currentDateTextView);

        // Установка текущей даты в текстовом поле
        setCurrentDate();

        // Инициализация DatePickerDialog
        initDatePicker();


        binding.layoutForDateToday.setOnClickListener(v -> {
            // При нажатии, отобразите DatePicker
            datePickerDialog.show();
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        });

        // Находим TextView для отображения суммы калорий завтрака
        sumCalorieBreakfast = root.findViewById(R.id.sumCalorieBreakfast);


        // Проверяем, не является ли dbHelper null, и инициализируем его при необходимости
        if (dbHelper == null) {
            dbHelper = new BreakfastDatabaseHelper(requireContext());
        }

        // Проверяем, не является ли dbHelperPerson null, и инициализируем его при необходимости
        if (dbHelperPerson == null) {
            dbHelperPerson = new DatabaseHelper(requireContext());
        }

        // Находим TextView для отображения суммы калорий обеда
        sumCalorieLunch = root.findViewById(R.id.sumCalorieLunch);
        // Проверяем, не является ли dbHelper null, и инициализируем его при необходимости
        if (dbHelperLunch == null) {
            dbHelperLunch = new LunchDatabaseHelper(requireContext());
        }

        // Находим TextView для отображения суммы калорий ужина
        sumCalorieDinner = root.findViewById(R.id.sumCalorieDinner);
        // Проверяем, не является ли dbHelper null, и инициализируем его при необходимости
        if (dbHelperDinner == null) {
            dbHelperDinner = new DinnerDatabaseHelper(requireContext());
        }

        // Находим TextView для отображения суммы калорий ужина
        sumCalorieSnack = root.findViewById(R.id.sumCalorieSnack);
        // Проверяем, не является ли dbHelper null, и инициализируем его при необходимости
        if (dbHelperSnack == null) {
            dbHelperSnack = new SnackDatabaseHelper(requireContext());
        }

        calorieSum = root.findViewById(R.id.calorie_sum);
        calorieRealValue = root.findViewById(R.id.CalorieRealValue);
        proteinSum = root.findViewById(R.id.proteinValue);
        fatSum = root.findViewById(R.id.fatValue);
        carbohydrateSum = root.findViewById(R.id.carbValue);

        // Получите выбранную дату из аргументов
        String selectedDate = currentDateTextView.getText().toString();

        // Загрузите данные из базы данных и установите их в TextView
        loadCaloriesSummaryFromDatabase(selectedDate);
        loadCaloriesSummaryLunchFromDatabase(selectedDate);
        loadCaloriesSummaryDinnerFromDatabase(selectedDate);
        loadCaloriesSummarySnackFromDatabase(selectedDate);
        loadTotalCaloriesSummaryFromDatabase(selectedDate);
        loadTotalProteinSummaryFromDatabase(selectedDate);
        loadTotalFatSummaryFromDatabase(selectedDate);
        loadTotalCarbohydrateSummaryFromDatabase(selectedDate);
        updateProgressBars(selectedDate);

        // Добавьте слушатель изменений даты
        currentDateTextView.addTextChangedListener(dateTextWatcher);

        selectedDate = selectedDateDBHelper.getSelectedDate();
        if(selectedDate != null && !selectedDate.isEmpty()) {
            // Если база данных содержит значение, используем его
            currentDateTextView.setText(selectedDate);
            Log.d("DashboardFragment", "Установлена дата из базы данных: " + selectedDate);
        }


        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nav_view);

        // Скройте BottomNavigationView
        bottomNavigationView.setVisibility(View.VISIBLE);

// Получаем данные из базы данных
        SQLiteDatabase db = dbHelperPerson.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME, null, null, null, null, null, null);
        String goals = null;
        if (cursor != null && cursor.moveToLast()) {
            goals = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_GOALS));
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();

        // Устанавливаем значения в TextView'ы в зависимости от целей
        if (goals != null) {
            switch (goals) {
                case "Похудеть":
                    // Заполняем TextView'ы для похудения
                    setCalorieValuesFromDatabase(dbHelperPerson.COLUMN_CAL_MIN_DEFICIT, dbHelperPerson.COLUMN_CAL_MAX_DEFICIT, dbHelperPerson.COLUMN_WATER, dbHelperPerson.COLUMN_PROTEIN_DEFICIT, dbHelperPerson.COLUMN_FAT_DEFICIT, dbHelperPerson.COLUMN_CARBOHYDRATE_MIN_DEFICIT, dbHelperPerson.COLUMN_CARBOHYDRATE_MAX_DEFICIT, root);
                    break;
                case "Набрать массу":
                    // Заполняем TextView'ы для набора массы
                    setCalorieValuesFromDatabase(dbHelperPerson.COLUMN_CAL_MIN_SURPLUS, dbHelperPerson.COLUMN_CAL_MAX_SURPLUS,dbHelperPerson.COLUMN_WATER, dbHelperPerson.COLUMN_PROTEIN_SURPLUS, dbHelperPerson.COLUMN_FAT_SURPLUS, dbHelperPerson.COLUMN_CARBOHYDRATE_MIN_SURPLUS, dbHelperPerson.COLUMN_CARBOHYDRATE_MAX_SURPLUS, root);
                    break;
                case "Удержать вес":
                    // Заполняем TextView'ы для удержания веса
                    setCalorieValuesFromDatabase(dbHelperPerson.COLUMN_CAL_NORM, dbHelperPerson.COLUMN_CAL_NORM, dbHelperPerson.COLUMN_WATER, dbHelperPerson.COLUMN_PROTEIN_NORM, dbHelperPerson.COLUMN_FAT_NORM, dbHelperPerson.COLUMN_CARBOHYDRATE_NORM, dbHelperPerson.COLUMN_CARBOHYDRATE_NORM, root);

                    break;
                default:
                    // Если значение не определено, не делаем ничего
                    break;
            }
        }

        setupProgressBar(root);
        setupWaterProgressBar(root);
        setupProgressBarCarb(root);
        setupProgressBarFat(root);
        setupProgressBarProtein(root);

        return root;
    }

    @SuppressLint("Range")
    private void setupWaterProgressBar(View root) {
        // Получаем значение для максимального значения прогресс-бара из TextView
        TextView waterValueTextView = root.findViewById(R.id.WaterValueInDashboard);
        TextView waterRealValueTextView = root.findViewById(R.id.WaterValueRealInDashboard);
        String waterValueString = waterValueTextView.getText().toString().replaceAll("[^\\d.]", ""); // Удаляем все символы, кроме цифр и точки
        double maxWaterValue = Double.parseDouble(waterValueString);

        // Получаем сумму граммов напитков за выбранную дату
        double totalGramsOfWater = 0;
        String selectedDate = currentDateTextView.getText().toString();
        Cursor cursor = dbHelper.getReadableDatabase().query(
                BreakfastDatabaseHelper.TABLE_BREAKFAST,
                new String[]{BreakfastDatabaseHelper.COLUMN_GRAMS},
                BreakfastDatabaseHelper.COLUMN_DATE + " = ? AND " + BreakfastDatabaseHelper.COLUMN_CATEGORY + " = ?",
                new String[]{selectedDate, "Напитки"},
                null,
                null,
                null
        );
        if (cursor != null) {
            while (cursor.moveToNext()) {
                totalGramsOfWater += cursor.getDouble(cursor.getColumnIndex(BreakfastDatabaseHelper.COLUMN_GRAMS));
            }
            cursor.close();
        }



        //
        Cursor cursorLunch = dbHelperLunch.getReadableDatabase().query(
                LunchDatabaseHelper.TABLE_LUNCH,
                new String[]{LunchDatabaseHelper.COLUMN_GRAMS},
                LunchDatabaseHelper.COLUMN_DATE + " = ? AND " + LunchDatabaseHelper.COLUMN_CATEGORY + " = ?",
                new String[]{selectedDate, "Напитки"},
                null,
                null,
                null
        );
        if (cursorLunch != null) {
            while (cursorLunch.moveToNext()) {
                totalGramsOfWater += cursorLunch.getDouble(cursorLunch.getColumnIndex(LunchDatabaseHelper.COLUMN_GRAMS));
            }
            cursorLunch.close();
        }

        //
        Cursor cursorDinner = dbHelperDinner.getReadableDatabase().query(
                DinnerDatabaseHelper.TABLE_DINNER,
                new String[]{DinnerDatabaseHelper.COLUMN_GRAMS},
                DinnerDatabaseHelper.COLUMN_DATE + " = ? AND " + DinnerDatabaseHelper.COLUMN_CATEGORY + " = ?",
                new String[]{selectedDate, "Напитки"},
                null,
                null,
                null
        );
        if (cursorDinner != null) {
            while (cursorDinner.moveToNext()) {
                totalGramsOfWater += cursorDinner.getDouble(cursorDinner.getColumnIndex(DinnerDatabaseHelper.COLUMN_GRAMS));
            }
            cursorDinner.close();
        }

        //
        Cursor cursorSnack = dbHelperSnack.getReadableDatabase().query(
                SnackDatabaseHelper.TABLE_SNACK,
                new String[]{SnackDatabaseHelper.COLUMN_GRAMS},
                SnackDatabaseHelper.COLUMN_DATE + " = ? AND " + SnackDatabaseHelper.COLUMN_CATEGORY + " = ?",
                new String[]{selectedDate, "Напитки"},
                null,
                null,
                null
        );
        if (cursorSnack != null) {
            while (cursorSnack.moveToNext()) {
                totalGramsOfWater += cursorSnack.getDouble(cursorSnack.getColumnIndex(SnackDatabaseHelper.COLUMN_GRAMS));
            }
            cursorSnack.close();
        }


        // Вычисляем значение для заполнения прогресс-бара
        double progressValue = totalGramsOfWater / 1000.0; // Переводим граммы в литры

        double maxWater = maxWaterValue * 1000;

        // Находим прогресс-бар и устанавливаем его значения
        ProgressBar waterProgressBar = root.findViewById(R.id.progressBarWater);
        waterProgressBar.setMax((int) maxWater);
        waterProgressBar.setProgress((int) totalGramsOfWater);

        waterRealValueTextView.setText(String.valueOf(progressValue) + " л");
    }


    private void setupProgressBar(View root) {
        TextView calorieMinValueTextView = root.findViewById(R.id.CalorieMaxValueInDashboard);
        ProgressBar progressBar = root.findViewById(R.id.progressBar);

        // Извлекаем значение из calorie_sum и устанавливаем его в прогресс бар
        TextView calorieSumTextView = root.findViewById(R.id.calorie_sum);
        String calorieSumText = calorieSumTextView.getText().toString().replace(" ккал", "").replace(",", ".");
        double calorieSumValue = Double.parseDouble(calorieSumText);
        progressBar.setMax((int) Double.parseDouble(calorieMinValueTextView.getText().toString()));
        progressBar.setProgress((int) calorieSumValue);
    }


    private void setupProgressBarCarb(View root) {
        TextView carbMinValueTextView = root.findViewById(R.id.carbMaxValue);
        ProgressBar progressBarCarb = root.findViewById(R.id.progressBarCarb);

        // Извлекаем значение из calorie_sum и устанавливаем его в прогресс бар
        TextView carbSumTextView = root.findViewById(R.id.carbValue);
        String carbSumText = carbSumTextView.getText().toString().replace(",", ".");
        double carbSumValue = Double.parseDouble(carbSumText) * 1000;

        String carbMinText = carbMinValueTextView.getText().toString();
        double carbMinValue = Double.parseDouble(carbMinText) * 1000;
        progressBarCarb.setMax((int) carbMinValue);
        progressBarCarb.setProgress((int) carbSumValue);
    }

    private void setupProgressBarFat(View root) {
        TextView fatMaxValueTextView = root.findViewById(R.id.fatMaxValue);
        ProgressBar progressBarFat = root.findViewById(R.id.progressBarFat);

        // Извлекаем значение из calorie_sum и устанавливаем его в прогресс бар
        TextView fatSumTextView = root.findViewById(R.id.fatValue);
        String fatSumText = fatSumTextView.getText().toString().replace(",", ".");
        double fatSumValue = Double.parseDouble(fatSumText) * 1000;

        String fatMaxText = fatMaxValueTextView.getText().toString();
        double fatMaxValue = Double.parseDouble(fatMaxText) * 1000;
        progressBarFat.setMax((int) fatMaxValue);
        progressBarFat.setProgress((int) fatSumValue);
    }

    private void setupProgressBarProtein(View root) {
        TextView proteinMaxValueTextView = root.findViewById(R.id.proteinMaxValue);
        ProgressBar progressBarProtein = root.findViewById(R.id.progressBarProtein);

        // Извлекаем значение из calorie_sum и устанавливаем его в прогресс бар
        TextView proteinSumTextView = root.findViewById(R.id.proteinValue);
        String proteinSumText = proteinSumTextView.getText().toString().replace(",", ".");
        double proteinSumValue = Double.parseDouble(proteinSumText) * 1000;

        String proteinMaxText = proteinMaxValueTextView.getText().toString();
        double proteinMaxValue = Double.parseDouble(proteinMaxText) * 1000;
        progressBarProtein.setMax((int) proteinMaxValue);
        progressBarProtein.setProgress((int) proteinSumValue);
    }


    @SuppressLint("Range")
    private void setCalorieValuesFromDatabase(String minValueColumn, String maxValueColumn, String maxWaterColumn, String maxProteinColumn, String maxFatColumn, String minCarbColumn, String maxCarbColumn, View root) {
        // Получаем данные из базы данных
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME, null, null, null, null, null, null);
        double minCalorieValue = 0;
        double maxCalorieValue = 0;
        double maxWaterValue = 0;
        double maxProteinValue = 0;
        double maxFatValue = 0;
        double minCarbValue = 0;
        double maxCarbValue = 0;

        if (cursor != null && cursor.moveToLast()) {
            minCalorieValue = cursor.getDouble(cursor.getColumnIndex(minValueColumn));
            maxCalorieValue = cursor.getDouble(cursor.getColumnIndex(maxValueColumn));
            maxWaterValue = cursor.getDouble(cursor.getColumnIndex(maxWaterColumn));
            maxProteinValue = cursor.getDouble(cursor.getColumnIndex(maxProteinColumn));
            maxFatValue = cursor.getDouble(cursor.getColumnIndex(maxFatColumn));
            minCarbValue = cursor.getDouble(cursor.getColumnIndex(minCarbColumn));
            maxCarbValue = cursor.getDouble(cursor.getColumnIndex(maxCarbColumn));



        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();

        // Устанавливаем значения в TextView'ы
        TextView calorieMinValueTextView = root.findViewById(R.id.CalorieMinValueInDashboard);
        TextView calorieMaxValueTextView = root.findViewById(R.id.CalorieMaxValueInDashboard);
        TextView waterMaxValueTextView = root.findViewById(R.id.WaterValueInDashboard);
        TextView proteinMaxValueTextView = root.findViewById(R.id.proteinMaxValue);
        TextView fatMaxValueTextView = root.findViewById(R.id.fatMaxValue);
        TextView carbMinValueTextView = root.findViewById(R.id.carbMinValue);
        TextView carbMaxValueTextView = root.findViewById(R.id.carbMaxValue);
        TextView carbTire = root.findViewById(R.id.carb_tire);
        TextView carbG = root.findViewById(R.id.carb_g);

        TextView tireCal = root.findViewById(R.id.tire_cal);
        TextView kcalText = root.findViewById(R.id.kcal_text);
        calorieMinValueTextView.setText(String.valueOf(minCalorieValue));
        if (minCalorieValue == maxCalorieValue) {
            calorieMaxValueTextView.setText(""); // Пустое значение, если min и max одинаковы
            tireCal.setText(" ккал");
            kcalText.setText("");
        } else {
            calorieMaxValueTextView.setText(String.valueOf(maxCalorieValue));
            tireCal.setText("-");
            kcalText.setText(" ккал");
        }
        waterMaxValueTextView.setText(String.valueOf(maxWaterValue));
        proteinMaxValueTextView.setText(String.valueOf(maxProteinValue));
        fatMaxValueTextView.setText(String.valueOf(maxFatValue));
        carbMinValueTextView.setText(String.valueOf(minCarbValue));

        if (minCarbValue == maxCarbValue) {
            carbMaxValueTextView.setText(""); // Пустое значение, если min и max одинаковы
            carbTire.setText(" г");
            carbG.setText("");
        } else {
            carbMaxValueTextView.setText(String.valueOf(maxCarbValue));
            carbTire.setText("-");
            carbG.setText(" г");
        }

    }


    // Новый метод для обновления прогресс-баров
    private void updateProgressBars(String selectedDate) {
        double proteinValue = dbHelper.getTotalProteinSummaryFinal(selectedDate); // Получаем сумму белка из таблицы protein_summary
        double fatValue = dbHelper.getTotalFatSummaryFinal(selectedDate); // Получаем сумму жиров из таблицы fat_summary
        double carbValue = dbHelper.getTotalCarbSummaryFinal(selectedDate); // Получаем сумму углеводов из таблицы carbohydrate_summary

        // Вычисляем общую сумму
        double total = proteinValue + fatValue + carbValue;

        // Вычисляем проценты для каждой части прогресс-бара
        int proteinPercentage = (int) ((proteinValue / total) * 100);
        int fatPercentage = (int) ((fatValue / total) * 100);
        int carbPercentage = (int) ((carbValue / total) * 100);

        // Устанавливаем ширину каждой части прогресс-бара
        setProgressBarWidth(binding.proteinProgress, proteinPercentage);
        setProgressBarWidth(binding.fatProgress, fatPercentage);
        setProgressBarWidth(binding.carbProgress, carbPercentage);
    }


    // Добавьте слушатель изменений текста для текущей даты
    private TextWatcher dateTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // Получите выбранную дату из вашего текстового поля
            String selectedDate = currentDateTextView.getText().toString();

            // Загрузите данные из базы данных и обновите TextView
            loadCaloriesSummaryFromDatabase(selectedDate);
            loadCaloriesSummaryLunchFromDatabase(selectedDate);
            loadCaloriesSummaryDinnerFromDatabase(selectedDate);
            loadCaloriesSummarySnackFromDatabase(selectedDate);
            loadTotalCaloriesSummaryFromDatabase(selectedDate);
            loadTotalProteinSummaryFromDatabase(selectedDate);
            loadTotalFatSummaryFromDatabase(selectedDate);
            loadTotalCarbohydrateSummaryFromDatabase(selectedDate);
            updateProgressBars(selectedDate);
            setupProgressBar(rootView);
            setupWaterProgressBar(rootView);
            setupProgressBarCarb(rootView);
            setupProgressBarFat(rootView);
            setupProgressBarProtein(rootView);
        }
    };

    private void loadCaloriesSummaryFromDatabase(String selectedDate) {
        // Получите сумму калорий из базы данных для выбранной даты
        double totalCalories = dbHelper.getTotalCaloriesSummary(selectedDate);

        // Установите значение в TextView с округлением до сотых
        sumCalorieBreakfast.setText(String.format(Locale.getDefault(), "%.2f ккал", totalCalories));
    }

    private void loadCaloriesSummaryLunchFromDatabase(String selectedDate) {
        // Получите сумму калорий из базы данных для выбранной даты
        double totalCaloriesLunch = dbHelperLunch.getTotalCaloriesSummaryLunch(selectedDate);

        // Установите значение в TextView с округлением до сотых
        sumCalorieLunch.setText(String.format(Locale.getDefault(), "%.2f ккал", totalCaloriesLunch));
    }

    private void loadCaloriesSummaryDinnerFromDatabase(String selectedDate) {
        // Получите сумму калорий из базы данных для выбранной даты
        double totalCaloriesDinner = dbHelperDinner.getTotalCaloriesSummaryDinner(selectedDate);

        // Установите значение в TextView с округлением до сотых
        sumCalorieDinner.setText(String.format(Locale.getDefault(), "%.2f ккал", totalCaloriesDinner));
    }

    private void loadCaloriesSummarySnackFromDatabase(String selectedDate) {
        // Получите сумму калорий из базы данных для выбранной даты
        double totalCaloriesSnack = dbHelperSnack.getTotalCaloriesSummarySnack(selectedDate);

        // Установите значение в TextView с округлением до сотых
        sumCalorieSnack.setText(String.format(Locale.getDefault(), "%.2f ккал", totalCaloriesSnack));
    }

    private void loadTotalCaloriesSummaryFromDatabase(String selectedDate){
        double totalCaloriesBreakfast = dbHelper.getTotalCaloriesSummary(selectedDate);
        double totalCaloriesLunch = dbHelperLunch.getTotalCaloriesSummaryLunch(selectedDate);
        double totalCaloriesDinner = dbHelperDinner.getTotalCaloriesSummaryDinner(selectedDate);
        double totalCaloriesSnack = dbHelperSnack.getTotalCaloriesSummarySnack(selectedDate);

        double totalCaloriesFinal = totalCaloriesBreakfast + totalCaloriesLunch + totalCaloriesDinner + totalCaloriesSnack;
        // Обновляем общую сумму калорий за день в таблице calories_summary_day
        dbHelperPerson.updateCaloriesSummaryDay(selectedDate, totalCaloriesFinal);
        calorieSum.setText(String.format(Locale.getDefault(), "%.2f ккал", totalCaloriesFinal));
        calorieRealValue.setText(String.format(Locale.getDefault(), "%.2f", totalCaloriesFinal));
    }

    private void loadTotalProteinSummaryFromDatabase(String selectedDate){
        double totalProteinBreakfast = dbHelper.getTotalProteinSummary(selectedDate);
        double totalProteinLunch = dbHelperLunch.getTotalProteinSummaryLunch(selectedDate);
        double totalProteinDinner = dbHelperDinner.getTotalProteinSummaryDinner(selectedDate);
        double totalProteinSnack = dbHelperSnack.getTotalProteinSummarySnack(selectedDate);

        double totalProteinFinal = totalProteinBreakfast + totalProteinLunch + totalProteinDinner + totalProteinSnack;

        dbHelper.updateProteinSummaryFinal(selectedDate, totalProteinFinal);
        proteinSum.setText(String.format(Locale.getDefault(), "%.2f", totalProteinFinal));
    }

    private void loadTotalFatSummaryFromDatabase(String selectedDate){
        double totalFatBreakfast = dbHelper.getTotalFatSummary(selectedDate);
        double totalFatLunch = dbHelperLunch.getTotalFatSummaryLunch(selectedDate);
        double totalFatDinner = dbHelperDinner.getTotalFatSummaryDinner(selectedDate);
        double totalFatSnack = dbHelperSnack.getTotalFatSummarySnack(selectedDate);

        double totalFatFinal = totalFatBreakfast + totalFatLunch + totalFatDinner + totalFatSnack;

        dbHelper.updateFatSummaryFinal(selectedDate, totalFatFinal);
        fatSum.setText(String.format(Locale.getDefault(), "%.2f", totalFatFinal));
    }

    private void loadTotalCarbohydrateSummaryFromDatabase(String selectedDate){
        double totalCarbBreakfast = dbHelper.getTotalCarbSummary(selectedDate);
        double totalCarbLunch = dbHelperLunch.getTotalCarbSummaryLunch(selectedDate);
        double totalCarbDinner = dbHelperDinner.getTotalCarbSummaryDinner(selectedDate);
        double totalCarbSnack = dbHelperSnack.getTotalCarbSummarySnack(selectedDate);

        double totalCarbFinal = totalCarbBreakfast + totalCarbLunch + totalCarbDinner + totalCarbSnack;

        dbHelper.updateCarbSummaryFinal(selectedDate, totalCarbFinal);
        carbohydrateSum.setText(String.format(Locale.getDefault(), "%.2f", totalCarbFinal));
    }

    private void setCurrentDate() {
        // Получение текущей даты
        Date currentDate = Calendar.getInstance().getTime();
        // Форматирование и установка текущей даты в текстовом поле
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(currentDate);
        currentDateTextView.setText(formattedDate);
    }

    private void initDatePicker() {
        // Получение текущей даты
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Инициализация DatePickerDialog
        datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year1, monthOfYear, dayOfMonth) -> {
                    // Обработка выбора новой даты
                    onDateSelected(year1, monthOfYear, dayOfMonth);
                }, year, month, day);

        // Настройка режима отображения DatePicker в виде спиннера
        datePickerDialog.getDatePicker().setSpinnersShown(true);
        datePickerDialog.getDatePicker().setCalendarViewShown(false);

    }

    private void onDateSelected(int year, int month, int day) {
        // Обработка выбора новой даты
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(year, month, day);

        // Форматирование и установка текущей даты в текстовом поле
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(selectedDate.getTime());
        selectedDateDBHelper.insertSelectedDate(formattedDate);
        currentDateTextView.setText(formattedDate);

    }




    private void setProgressBarWidth(View progressBarPart, int percentage) {
        // Получите LayoutParams для текущего параметра прогресс-бара
        ViewGroup.LayoutParams layoutParams = progressBarPart.getLayoutParams();

        // Установите ширину в процентах от общей ширины прогресс-бара
        layoutParams.width = (int) (getScreenWidth() * (percentage / 100.0));

        // Примените изменения
        progressBarPart.setLayoutParams(layoutParams);
    }

    private int getScreenWidth() {
        // Получите ширину экрана устройства
        return getResources().getDisplayMetrics().widthPixels;
    }

    private void showBottomSheetStory(int bottomSheetLayoutId) {
        // Inflate the bottom sheet layout
        View bottomSheetView = getLayoutInflater().inflate(bottomSheetLayoutId, null);

        // Create a BottomSheetDialog and set the layout
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(bottomSheetView);

        // Show the bottom sheet
        bottomSheetDialog.show();

        // Установим высоту нижнего листа на весь экран
        View parentLayout = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        ViewGroup.LayoutParams layoutParams = parentLayout.getLayoutParams();
        layoutParams.height = getResources().getDisplayMetrics().heightPixels;
        parentLayout.setLayoutParams(layoutParams);

        // Установим состояние нижнего листа в STATE_EXPANDED
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(parentLayout);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }






}