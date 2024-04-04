package com.example.calorieapp.ui.dashboard;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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
    private TextView proteinSum;
    private TextView fatSum;
    private TextView carbohydrateSum;


    // Объявляем экземпляр класса базы данных для выбранной даты
    private SelectedDateDatabaseHelper selectedDateDBHelper;
    private SelectedButtonDatabaseHelper selectedButtonDBHelper;
    private String breakfast_lunch_or_dinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedDateDBHelper = new SelectedDateDatabaseHelper(requireContext());
        selectedButtonDBHelper = new SelectedButtonDatabaseHelper(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();



        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nav_view);

        // Скройте BottomNavigationView
        bottomNavigationView.setVisibility(View.VISIBLE);

        // Удаляем свечение при прокрутке
        NestedScrollView nestedScrollView = root.findViewById(R.id.nestedscrollview_dashboard);
        nestedScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        // Найти кнопку добавления завтрака
        ImageView buttonAddBreakfast = root.findViewById(R.id.buttonAddBreakfast);
        // Найти кнопку добавления обеда
        ImageView buttonAddLunch = root.findViewById(R.id.buttonAddLunch);
        // Найти кнопку добавления ужина
        ImageView buttonAddDinner = root.findViewById(R.id.buttonAddDinner);
        // Найти кнопку добавления перекусв
        ImageView buttonAddSnack = root.findViewById(R.id.buttonAddSnack);
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
                fragmentTransaction.addToBackStack(null);

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
                fragmentTransaction.addToBackStack(null);

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
                fragmentTransaction.addToBackStack(null);

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
                fragmentTransaction.addToBackStack(null);

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
                        .addToBackStack(null)
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
                        .addToBackStack(null)
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
                        .addToBackStack(null)
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
                        .addToBackStack(null)
                        .commit();
            }
        });

        homeHorizontalRec = root.findViewById(R.id.home_hor_rec);



        homeHorModelList = new ArrayList<>();
        homeHorModelList.add(new HomeHorModel(R.drawable.pizza, "Pizza"));
        homeHorModelList.add(new HomeHorModel(R.drawable.hamburger, "Hamburger"));
        homeHorModelList.add(new HomeHorModel(R.drawable.fried_potatoes, "Fries"));
        homeHorModelList.add(new HomeHorModel(R.drawable.ice_cream, "Ice Cream"));
        homeHorModelList.add(new HomeHorModel(R.drawable.sandwich, "Sandwich")); 

        homeHorAdapter = new HomeHorAdapter(getActivity(), homeHorModelList);
        homeHorizontalRec.setAdapter(homeHorAdapter);
        homeHorizontalRec.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        homeHorizontalRec.setHasFixedSize(true);
        homeHorizontalRec.setNestedScrollingEnabled(false);


        homeHorizontalRec.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_UP) {
                    View childView = rv.findChildViewUnder(e.getX(), e.getY());
                    if (childView != null) {
                        int position = rv.getChildAdapterPosition(childView);
                        Intent intent = new Intent(getActivity(), StoryActivity.class);
                        intent.putExtra("position", position); // передаем позицию выбранного элемента сториса
                        startActivity(intent);
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {}

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
        });


        currentDateTextView = root.findViewById(R.id.currentDateTextView);

        // Установка текущей даты в текстовом поле
        setCurrentDate();

        // Инициализация DatePickerDialog
        initDatePicker();


        binding.layoutForDateToday.setOnClickListener(v -> {
            // При нажатии, отобразите DatePicker
            datePickerDialog.show();
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

        return root;
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
        }
    };

    private void loadCaloriesSummaryFromDatabase(String selectedDate) {
        // Получите сумму калорий из базы данных для выбранной даты
        double totalCalories = dbHelper.getTotalCaloriesSummary(selectedDate);

        // Установите значение в TextView с округлением до сотых
        sumCalorieBreakfast.setText(String.format(Locale.getDefault(), "%.2f калорий", totalCalories));
    }

    private void loadCaloriesSummaryLunchFromDatabase(String selectedDate) {
        // Получите сумму калорий из базы данных для выбранной даты
        double totalCaloriesLunch = dbHelperLunch.getTotalCaloriesSummaryLunch(selectedDate);

        // Установите значение в TextView с округлением до сотых
        sumCalorieLunch.setText(String.format(Locale.getDefault(), "%.2f калорий", totalCaloriesLunch));
    }

    private void loadCaloriesSummaryDinnerFromDatabase(String selectedDate) {
        // Получите сумму калорий из базы данных для выбранной даты
        double totalCaloriesDinner = dbHelperDinner.getTotalCaloriesSummaryDinner(selectedDate);

        // Установите значение в TextView с округлением до сотых
        sumCalorieDinner.setText(String.format(Locale.getDefault(), "%.2f калорий", totalCaloriesDinner));
    }

    private void loadCaloriesSummarySnackFromDatabase(String selectedDate) {
        // Получите сумму калорий из базы данных для выбранной даты
        double totalCaloriesSnack = dbHelperSnack.getTotalCaloriesSummarySnack(selectedDate);

        // Установите значение в TextView с округлением до сотых
        sumCalorieSnack.setText(String.format(Locale.getDefault(), "%.2f калорий", totalCaloriesSnack));
    }

    private void loadTotalCaloriesSummaryFromDatabase(String selectedDate){
        double totalCaloriesBreakfast = dbHelper.getTotalCaloriesSummary(selectedDate);
        double totalCaloriesLunch = dbHelperLunch.getTotalCaloriesSummaryLunch(selectedDate);
        double totalCaloriesDinner = dbHelperDinner.getTotalCaloriesSummaryDinner(selectedDate);
        double totalCaloriesSnack = dbHelperSnack.getTotalCaloriesSummarySnack(selectedDate);

        double totalCaloriesFinal = totalCaloriesBreakfast + totalCaloriesLunch + totalCaloriesDinner + totalCaloriesSnack;
        // Обновляем общую сумму калорий за день в таблице calories_summary_day
        dbHelperPerson.updateCaloriesSummaryDay(selectedDate, totalCaloriesFinal);
        calorieSum.setText(String.format(Locale.getDefault(), "%.2f калорий", totalCaloriesFinal));
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
}