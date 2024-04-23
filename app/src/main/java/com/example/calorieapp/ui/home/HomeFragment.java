package com.example.calorieapp.ui.home;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.calorieapp.R;
import com.example.calorieapp.databinding.FragmentHomeBinding;
import com.example.calorieapp.ui.dashboard.BreakfastDatabaseHelper;
import com.example.calorieapp.ui.dashboard.DinnerDatabaseHelper;
import com.example.calorieapp.ui.dashboard.LunchDatabaseHelper;
import com.example.calorieapp.ui.dashboard.SnackDatabaseHelper;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {
    // Внутри класса HomeFragment
    private LineChart weightChart;
    private FragmentHomeBinding binding;
    private TextView personNameTextView, calorieNormTextView, genderValueTextView, ageValueTextView, heightValueTextView, weightValueTextView, activityValueTextView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

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
        NestedScrollView nestedScrollView = root.findViewById(R.id.nestedscrollview_home);
        nestedScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        personNameTextView = root.findViewById(R.id.person_name);
        calorieNormTextView = root.findViewById(R.id.calorie_norm);
        genderValueTextView = root.findViewById(R.id.genderValue);
        ageValueTextView = root.findViewById(R.id.ageValue);
        heightValueTextView = root.findViewById(R.id.heightValue);
        weightValueTextView = root.findViewById(R.id.weightValue);
        activityValueTextView = root.findViewById(R.id.activityValue);

        loadPersonData();

        ImageView imageView = root.findViewById(R.id.image_draw);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Создание и отображение фрагмента для редактирования значений персоны
                EditPersonValueFragment editFragment = new EditPersonValueFragment();

                // Получаем FragmentManager и начинаем транзакцию
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_main, editFragment) // Заменяем текущий фрагмент на ProductListFragment
                        .addToBackStack("home_fragment")
                        .commit(); // Применяем транзакцию
            }
        });
        //loadWeightChartData(); // Загрузка данных для графика веса


        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadWeightChartData(); // Загрузка данных для графика веса
        loadCaloriesSummaryChartData();
        setupTopCategoriesChart(); // Инициализация гистограммы топ-3 категорий
    }

    public void loadPersonData() {
        // Получаем данные из базы данных и устанавливаем их в TextView'ы
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToLast()) {
            // Получаем данные
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME));
            @SuppressLint("Range") int age = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_AGE));
            @SuppressLint("Range") float height = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COLUMN_HEIGHT));
            @SuppressLint("Range") float weight = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COLUMN_WEIGHT));
            @SuppressLint("Range") String gender = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_GENDER));
            @SuppressLint("Range") float activityLevel = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COLUMN_ACTIVITY_LEVEL));
            @SuppressLint("Range") float calNorm = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COLUMN_CAL_NORM)); // Получаем значение cal_norm из базы данных


            // Устанавливаем данные в TextView'ы
            personNameTextView.setText(name + "!");
            calorieNormTextView.setText(String.format(Locale.getDefault(), "%.2f ккал", calNorm)); // Устанавливаем значение из базы данных
            genderValueTextView.setText(gender);

            // Определяем, какое окончание использовать для слова "год"
            String ageSuffix = "лет";
            if (age % 10 == 1 && age % 100 != 11) {
                ageSuffix = "год";
            } else if ((age % 10 == 2 || age % 10 == 3 || age % 10 == 4) && (age % 100 < 10 || age % 100 >= 20)) {
                ageSuffix = "года";
            }

            // Устанавливаем данные в TextView для возраста с учетом окончания
            ageValueTextView.setText(String.format(Locale.getDefault(), "%d %s", age, ageSuffix));

            heightValueTextView.setText(String.format(Locale.getDefault(), "%.1f см.", height)); // Форматируем до одного знака после запятой
            weightValueTextView.setText(String.format(Locale.getDefault(), "%.1f кг.", weight)); // Форматируем до одного знака после запятой
            // Заменяем точку на запятую в значении переменной activityLevel
            // Заменяем точку на запятую в значении переменной activityLevel
            // Преобразовываем значение activityLevel в строку и заменяем точку на запятую
            String activityLevelString = String.valueOf(activityLevel).replace(".", ",");

            // Устанавливаем данные в TextView для уровня активности с измененной точкой на запятую
            activityValueTextView.setText(activityLevelString);
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
    }

    private void loadWeightChartData() {
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Запрос для получения данных о весе, отсортированных по дате в убывающем порядке
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME, new String[]{DatabaseHelper.COLUMN_WEIGHT, DatabaseHelper.COLUMN_DATE},
                null, null, null, null, DatabaseHelper.COLUMN_DATE + " ASC");

        ArrayList<Entry> weightEntries = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") float weight = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COLUMN_WEIGHT));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE));

                // Convert the date string to the desired format (dd.MM )
                try {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM", Locale.getDefault());
                    Date dateObject = inputFormat.parse(date);
                    date = outputFormat.format(dateObject);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // Добавляем вес и дату в соответствующие списки
                weightEntries.add(new Entry(weightEntries.size(), weight));
                dates.add(date);

                LineDataSet weightDataSet = new LineDataSet(weightEntries, "Вес");

                weightDataSet.setColor(Color.BLUE);
                weightDataSet.setCircleColor(Color.BLUE);

            } while (cursor.moveToNext());

        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();

        LineDataSet weightDataSet = new LineDataSet(weightEntries, "Вес, кг");
        weightDataSet.setColor(Color.BLUE);
        weightDataSet.setCircleColor(Color.BLUE);

        LineData lineData = new LineData(weightDataSet);

        LineChart weightChart = requireView().findViewById(R.id.weightChart);
        weightChart.setData(lineData);
        weightChart.getDescription().setEnabled(false);

        XAxis xAxis = weightChart.getXAxis();
        // Настройка цвета текста оси X
        xAxis.setTextColor(Color.WHITE);
        xAxis.setTextSize(14f); // Замените 12f на желаемый размер
        // Настройка цвета текста легенды (если она отображается)
        Legend legend = weightChart.getLegend();
        legend.setTextColor(Color.WHITE);
        legend.setTextSize(14f); // Замените 12f на желаемый размер

// Настройка цвета текста значений на графике
        weightDataSet.setValueTextColor(Color.WHITE);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // устанавливаем минимальный интервал между метками по оси X
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));
        xAxis.setLabelCount(dates.size()); // устанавливаем количество меток на оси X
        xAxis.setAvoidFirstLastClipping(true); // предотвращаем обрезку первой и последней меток

        // Получение объекта оси Y
        YAxis yAxis = weightChart.getAxisLeft();
        YAxis yAxisR = weightChart.getAxisRight();
        // Скрытие значений по оси Y
        yAxis.setDrawLabels(false);
        yAxisR.setDrawLabels(false);

// Настройка размера текста значений на графике
        weightDataSet.setValueTextSize(12f); // Замените 12f на желаемый размер
        weightChart.invalidate(); // Обновляем график
    }


    private void loadCaloriesSummaryChartData() {
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Запрос для получения данных о сумме калорий за день, отсортированных по дате в убывающем порядке
        Cursor cursor = db.query(DatabaseHelper.TABLE_CALORIES_SUMMARY_DAY, new String[]{DatabaseHelper.COLUMN_TOTAL_CALORIES_DAY, DatabaseHelper.COLUMN_DATE_DAY},
                null, null, null, null, DatabaseHelper.COLUMN_DATE_DAY);

        ArrayList<BarEntry> caloriesEntries = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") float totalCalories = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COLUMN_TOTAL_CALORIES_DAY));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE_DAY));

                // Преобразуем дату в нужный формат (дд-мм)
                SimpleDateFormat inputFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM", Locale.getDefault());
                try {
                    Date dateObject = inputFormat.parse(date);
                    date = outputFormat.format(dateObject);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // Проверяем, есть ли уже бар для этого дня
                int existingIndex = -1;
                for (int i = 0; i < dates.size(); i++) {
                    if (dates.get(i).equals(date)) {
                        existingIndex = i;
                        break;
                    }
                }

                if (existingIndex != -1) {
                    // Обновляем значение существующего бара
                    caloriesEntries.set(existingIndex, new BarEntry(existingIndex, totalCalories));
                } else {
                    // Добавляем новый бар
                    caloriesEntries.add(new BarEntry(caloriesEntries.size(), totalCalories));
                    dates.add(date);
                }

            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();

        BarDataSet caloriesDataSet = new BarDataSet(caloriesEntries, "Сумма калорий за день");
        caloriesDataSet.setColor(Color.BLUE);
        caloriesDataSet.setValueTextColor(Color.WHITE);
        caloriesDataSet.setValueTextSize(12f);

        BarData barData = new BarData(caloriesDataSet);

        BarChart caloriesChart = requireView().findViewById(R.id.caloriesChart);
        caloriesChart.setData(barData);
        caloriesChart.getDescription().setEnabled(false);
        caloriesDataSet.setColor(Color.WHITE); // Изменение цвета баров на красный

        XAxis xAxis = caloriesChart.getXAxis();
        xAxis.setTextColor(Color.WHITE); // Изменение цвета текста на оси X на желтый
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(12f);

        xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));
        xAxis.setLabelCount(dates.size());
        xAxis.setAvoidFirstLastClipping(true);

        YAxis yAxis = caloriesChart.getAxisLeft();
        yAxis.setTextColor(Color.WHITE);
        yAxis.setTextSize(12f);
        yAxis.setGridColor(Color.WHITE); // Изменение цвета сетки на серый
        YAxis yAxisR = caloriesChart.getAxisRight();
        // Скрытие значений по оси Y
        yAxis.setDrawLabels(false);

        yAxisR.setDrawLabels(false);
        Legend legend = caloriesChart.getLegend();
        legend.setTextColor(Color.WHITE); // Изменение цвета текста в легенде на зеленый
        legend.setTextSize(12f);
        caloriesChart.invalidate();
    }





    private void setupTopCategoriesChart() {
        // Получаем топ-3 категорий завтраков
        List<Pair<String, Integer>> breakfastCategories = new BreakfastDatabaseHelper(requireContext()).getTop3CategoriesWithCount();
        // Получаем топ-3 категорий обедов
        List<Pair<String, Integer>> lunchCategories = new LunchDatabaseHelper(requireContext()).getTop3CategoriesWithCount();
        // Получаем топ-3 категорий ужинов
        List<Pair<String, Integer>> dinnerCategories = new DinnerDatabaseHelper(requireContext()).getTop3CategoriesWithCount();
        // Получаем топ-3 категорий закусок
        List<Pair<String, Integer>> snackCategories = new SnackDatabaseHelper(requireContext()).getTop3CategoriesWithCount();

        // Объединяем все категории в один список
        List<Pair<String, Integer>> allCategories = new ArrayList<>();
        allCategories.addAll(breakfastCategories);
        allCategories.addAll(lunchCategories);
        allCategories.addAll(dinnerCategories);
        allCategories.addAll(snackCategories);

        // Подсчитываем общее количество продуктов для каждой категории
        Map<String, Integer> categoryCountMap = new HashMap<>();
        for (Pair<String, Integer> category : allCategories) {
            String categoryName = category.first;
            int categoryCount = category.second;
            categoryCountMap.put(categoryName, categoryCountMap.getOrDefault(categoryName, 0) + categoryCount);
        }

        // Сортируем категории по количеству продуктов
        List<Map.Entry<String, Integer>> sortedCategoryList = new ArrayList<>(categoryCountMap.entrySet());
        sortedCategoryList.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        // Создаем данные для гистограммы
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        // Добавляем топ-3 категорий в данные для гистограммы
        int count = Math.min(sortedCategoryList.size(), 3); // Выводим только топ-3 категорий
        for (int i = 0; i < count; i++) {
            Map.Entry<String, Integer> entry = sortedCategoryList.get(i);
            String categoryName = entry.getKey();
            int categoryCount = entry.getValue();
            entries.add(new BarEntry(i, categoryCount));
            labels.add(categoryName);
        }

        // Создаем набор данных для гистограммы
        BarDataSet dataSet = new BarDataSet(entries, "Top 3 Categories");
        dataSet.setColor(Color.rgb(0, 155, 0)); // Зеленый цвет для столбцов

        // Создаем объект данных для гистограммы
        BarData barData = new BarData(dataSet);
        class IntegerValueFormatter extends ValueFormatter {
            @Override
            public String getFormattedValue(float value) {
                // Преобразуем значение в целое число и возвращаем его в виде строки
                return String.valueOf((int) value);
            }
        }
        // Находим гистограмму по идентификатору в макете
        BarChart topCategoriesChart = requireView().findViewById(R.id.topCategoriesChart);
        barData.setValueFormatter(new IntegerValueFormatter());

        // Устанавливаем данные для гистограммы
        topCategoriesChart.setData(barData);
        topCategoriesChart.getDescription().setEnabled(false);
        dataSet.setColor(Color.WHITE); // Изменение цвета баров на красный
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);

        // Настраиваем ось X
        XAxis xAxis = topCategoriesChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(12f);
        xAxis.setLabelRotationAngle(-45); // Поворачиваем метки по оси X для лучшей читаемости
        xAxis.setDrawGridLines(false);

        // Настраиваем ось Y
        topCategoriesChart.getAxisLeft().setEnabled(false);
        topCategoriesChart.getAxisRight().setEnabled(false);
        topCategoriesChart.getLegend().setEnabled(false);
        topCategoriesChart.getDescription().setEnabled(false);


        YAxis yAxis = topCategoriesChart.getAxisLeft();

        yAxis.setTextColor(Color.WHITE);
        yAxis.setTextSize(12f);
        yAxis.setGridColor(Color.WHITE); // Изменение цвета сетки на серый
        YAxis yAxisR = topCategoriesChart.getAxisRight();
        yAxisR.setDrawLabels(false);
        Legend legend = topCategoriesChart.getLegend();
        legend.setTextColor(Color.WHITE); // Изменение цвета текста в легенде на зеленый
        legend.setTextSize(12f);

        // Обновляем гистограмму
        topCategoriesChart.invalidate();
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
